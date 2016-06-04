package org.colorcoding.bobas.test.repository;

import org.colorcoding.bobas.common.ICriteria;
import org.colorcoding.bobas.common.IOperationResult;
import org.colorcoding.bobas.common.OperationMessages;
import org.colorcoding.bobas.common.OperationResult;
import org.colorcoding.bobas.data.DateTime;
import org.colorcoding.bobas.ownership.OwnershipException;
import org.colorcoding.bobas.ownership.Permission;
import org.colorcoding.bobas.ownership.PermissionGroup;
import org.colorcoding.bobas.ownership.PermissionItem;
import org.colorcoding.bobas.ownership.PermissionValue;
import org.colorcoding.bobas.repository.BORepositoryServiceApplication;
import org.colorcoding.bobas.repository.InvalidTokenException;
import org.colorcoding.bobas.test.bo.ISalesOrder;
import org.colorcoding.bobas.test.bo.SalesOrder;

/**
 * 业务对象仓库（测试）
 * 
 * @author Niuren.Zhu
 *
 */
@PermissionGroup("Test") // 权限分组的名称
public class BORepositoryTest extends BORepositoryServiceApplication
		implements IBORepositoryTestSvc, IBORepositoryTestApp {

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

	@Permission(defaultValue = PermissionValue.available) // 权限标记，如果没有配置，则使用默认权限值
	public OperationMessages closeOrders(String token) {
		OperationMessages operationMessages = new OperationMessages();
		try {
			this.setCurrentUser(token);
			this.checkMethodPermissions();
		} catch (OwnershipException e) {
			operationMessages.setError(e);
		} catch (InvalidTokenException e) {
			operationMessages.setError(e);
		}
		return operationMessages;
	}

	@Permission(defaultValue = PermissionValue.unavailable) // 权限标记，如果没有配置，则使用默认权限值
	public OperationMessages openOrders(String token) {
		OperationMessages operationMessages = new OperationMessages();
		try {
			this.setCurrentUser(token);
			this.checkMethodPermissions();
		} catch (OwnershipException e) {
			operationMessages.setError(e);
		} catch (InvalidTokenException e) {
			operationMessages.setError(e);
		}
		return operationMessages;
	}

	public OperationResult<PermissionItem> getPermissions() {
		OperationResult<PermissionItem> operationResult = new OperationResult<PermissionItem>();
		try {
			operationResult.addResultObjects(super.getMethodPermissions());
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}
}
