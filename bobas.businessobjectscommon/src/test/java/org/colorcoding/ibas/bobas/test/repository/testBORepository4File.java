package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.repository.BORepository4File;
import org.colorcoding.ibas.bobas.repository.IBORepository4File;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

import junit.framework.TestCase;

public class testBORepository4File extends TestCase {

    public void testBOSave() {
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

        IBORepository4File boRepository = new BORepository4File();
        // boRepository.setRepositoryFolder("D:\\WorkTemp\\borepository");

        IOperationResult<?> operationResult = boRepository.saveEx(order);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);

        order.setCustomerName("宇宙无敌影业--");
        operationResult = boRepository.saveEx(order);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        order.delete();
        operationResult = boRepository.saveEx(order);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);

    }

    public void testBOFetch() {
        ICriteria criteria = new Criteria();
        criteria.setResultCount(100);
        IBORepository4File boRepository = new BORepository4File();
        // boRepository.setRepositoryFolder("D:\\WorkTemp\\borepository");

        IOperationResult<?> operationResult = boRepository.fetchEx(criteria, SalesOrder.class);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
    }

}
