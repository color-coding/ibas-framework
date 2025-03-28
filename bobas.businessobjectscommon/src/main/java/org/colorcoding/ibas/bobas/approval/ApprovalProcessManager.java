package org.colorcoding.ibas.bobas.approval;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.repository.ITransaction;

/**
 * 流程管理员（基类）
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcessManager {

	private ITransaction transaction;

	protected final ITransaction getTransaction() {
		return this.transaction;
	}

	final void setTransaction(ITransaction transaction) {
		this.transaction = transaction;
	}

	/**
	 * 创建审批流程
	 * 
	 * @param boCode 业务对象编码
	 * @return
	 */
	public abstract <T extends IProcessData> Iterator<ApprovalProcess<T>> createApprovalProcess(String boCode);

	/**
	 * 创建审批流程
	 * 
	 * @param processData 流程数据
	 * @return
	 */
	public abstract <T extends IProcessData> ApprovalProcess<T> createApprovalProcess(IProcessData processData);

	/**
	 * 加载审批流程数据
	 * 
	 * @param apData 待审批数据
	 * @return
	 */
	public abstract <T extends IProcessData> T loadProcessData(IApprovalData apData);

}
