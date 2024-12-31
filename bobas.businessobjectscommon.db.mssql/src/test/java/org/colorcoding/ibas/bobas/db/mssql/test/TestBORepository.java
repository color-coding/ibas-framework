package org.colorcoding.ibas.bobas.db.mssql.test;

import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class TestBORepository extends TestCase {

	public void testSaveBO() throws Exception {
		BORepositoryTest boRepository = new BORepositoryTest();
		SalesOrder order = new SalesOrder();
		order.setCustomerCode("'C00001");
		order.setCustomerName("宇宙无敌影业");
		SalesOrderItem item = order.getSalesOrderItems().create();
		item.setItemCode("T800;");
		item.setItemDescription("终结者机器人-T800");
		item.setQuantity(Decimals.valueOf(1));
		item.setPrice(Decimals.valueOf(999999.99));
		item = order.getSalesOrderItems().create();
		item.setItemCode("S001");
		item.setItemDescription("绝地武士-剑");
		item.setQuantity(Decimals.valueOf(2));
		item.setPrice(Decimals.valueOf(99.00));
		IOperationResult<?> operationResult = boRepository.saveSalesOrder(order, "");
		System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
				operationResult.getMessage(), operationResult.getResultObjects().size()));
		assertEquals(operationResult.getResultCode(), 0);
		order.setCustomerName("宇宙无敌影业--");
		order.getSalesOrderItems().get(0).delete();
		item = order.getSalesOrderItems().get(1);
		item.setQuantity(Decimals.valueOf(20));
		item = order.getSalesOrderItems().create();
		item.setItemCode("S003");
		item.setItemDescription("绝地武士-头盔");
		item.setQuantity(Decimals.valueOf(3));
		item.setPrice(Decimals.valueOf(299.00));
		operationResult = boRepository.saveSalesOrder(order, "");
		System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
				operationResult.getMessage(), operationResult.getResultObjects().size()));
		assertEquals(operationResult.getResultCode(), 0);
		order.delete();
		operationResult = boRepository.saveSalesOrder(order, "");
		System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
				operationResult.getMessage(), operationResult.getResultObjects().size()));
		assertEquals(operationResult.getResultCode(), 0);
		boRepository.close();
	}

}
