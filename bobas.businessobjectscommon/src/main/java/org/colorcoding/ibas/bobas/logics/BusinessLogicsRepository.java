package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.repository.BORepositoryLogicService;

/**
 * 业务逻辑执行仓库
 * 
 * @author Niuren.Zhu
 *
 */
class BusinessLogicsRepository extends BORepositoryLogicService {

	public BusinessLogicsRepository() {
		this.setRefetchBeforeDelete(false);// 删除前不替换数据
		this.setRefetchAfterSave(false);// 保存后不检索数据
		this.setCheckApprovalProcess(false);// 不检查审批逻辑
	}

	/**
	 * 保存对象
	 * 
	 * @param bo
	 *            业务对象
	 * @return
	 */
	public <P extends IBusinessObject> OperationResult<P> saveData(P bo) {
		String token = this.getCurrentUser().getToken();
		return super.save(bo, token);
	}
}
