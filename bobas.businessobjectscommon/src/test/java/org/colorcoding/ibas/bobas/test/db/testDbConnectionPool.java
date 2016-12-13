package org.colorcoding.ibas.bobas.test.db;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.repository.InvalidRepositoryException;
import org.colorcoding.ibas.bobas.repository.InvalidTokenException;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.User;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

/**
 * 测试数据库连接池
 * 
 * 思路，重新实现数据库相关内容，排除数据性能影响，检查资源回收是否正常
 * 
 * @author Niuren.Zhu
 *
 */
public class testDbConnectionPool extends TestCase {

    public void testFetchBO() throws InvalidRepositoryException, InvalidTokenException {
        MyConfiguration.addConfigValue(MyConfiguration.CONFIG_ITEM_DB_TYPE, "org.colorcoding.ibas.bobas.test.db");
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUserToken("");
        ICriteria criteria = new Criteria();
        criteria.setResultCount(100);
        // ("DocStatus" = 'P' OR "DocStatus" = 'F')
        ICondition condition = criteria.getConditions().create();
        condition.setBracketOpenNum(1);
        condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
        condition.setCondVal(emDocumentStatus.Planned);
        condition = criteria.getConditions().create();
        condition.setBracketCloseNum(1);
        condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
        condition.setCondVal(emDocumentStatus.Released);
        condition.setRelationship(ConditionRelationship.cr_OR);
        // ORDER BY "DocEntry" DESC, "CardCode" ASC
        ISort sort = criteria.getSorts().create();
        sort.setAlias(SalesOrder.DocEntryProperty.getName());
        sort.setSortType(SortType.st_Descending);
        sort = criteria.getSorts().create();
        sort.setAlias(SalesOrder.CustomerCodeProperty.getName());
        sort.setSortType(SortType.st_Ascending);

        IOperationResult<ISalesOrder> operationResult = boRepository.fetchSalesOrder(criteria);
        System.out.println(String.format("%s code:%s message:%s results:%s", Thread.currentThread().getName(),
                operationResult.getResultCode(), operationResult.getMessage(),
                operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        for (int i = 0; i < 3; i++) {
            operationResult = boRepository.fetchSalesOrder(criteria);
            System.out.println(String.format("%s code:%s message:%s results:%s", Thread.currentThread().getName(),
                    operationResult.getResultCode(), operationResult.getMessage(),
                    operationResult.getResultObjects().size()));
            assertEquals(operationResult.getResultCode(), 0);
        }
    }

    public void testSaveBO() throws InvalidRepositoryException, InvalidTokenException {
        MyConfiguration.addConfigValue(MyConfiguration.CONFIG_ITEM_DB_TYPE, "org.colorcoding.ibas.bobas.test.db");
        MyConfiguration.addConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_POST_TRANSACTION, "true");
        MyConfiguration.addConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_REFETCH, "true");
        MyConfiguration.addConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_VERSION_CHECK, "true");
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUserToken("");
        ISalesOrder order = new SalesOrder();
        order.setDocumentUser(new User());
        order.getDocumentUser().setUserCode(DateTime.getNow().toString("HHmmss") + "00");
        order.setTeamUsers(new User[] { new User(), new User() });
        order.getTeamUsers()[0].setUserCode(DateTime.getNow().toString("HHmmss") + "01");
        order.getTeamUsers()[1].setUserCode(DateTime.getNow().toString("HHmmss") + "02");
        order.setCustomerCode("C00001");
        order.setCustomerName("宇宙无敌影业");
        ISalesOrderItem item = order.getSalesOrderItems().create();
        item.setItemCode("T800");
        item.setItemDescription("终结者机器人-T800");
        item.setQuantity(1);
        item.setPrice(999999.99);
        item = order.getSalesOrderItems().create();
        item.setItemCode("S001");
        item.setItemDescription("绝地武士-剑");
        item.setQuantity(2);
        item.setPrice(99.00);
        IOperationResult<ISalesOrder> operationResult = boRepository.saveSalesOrder(order);
        System.out.println(String.format("%s code:%s message:%s results:%s", Thread.currentThread().getName(),
                operationResult.getResultCode(), operationResult.getMessage(),
                operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        order.setCustomerName("宇宙无敌影业--");
        order.getSalesOrderItems().get(0).delete();
        item = order.getSalesOrderItems().get(1);
        item.setQuantity(20);
        item = order.getSalesOrderItems().create();
        item.setItemCode("S003");
        item.setItemDescription("绝地武士-头盔");
        item.setQuantity(3);
        item.setPrice(299.00);
        operationResult = boRepository.saveSalesOrder(order);
        System.out.println(String.format("%s code:%s message:%s results:%s", Thread.currentThread().getName(),
                operationResult.getResultCode(), operationResult.getMessage(),
                operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        order.delete();
        operationResult = boRepository.saveSalesOrder(order);
        System.out.println(String.format("%s code:%s message:%s results:%s", Thread.currentThread().getName(),
                operationResult.getResultCode(), operationResult.getMessage(),
                operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
    }

    static boolean flagStop = false;

    public void testExtremeTask() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread ts = new Thread() {
                    @Override
                    public void run() {
                        testDbConnectionPool test = new testDbConnectionPool();
                        while (!flagStop) {
                            try {
                                test.testSaveBO();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Thread tf = new Thread() {
                    @Override
                    public void run() {
                        testDbConnectionPool test = new testDbConnectionPool();
                        while (!flagStop) {
                            try {
                                test.testFetchBO();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                tf.start();
                ts.start();
            }
            Thread.sleep(300000);
            flagStop = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
