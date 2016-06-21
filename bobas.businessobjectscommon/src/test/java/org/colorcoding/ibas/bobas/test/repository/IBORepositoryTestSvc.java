package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.repository.IBORepositoryApplication;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

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
    /**
     * 查询-物料主数据
     * @param criteria 查询
     * @param token 口令
     * @return 操作结果
     */
    OperationResult<Materials> fetchMaterials(ICriteria criteria, String token);

    /**
     * 保存-物料主数据
     * @param bo 对象实例
     * @param token 口令
     * @return 操作结果
     */
    OperationResult<Materials> saveMaterials(Materials bo, String token);
}
