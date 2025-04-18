package org.colorcoding.ibas.demo.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;
import org.colorcoding.ibas.demo.bo.materials.IMaterials;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.bo.materialsjournal.IMaterialsJournal;
import org.colorcoding.ibas.demo.bo.materialsjournal.MaterialsJournal;
import org.colorcoding.ibas.demo.bo.salesorder.ISalesOrder;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;
import org.colorcoding.ibas.demo.bo.user.IUser;
import org.colorcoding.ibas.demo.bo.user.User;

/**
 * TrainingTesting仓库
 */
public class BORepositoryTrainingTesting extends BORepositoryServiceApplication
		implements IBORepositoryTrainingTestingSvc, IBORepositoryTrainingTestingApp {

	public void skipLogics() {
		super.setSkipLogics(true);
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-物料主数据
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	public OperationResult<Materials> fetchMaterials(ICriteria criteria, String token) {
		return super.fetch(Materials.class, criteria, token);
	}

	/**
	 * 查询-物料主数据（提前设置用户口令）
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	public IOperationResult<IMaterials> fetchMaterials(ICriteria criteria) {
		return new OperationResult<IMaterials>(this.fetchMaterials(criteria, this.getUserToken()));
	}

	/**
	 * 保存-物料主数据
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	public OperationResult<Materials> saveMaterials(Materials bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-物料主数据（提前设置用户口令）
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IMaterials> saveMaterials(IMaterials bo) {
		return new OperationResult<IMaterials>(this.saveMaterials((Materials) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-销售订单
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	public OperationResult<SalesOrder> fetchSalesOrder(ICriteria criteria, String token) {
		return super.fetch(SalesOrder.class, criteria, token);
	}

	/**
	 * 查询-销售订单（提前设置用户口令）
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	public IOperationResult<ISalesOrder> fetchSalesOrder(ICriteria criteria) {
		return new OperationResult<ISalesOrder>(this.fetchSalesOrder(criteria, this.getUserToken()));
	}

	/**
	 * 保存-销售订单
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	public OperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-销售订单（提前设置用户口令）
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	public IOperationResult<ISalesOrder> saveSalesOrder(ISalesOrder bo) {
		return new OperationResult<ISalesOrder>(this.saveSalesOrder((SalesOrder) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	public OperationResult<User> fetchUser(ICriteria criteria, String token) {
		return super.fetch(User.class, criteria, token);
	}

	/**
	 * 查询-用户（提前设置用户口令）
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	public IOperationResult<IUser> fetchUser(ICriteria criteria) {
		return new OperationResult<IUser>(this.fetchUser(criteria, this.getUserToken()));
	}

	/**
	 * 保存-用户
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	public OperationResult<User> saveUser(User bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-用户（提前设置用户口令）
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IUser> saveUser(IUser bo) {
		return new OperationResult<IUser>(this.saveUser((User) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-仓库日记账
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	public OperationResult<MaterialsJournal> fetchMaterialsJournal(ICriteria criteria, String token) {
		return super.fetch(MaterialsJournal.class, criteria, token);
	}

	/**
	 * 查询-仓库日记账（提前设置用户口令）
	 * 
	 * @param criteria 查询
	 * @return 操作结果
	 */
	public IOperationResult<IMaterialsJournal> fetchMaterialsJournal(ICriteria criteria) {
		return new OperationResult<IMaterialsJournal>(this.fetchMaterialsJournal(criteria, this.getUserToken()));
	}

	/**
	 * 保存-仓库日记账
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	public OperationResult<MaterialsJournal> saveMaterialsJournal(MaterialsJournal bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-仓库日记账（提前设置用户口令）
	 * 
	 * @param bo 对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IMaterialsJournal> saveMaterialsJournal(IMaterialsJournal bo) {
		return new OperationResult<IMaterialsJournal>(
				this.saveMaterialsJournal((MaterialsJournal) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//

}
