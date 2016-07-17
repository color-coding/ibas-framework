package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.repository.IBORepositorySmartService;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.logics.PurchaseOrder;

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

	/**
	 * 查询-物料主数据
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	IOperationResult<IMaterials> fetchMaterials(ICriteria criteria);

	/**
	 * 保存-物料主数据
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	IOperationResult<IMaterials> saveMaterials(IMaterials bo);

	/**
	 * 查询-采购订单
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	IOperationResult<PurchaseOrder> fetchPurchaseOrder(ICriteria criteria);

	/**
	 * 保存-采购订单，仅内部可用
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	IOperationResult<PurchaseOrder> savePurchaseOrder(PurchaseOrder bo);

}
