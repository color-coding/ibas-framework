package org.colorcoding.ibas.bobas.approval;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.message.Logger;
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
	 * 开始审批流程
	 * 
	 * @param apData 待审批数据
	 * @return
	 */
	public <T extends IProcessData> ApprovalProcess<T> startProcess(IApprovalData apData) throws ApprovalException {
		ApprovalProcess<T> process;
		Iterator<ApprovalProcess<T>> processes = this.createApprovalProcess(apData.getObjectCode());
		while (processes != null && processes.hasNext()) {
			process = processes.next();
			process.setTransaction(this.getTransaction());
			if (process.start(apData)) {
				// 审批流程开始
				Logger.log("approval process: data [%s]'s approval process was started, name [%s].",
						apData.getIdentifiers(), process.getName());
				return process;
			}
		}
		return null;
	}

	/**
	 * 开始审批流程
	 * 
	 * @param processData 流程数据
	 * @return
	 */
	public <T extends IProcessData> ApprovalProcess<T> startProcess(T processData) throws ApprovalException {
		return this.startProcess(processData, null);
	}

	/**
	 * 开始审批流程
	 * 
	 * @param processData 流程数据
	 * @param apData      待审批数据
	 * @return
	 */
	public <T extends IProcessData> ApprovalProcess<T> startProcess(T processData, IApprovalData apData)
			throws ApprovalException {
		ApprovalProcess<T> process = this.createApprovalProcess(processData);
		if (process != null) {
			// 加载已存在审批流程
			process.setTransaction(this.getTransaction());
			if (apData != null) {
				process.setApprovalData(apData);
				Logger.log("approval process: data [%s] using approval process, name [%s].", apData.getIdentifiers(),
						process.getName());
			} else {
				Logger.log("approval process: using approval process, name [%s].", process.getName());
			}
		}
		return process;
	}

	/**
	 * 加载审批流程数据
	 * 
	 * @param apData 待审批数据
	 * @return
	 */
	public abstract IProcessData loadProcessData(IApprovalData apData) throws ApprovalException;

	/**
	 * 创建审批流程实例
	 * 
	 * @return
	 */
	protected abstract <T extends IProcessData> ApprovalProcess<T> createApprovalProcess(T processData);

	/**
	 * 创建对象的审批流程实例
	 * 
	 * @param boCode 对象类型
	 * @return
	 */
	protected abstract <T extends IProcessData> Iterator<ApprovalProcess<T>> createApprovalProcess(String boCode);

}
