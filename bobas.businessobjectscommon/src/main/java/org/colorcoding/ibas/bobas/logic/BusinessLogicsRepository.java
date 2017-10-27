package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
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

	public <P extends IBusinessObject> OperationResult<P> saveData(P bo) {
		String token = this.getCurrentUser().getToken();
		return super.save(bo, token);
	}

	public <P extends IBusinessObject> OperationResult<P> fetchData(ICriteria criteria, Class<P> boType) {
		String token = this.getCurrentUser().getToken();
		return super.fetch(criteria, token, boType);
	}
}
