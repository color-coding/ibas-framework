package org.colorcoding.ibas.bobas.test;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.rule.BusinessRulesManager;
import org.colorcoding.ibas.bobas.rule.IBusinessRules;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;

import junit.framework.TestCase;

public class TestRules extends TestCase {

	public void testCommons() {

		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentDate(DateTimes.today());
		salesOrder.setDocumentStatus(emDocumentStatus.RELEASED);
		salesOrder.setDocumentTotal(Decimals.valueOf("99.99"));
		salesOrder.setActivated(true);
		salesOrder.setCustomerCode("C00001");

		SalesOrderItem orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(BigDecimal.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(99.99));
		orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(BigDecimal.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));

		// 执行业务逻辑方法
		Consumer<IBusinessObject> executeRules = new Consumer<IBusinessObject>() {

			@Override
			public void accept(IBusinessObject bo) {
				// 获取业务规则
				IBusinessRules boRules = BusinessRulesManager.create().getRules(bo.getClass());
				if (boRules != null) {
					// 业务逻辑
					boRules.execute(bo);
				}
			}
		};

		// 测试子项状态变化，父项状态
		emDocumentStatus changedDocument = emDocumentStatus.FINISHED;
		for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
			item.setLineStatus(changedDocument);
		}
		// 首先执行子项规则
		BOUtilities.traverse(salesOrder, (data) -> {
			// 加载状态不执行逻辑
			if (data.isLoading()) {
				return;
			}
			// 执行规则
			executeRules.accept(data);
		});
		// 执行自身规则
		executeRules.accept(salesOrder);

		// 测试属性值
		BigDecimal total = Decimals.VALUE_ZERO;
		for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
			assertEquals("Property [DocumentStatus] faild. ", item.getLineStatus(), changedDocument);
			assertEquals("Property [LineTotal] faild. ", item.getLineTotal(),
					Decimals.multiply(item.getPrice(), item.getQuantity()));
			total = Decimals.add(total, item.getLineTotal());
		}

		assertEquals("Property [DocumentTotal] faild. ", salesOrder.getDocumentTotal(), total);
		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), changedDocument);

		// 测试父项变化，子项状态
		salesOrder.setCanceled(emYesNo.YES);
		for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
			assertEquals("Property [Canceled] faild. ", item.getCanceled(), salesOrder.getCanceled());
		}
	}
}
