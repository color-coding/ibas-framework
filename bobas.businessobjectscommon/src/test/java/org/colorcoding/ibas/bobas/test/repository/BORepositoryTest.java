package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;

public class BORepositoryTest extends BORepositoryServiceApplication {

	public IOperationResult<SalesOrder> fetchSalesOrder(ICriteria criteria, String token) {
		return super.fetch(criteria, SalesOrder.class, token);
	}

	public IOperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, String token) {
		return super.save(bo, token);
	}

}
