package org.colorcoding.ibas.bobas.db.mssql.test;

import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.bo.IUserField;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.ComputeException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.repository.BORepository4DbReadonly;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;
import org.colorcoding.ibas.bobas.repository.InvalidRepositoryException;
import org.colorcoding.ibas.bobas.repository.InvalidTokenException;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.IUser;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.SalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.User;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testBORepository extends TestCase {

    public boolean details_out = true;

    public void testCriteria() throws InvalidRepositoryException, InvalidTokenException {
        ICriteria criteria = new Criteria();
        criteria.setNotLoadedChildren(true);
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

        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUserToken("");
        IOperationResult<?> operationResult = boRepository.fetchSalesOrder(criteria);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));

        criteria.setNotLoadedChildren(false);
        // 查询子项
        IChildCriteria childCriteria = criteria.getChildCriterias().create();
        childCriteria.setPropertyPath(SalesOrder.SalesOrderItemsProperty.getName());
        // childCriteria.setFatherMustHasResluts(false);//父项必须有返回值
        condition = childCriteria.getConditions().create();
        condition.setAlias(SalesOrderItem.LineStatusProperty.getName());
        condition.setCondVal(emDocumentStatus.Finished);

        boRepository = new BORepositoryTest();// 重新构造后缓存失效
        boRepository.setUserToken("");
        operationResult = boRepository.fetchSalesOrder(criteria);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));

    }

    public void testConnectBORepository() throws InvalidTokenException, InvalidRepositoryException {
        // System.out.println(System.getProperty("java.class.path"));
        // System.out.println(System.getProperty("user.dir"));
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUserToken("");
        DateTime dateTime = boRepository.getServerTime();
        System.out.println(dateTime.toString());
        boRepository = new BORepositoryTest();
        boRepository.connectRepository("MSSQL", "localhost", "ibas_demo", "sa", "1q2w3e");
        dateTime = boRepository.getServerTime();
        System.out.println(dateTime.toString());
    }

    public void testBORepositoryTest() throws InvalidTokenException {
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUserToken("");
        ISalesOrder order = new SalesOrder();
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
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);

        ICriteria criteria = order.getCriteria();// new Criteria();
        criteria.setResultCount(1);
        ISort sort = criteria.getSorts().create();
        sort.setAlias(SalesOrder.DocEntryProperty.getName());
        sort.setSortType(SortType.st_Descending);

        operationResult = boRepository.fetchSalesOrder(criteria);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);

    }

    public void testFetchBO() throws InvalidRepositoryException, InvalidTokenException {
        BORepositoryTest boRepository = new BORepositoryTest();
        // boRepository.connectRepository("MSSQL", "localhost", "ibas_demo",
        // "sa", "1q2w3e");
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

        IOperationResult<?> operationResult = boRepository.fetchSalesOrder(criteria);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);

        for (int i = 0; i < 3; i++) {
            operationResult = boRepository.fetchSalesOrder(criteria);
            System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                    operationResult.getMessage(), operationResult.getResultObjects().size()));
            assertEquals(operationResult.getResultCode(), 0);
        }

        if (details_out) {
            for (Object item : operationResult.getResultObjects()) {
                if (item instanceof IBOUserFields) {
                    IBOUserFields userFields = (IBOUserFields) item;
                    System.out.println(String.format("%s user fields count:%s", item.toString(),
                            userFields.getUserFields().size()));
                    for (IUserField field : userFields.getUserFields()) {
                        System.out.println(
                                String.format(" %s %s %s", field.getName(), field.getValue(), field.getValueType()));
                    }
                }
                System.out.println(String.format("%s complex field value:%s", item.toString(),
                        ((ISalesOrder) item).getCycle().toString()));
            }

        }
    }

    public void testSaveBO() throws InvalidRepositoryException, InvalidTokenException {
        BORepositoryTest boRepository = new BORepositoryTest();
        // boRepository.connectRepository("MSSQL", "localhost", "ibas_demo",
        // "sa", "1q2w3e");
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
        IOperationResult<?> operationResult = boRepository.saveSalesOrder(order);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
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
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        order.delete();
        operationResult = boRepository.saveSalesOrder(order);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
    }

    public void testBOAssociations() throws InvalidRepositoryException, InvalidTokenException {
        BORepositoryTest boRepository = new BORepositoryTest();
        // boRepository.connectRepository("MSSQL", "localhost", "ibas_demo",
        // "sa", "1q2w3e");
        boRepository.setUserToken("");
        ICriteria criteria = new Criteria();

        IOperationResult<?> operationResult = boRepository.fetchSalesOrder(criteria);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        ISalesOrder order = (ISalesOrder) operationResult.getResultObjects().firstOrDefault();
        System.out.println(order.toString("xml"));
        IUser documentUser = order.getDocumentUser();
        if (documentUser != null) {
            System.out.println(String.format("user:%s ", documentUser));
        }
        IUser[] teamUsers = order.getTeamUsers();
        if (teamUsers != null) {
            System.out.println(String.format("team user count:%s ", teamUsers.length));
        }
    }

    static boolean flagStop = false;

    public void testExtremeTask() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread ts = new Thread() {
                    @Override
                    public void run() {
                        testBORepository test = new testBORepository();
                        test.details_out = false;
                        while (!flagStop) {
                            try {
                                test.testSaveBO();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                ts.start();
                Thread tf = new Thread() {
                    @Override
                    public void run() {
                        testBORepository test = new testBORepository();
                        test.details_out = false;
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
            }
            Thread.sleep(50000);
            flagStop = true;
            System.gc();
            System.out.println("all done.");
            Thread.sleep(500000);// 继续等待，资源释放
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testQuery() {
        IBORepository4DbReadonly boRepository = new BORepository4DbReadonly();
        ISqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setQueryString("select * from cc_tt_ordr");

        IOperationResult<IDataTable> operationResult = boRepository.query(sqlQuery);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        System.out.println(operationResult.getResultObjects().firstOrDefault().toString("json"));

    }

    public void testWriteBO2DB() throws ComputeException, RepositoryException {
        /**
         * 测试写入速度，建议添加以下配置项
         * 
         * <add key="DisabledRefetch" value="true" />
         * <add key="DisabledBusinessLogics" value="true" />
         * <add key="DisabledBusinessApproval" value="true" />
         * <add key="DisabledCache" value="true" />
         * <add key="DisabledVersionCheck" value="true" />
         * <add key="DisabledPostTransaction" value="true" />
         * <add key="EnabledUserFields" value="false" />
         */
        BORepositoryTest boRepository = new BORepositoryTest();
        // boRepository.beginTransaction();
        DateTime start = DateTime.getNow();
        int count = 1000;
        for (int i = 0; i < count; i++) {
            SalesOrder order = new SalesOrder();
            order.setCustomerName("大量数据写入");
            ISalesOrderItem line = order.getSalesOrderItems().create();
            line.setItemDescription("鬼知道的物料1");
            line = order.getSalesOrderItems().create();
            line.setItemDescription("鬼知道的物料2");
            IOperationResult<?> operationResult = boRepository.saveSalesOrder(order);
            if (operationResult.getResultCode() != 0) {
                System.err.println(operationResult.getMessage());
                break;
            }
        }
        // boRepository.commitTransaction();
        // boRepository.dispose();
        DateTime finish = DateTime.getNow();
        System.out.println(String.format("写入[%s]条数据，从[%s]到[%s]共[%s]秒，平均%s条/秒。", count, start.toString("HH:mm:ss"),
                finish.toString("HH:mm:ss"), DateTime.interval(start, finish, emTimeUnit.second),
                count / DateTime.interval(start, finish, emTimeUnit.second)));
        SalesOrder order = new SalesOrder();
        order.setCustomerName("大量数据写入");
        ISalesOrderItem line = order.getSalesOrderItems().create();
        line.setItemDescription("鬼知道的物料1");
        line = order.getSalesOrderItems().create();
        line.setItemDescription("鬼知道的物料2");
        IOperationResult<?> operationResult = boRepository.saveSalesOrder(order);
        if (operationResult.getResultCode() != 0) {
            System.err.println(operationResult.getMessage());
        }
        line = order.getSalesOrderItems().create();
        line.setItemDescription("鬼知道的物料3");
        line = order.getSalesOrderItems().create();
        line.setItemDescription("鬼知道的物料4");
        operationResult = boRepository.saveSalesOrder(order);
        if (operationResult.getResultCode() != 0) {
            System.err.println(operationResult.getMessage());
        }
    }

}
