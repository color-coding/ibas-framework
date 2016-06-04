package org.colorcoding.bobas.test.repository;

import org.colorcoding.bobas.common.ICriteria;
import org.colorcoding.bobas.common.OperationResult;
import org.colorcoding.bobas.repository.IBORepositoryApplication;
import org.colorcoding.bobas.test.bo.SalesOrder;

/**
 * 测试业务仓库服务
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepositoryTestSvc extends IBORepositoryApplication {
	/**
	 * 查询-销售订单
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	OperationResult<SalesOrder> fetchSalesOrder(ICriteria criteria, String token);

	/**
	 * 保存-销售订单，仅内部可用
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	OperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, String token);
}
