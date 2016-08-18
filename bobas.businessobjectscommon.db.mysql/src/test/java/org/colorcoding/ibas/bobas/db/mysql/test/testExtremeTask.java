package org.colorcoding.ibas.bobas.db.mysql.test;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.ComputeException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testExtremeTask extends TestCase {

	private void writeBO2DB() throws ComputeException {
		// 向数据库中写入100万数据
		BORepositoryTest boRepository = new BORepositoryTest();
		DateTime start = DateTime.getNow();
		int count = 1000;
		for (int i = 0; i < count; i++) {
			SalesOrder order = new SalesOrder();
			order.setCustomerName("大量数据写入");
			ISalesOrderItem line = order.getSalesOrderItems().create();
			line.setItemDescription("鬼知道的物料");

			IOperationResult<?> operationResult = boRepository.saveSalesOrder(order);
			if (operationResult.getResultCode() != 0) {
				System.err.println(operationResult.getMessage());
				break;
			}
		}
		DateTime finish = DateTime.getNow();
		System.out.println(String.format("写入[%s]条数据，从[%s]到[%s]共[%s]秒。", count, start.toString("HH:mm:ss"),
				finish.toString("HH:mm:ss"), DateTime.interval(start, finish, emTimeUnit.second)));
	}

	public void testDataTable2BO() throws ComputeException {
		// this.writeBO2DB();// 写入大量测试数据
		BORepositoryTest boRepository = new BORepositoryTest();
		ICriteria criteria = new Criteria();
		criteria.setResultCount(10000);
		DateTime start = DateTime.getNow();
		IOperationResult<?> operationResult = boRepository.fetchSalesOrder(criteria);
		DateTime finish = DateTime.getNow();
		System.out.println(
				String.format("查询[%s]条数据，从[%s]到[%s]共[%s]秒。", criteria.getResultCount(), start.toString("HH:mm:ss"),
						finish.toString("HH:mm:ss"), DateTime.interval(start, finish, emTimeUnit.second)));
		if (operationResult.getResultCode() != 0) {
			System.err.println(operationResult.getMessage());
		}
	}
}
