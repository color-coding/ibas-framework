package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.core.IBORepository;

/**
 * 审批流程
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcess {

	/**
	 * 保存审批流程
	 * 
	 * @param boRepository
	 *            保存到的业务仓库
	 */
	void save(IBORepository boRepository);
}
