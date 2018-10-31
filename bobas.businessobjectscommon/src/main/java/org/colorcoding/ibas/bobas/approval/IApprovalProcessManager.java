package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.core.IBORepository;

/**
 * 审批流程管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcessManager {

	/**
	 * 使用业务对象仓库
	 * 
	 * @param boRepository 仓库
	 */
	void useRepository(IBORepository boRepository);

	/**
	 * 检查并开启流程
	 * 
	 * @param data 判断是否需要审批的数据
	 * @return 流程实例或null;
	 */
	IApprovalProcess checkProcess(IApprovalData data);
}
