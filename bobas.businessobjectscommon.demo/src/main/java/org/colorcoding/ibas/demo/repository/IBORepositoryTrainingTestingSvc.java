package org.colorcoding.ibas.demo.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.repository.IBORepositorySmartService;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.bo.materialsjournal.MaterialsJournal;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;
import org.colorcoding.ibas.demo.bo.user.User;

/**
 * TrainingTesting仓库服务
 */
public interface IBORepositoryTrainingTestingSvc extends IBORepositorySmartService {

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-物料主数据
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	OperationResult<Materials> fetchMaterials(ICriteria criteria, String token);

	/**
	 * 保存-物料主数据
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<Materials> saveMaterials(Materials bo, String token);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-销售订单
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	OperationResult<SalesOrder> fetchSalesOrder(ICriteria criteria, String token);

	/**
	 * 保存-销售订单
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, String token);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	OperationResult<User> fetchUser(ICriteria criteria, String token);

	/**
	 * 保存-用户
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<User> saveUser(User bo, String token);

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-仓库日记账
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	OperationResult<MaterialsJournal> fetchMaterialsJournal(ICriteria criteria, String token);

	/**
	 * 保存-仓库日记账
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	OperationResult<MaterialsJournal> saveMaterialsJournal(MaterialsJournal bo, String token);

	// --------------------------------------------------------------------------------------------//

}
