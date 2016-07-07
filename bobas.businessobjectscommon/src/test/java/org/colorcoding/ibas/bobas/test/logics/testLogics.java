package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.repository.BORepository4File;
import org.colorcoding.ibas.bobas.repository.IBORepository4File;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testLogics extends TestCase {

	public void testMaterialsOrderQuantity() {
		// 创建物料数据
		IMaterials materials01 = new Materials();
		materials01.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
		materials01.setItemDescription("CPU i7");
		IMaterials materials02 = new Materials();
		materials02.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
		materials02.setItemDescription("Disk 5T");
		// 保存物料到文件系统
		IBORepository4File fileRepository = new BORepository4File();
		//fileRepository.setRepositoryFolder("D:\\WorkTemp\\borepository");
		BORepositoryTest boRepository = new BORepositoryTest();
		boRepository.setRepository(fileRepository);
		IOperationResult<?> operationResult;

		operationResult = boRepository.saveMaterials(materials01);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
		operationResult = boRepository.saveMaterials(materials02);
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

		// 创建销售订单
		ISalesOrder order = new SalesOrder();
		order.setCustomerCode("C00001");
		order.setCustomerName("宇宙无敌影业");
		ISalesOrderItem item = order.getSalesOrderItems().create();
		item.setItemCode(materials01.getItemCode());
		item.setItemDescription(materials01.getItemDescription());
		item.setQuantity(1);
		item.setPrice(999999.99);
		item = order.getSalesOrderItems().create();
		item.setItemCode(materials02.getItemCode());
		item.setItemDescription(materials02.getItemDescription());
		item.setQuantity(2);
		item.setPrice(99.00);

		operationResult = boRepository.saveSalesOrder(order);
		if (operationResult.getResultCode() != 0) {
			System.err.println(operationResult.getMessage());
		}
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
	}
}
