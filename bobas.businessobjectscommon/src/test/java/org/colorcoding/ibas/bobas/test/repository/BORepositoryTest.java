package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;

public class BORepositoryTest extends BORepositoryServiceApplication {

	public OperationResult<SalesOrder> fetchSalesOrder(ICriteria criteria, String token) {
		return new OperationResult<>(super.fetch(criteria, SalesOrder.class, token));
	}

	public OperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, String token) {
		return new OperationResult<>(super.save(bo, token));
	}

}
