package org.colorcoding.ibas.demo.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.repository.IBORepositoryApplication;
import org.colorcoding.ibas.demo.bo.materials.IMaterials;
import org.colorcoding.ibas.demo.bo.materialsjournal.IMaterialsJournal;
import org.colorcoding.ibas.demo.bo.salesorder.ISalesOrder;
import org.colorcoding.ibas.demo.bo.user.IUser;

/**
 * TrainingTesting仓库应用
 */
public interface IBORepositoryTrainingTestingApp extends IBORepositoryApplication {

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-物料主数据
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	IOperationResult<IMaterials> fetchMaterials(ICriteria criteria);

	/**
	 * 保存-物料主数据
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	IOperationResult<IMaterials> saveMaterials(IMaterials bo);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-销售订单
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	IOperationResult<ISalesOrder> fetchSalesOrder(ICriteria criteria);

	/**
	 * 保存-销售订单
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	IOperationResult<ISalesOrder> saveSalesOrder(ISalesOrder bo);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	IOperationResult<IUser> fetchUser(ICriteria criteria);

	/**
	 * 保存-用户
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	IOperationResult<IUser> saveUser(IUser bo);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-仓库日记账
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	IOperationResult<IMaterialsJournal> fetchMaterialsJournal(ICriteria criteria);

	/**
	 * 保存-仓库日记账
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	IOperationResult<IMaterialsJournal> saveMaterialsJournal(IMaterialsJournal bo);

	// --------------------------------------------------------------------------------------------//

}
