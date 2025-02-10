package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.BORepositoryServiceApplication;

/**
 * 审批流程专用业务仓库
 * 
 * @author Niuren.Zhu
 *
 */
class ApprovalProcessRepository extends BORepositoryServiceApplication {

	/**
	 * 查询对象（涉及BOCode解析需要提前加载class）
	 * 
	 * @param criteria 条件，需要指定BOCode
	 * @return
	 */
	public <P extends IBusinessObject> IOperationResult<P> fetchData(ICriteria criteria) {
		try {
			// 加载命名空间的类
			Class<?> boClass = BOFactory.classOf(criteria.getBusinessObject());
			if (boClass == null) {
				throw new ClassNotFoundException(
						I18N.prop("msg_bobas_not_found_bo_class", criteria.getBusinessObject()));
			}
			@SuppressWarnings("unchecked")
			Class<P> boType = (Class<P>) boClass;
			return super.fetch(criteria, boType);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param bo 业务对象
	 * @return
	 */
	public <P extends IBusinessObject> IOperationResult<P> saveData(P bo) {
		return super.save(bo);
	}
}
