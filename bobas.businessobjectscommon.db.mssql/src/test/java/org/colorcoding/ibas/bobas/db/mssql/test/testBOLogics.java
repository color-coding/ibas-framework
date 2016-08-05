package org.colorcoding.ibas.bobas.db.mssql.test;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testBOLogics extends TestCase {

	public void testSalesOrderLogics() {

		BORepositoryTest boRepository = new BORepositoryTest();

		String sign = String.valueOf(DateTime.getNow().getTime());
		IMaterials material_1 = new Materials();
		material_1.setItemCode(String.format("A%s", sign));
		material_1.setItemDescription(String.format("CPU [%s]", sign));
		IOperationResult<?> operationResult = boRepository.saveMaterials(material_1);
		System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
				operationResult.getMessage(), operationResult.getResultObjects().size()));
		assertEquals(operationResult.getResultCode(), 0);
		sign = String.valueOf(DateTime.getNow().getTime());
		IMaterials material_2 = new Materials();
		material_2.setItemCode(String.format("A%s", sign));
		material_2.setItemDescription(String.format("CPU [%s]", sign));
		operationResult = boRepository.saveMaterials(material_2);
		System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
				operationResult.getMessage(), operationResult.getResultObjects().size()));
		assertEquals(operationResult.getResultCode(), 0);

		SalesOrder order = new SalesOrder();

		order.setCustomerCode("C00001");
		order.setDocumentStatus(emDocumentStatus.Released);

		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode(material_1.getItemCode());
		orderItem.setItemDescription(material_1.getItemDescription());
		orderItem.setQuantity(new Decimal(2));
		orderItem.setPrice(new Decimal(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode(material_2.getItemCode());
		orderItem.setItemDescription(material_2.getItemDescription());
		orderItem.setQuantity(1);
		orderItem.setPrice(199.99);

		operationResult = boRepository.saveSalesOrder(order);
		System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
				operationResult.getMessage(), operationResult.getResultObjects().size()));
		assertEquals(operationResult.getResultCode(), 0);

	}
}
