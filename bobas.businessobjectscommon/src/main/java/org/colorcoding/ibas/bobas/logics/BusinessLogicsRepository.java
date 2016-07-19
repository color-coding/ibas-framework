package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.repository.BORepositoryLogicService;

/**
 * 业务逻辑执行仓库
 * 
 * @author Niuren.Zhu
 *
 */
class BusinessLogicsRepository extends BORepositoryLogicService {

	public IOperationResult<?> save(IBusinessObjectBase bo) {
		return super.save(bo, null);
	}
}
