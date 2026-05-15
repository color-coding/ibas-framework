package org.colorcoding.ibas.bobas.approval;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.repository.ITransaction;

/**
 * 审批流程管理器（基类），负责创建和启动审批流程
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
	 * 根据审批数据启动审批流程，依次尝试匹配的流程，首个满足条件的流程启动后返回
	 *
	 * @param apData 待审批数据
	 * @return 成功启动的审批流程，无匹配流程时返回null
	 * @throws ApprovalException 审批异常
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
	 * 根据已有流程数据启动审批流程
	 *
	 * @param processData 流程数据
	 * @return 审批流程实例，无匹配时返回null
	 * @throws ApprovalException 审批异常
	 */
	public <T extends IProcessData> ApprovalProcess<T> startProcess(T processData) throws ApprovalException {
		return this.startProcess(processData, null);
	}

	/**
	 * 根据已有流程数据和审批数据启动审批流程
	 *
	 * @param processData 流程数据
	 * @param apData      审批数据，可为null
	 * @return 审批流程实例，无匹配时返回null
	 * @throws ApprovalException 审批异常
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
	 * @return 流程数据
	 * @throws ApprovalException 审批异常
	 */
	public abstract IProcessData loadProcessData(IApprovalData apData) throws ApprovalException;

	/**
	 * 根据流程数据创建审批流程实例
	 *
	 * @param processData 流程数据
	 * @return 审批流程实例
	 */
	protected abstract <T extends IProcessData> ApprovalProcess<T> createApprovalProcess(T processData);

	/**
	 * 根据业务对象类型创建审批流程实例迭代器
	 *
	 * @param boCode 业务对象编码
	 * @return 审批流程迭代器，无匹配时可能返回null
	 */
	protected abstract <T extends IProcessData> Iterator<ApprovalProcess<T>> createApprovalProcess(String boCode);

}