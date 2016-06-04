package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.repository.IBORepositorySmartService;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;

/**
 * 测试业务仓库应用
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepositoryTestApp extends IBORepositorySmartService {
	/**
	 * 查询-销售订单
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	IOperationResult<ISalesOrder> fetchSalesOrder(ICriteria criteria);

	/**
	 * 保存-销售订单，仅内部可用
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	IOperationResult<ISalesOrder> saveSalesOrder(ISalesOrder bo);
}
