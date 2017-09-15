package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.logics.MaterialsQuantityJournal;
import org.colorcoding.ibas.bobas.test.logics.PurchaseOrder;

/**
 * 业务对象仓库（测试）
 * 
 * @author Niuren.Zhu
 *
 */
public class BORepositoryTest extends BORepositoryServiceApplication
		implements IBORepositoryTestSvc, IBORepositoryTestApp {

	public boolean beginTransaction() throws RepositoryException {
		return super.beginTransaction();
	}

	public void commitTransaction() throws RepositoryException {
		super.commitTransaction();
	}

	/**
	 * 获取服务器时间
	 * 
	 * @return
	 */
	public DateTime getServerTime() {
		return this.getRepository().getServerTime();
	}
	// --------------------------------------------------------------------------------------------//

	/**
	 * 查询-销售订单
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	@Override
	public OperationResult<SalesOrder> fetchSalesOrder(ICriteria criteria, String token) {
		return super.fetch(criteria, token, SalesOrder.class);
	}

	/**
	 * 查询-销售订单
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	@Override
	public IOperationResult<ISalesOrder> fetchSalesOrder(ICriteria criteria) {
		return new OperationResult<ISalesOrder>(this.fetchSalesOrder(criteria, this.getUserToken()));
	}

	/**
	 * 保存-销售订单
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	@Override
	public OperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-销售订单，仅内部可用
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	@Override
	public IOperationResult<ISalesOrder> saveSalesOrder(ISalesOrder bo) {
		return new OperationResult<ISalesOrder>(this.saveSalesOrder((SalesOrder) bo, this.getUserToken()));
	}

	/**
	 * 查询-物料主数据
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<Materials> fetchMaterials(ICriteria criteria, String token) {
		return super.fetch(criteria, token, Materials.class);
	}

	/**
	 * 查询-物料主数据（提前设置用户口令）
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	public IOperationResult<IMaterials> fetchMaterials(ICriteria criteria) {
		return new OperationResult<IMaterials>(this.fetchMaterials(criteria, this.getUserToken()));
	}

	/**
	 * 保存-物料主数据
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<Materials> saveMaterials(Materials bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-物料主数据（提前设置用户口令）
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	public IOperationResult<IMaterials> saveMaterials(IMaterials bo) {
		return new OperationResult<IMaterials>(this.saveMaterials((Materials) bo, this.getUserToken()));
	}

	// --------------------------------------------------------------------------------------------//

	/**
	 * 查询-采购订单
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<PurchaseOrder> fetchPurchaseOrder(ICriteria criteria, String token) {
		return super.fetch(criteria, token, PurchaseOrder.class);
	}

	/**
	 * 查询-采购订单
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	@Override
	public IOperationResult<PurchaseOrder> fetchPurchaseOrder(ICriteria criteria) {
		return new OperationResult<PurchaseOrder>(this.fetchPurchaseOrder(criteria, this.getUserToken()));
	}

	/**
	 * 保存-采购订单
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<PurchaseOrder> savePurchaseOrder(PurchaseOrder bo, String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-采购订单，仅内部可用
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	@Override
	public IOperationResult<PurchaseOrder> savePurchaseOrder(PurchaseOrder bo) {
		return new OperationResult<PurchaseOrder>(this.savePurchaseOrder((PurchaseOrder) bo, this.getUserToken()));
	}// --------------------------------------------------------------------------------------------//

	/**
	 * 查询-物料交易记录
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<MaterialsQuantityJournal> fetchMaterialsQuantityJournal(ICriteria criteria, String token) {
		return super.fetch(criteria, token, MaterialsQuantityJournal.class);
	}

	/**
	 * 查询-物料交易记录
	 * 
	 * @param criteria
	 *            查询
	 * @return 操作结果
	 */
	public IOperationResult<MaterialsQuantityJournal> fetchMaterialsQuantityJournal(ICriteria criteria) {
		return new OperationResult<MaterialsQuantityJournal>(
				this.fetchMaterialsQuantityJournal(criteria, this.getUserToken()));
	}

	/**
	 * 保存-物料交易记录
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	public OperationResult<MaterialsQuantityJournal> saveMaterialsQuantityJournal(MaterialsQuantityJournal bo,
			String token) {
		return super.save(bo, token);
	}

	/**
	 * 保存-物料交易记录，仅内部可用
	 * 
	 * @param bo
	 *            对象实例
	 * @return 操作结果
	 */
	public IOperationResult<MaterialsQuantityJournal> saveMaterialsQuantityJournal(MaterialsQuantityJournal bo) {
		return new OperationResult<MaterialsQuantityJournal>(
				this.saveMaterialsQuantityJournal((MaterialsQuantityJournal) bo, this.getUserToken()));
	}
}
