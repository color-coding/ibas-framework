package org.colorcoding.ibas.bobas.approval;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.logging.Logger;
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
		Objects.requireNonNull(transaction);
		this.transaction = transaction;
	}

	private List<IApprovalProcess> myProcesses;

	public IApprovalProcess checkProcess(IApprovalData data) {
		if (data == null) {
			return null;
		}
		for (IApprovalProcess item : this.myProcesses) {
			if (item.getApprovalData() == data) {
				return item;
			}
		}
		// 已保存的，仅看处理过的（没匹配的保存后又进行匹配）
		if (data.isDirty() == false) {
			return null;
		}
		IApprovalProcess process = null;
		if (!data.isNew()) {
			// 不是新建查看是否存在审批
			process = this.loadApprovalProcess(data.getIdentifiers());
			if (process != null) {
				process.setRepository(this.getRepository());
				process.setApprovalData(data);
			}
		}
		// 创建审批流程并尝试开始
		if (process == null && this.checkDataStatus(data)) {
			Iterator<IApprovalProcess> processes = this.createApprovalProcess(data.getObjectCode());
			while (processes != null && processes.hasNext()) {
				process = processes.next();
				process.setRepository(this.getRepository());
				if (process.start(data)) {
					Logger.log("approval process: data [%s]'s approval process was started, name [%s].", data,
							process.getName());
					// 审批流程开始
					break;
				} else {
					process = null;
				}
			}
		}
		if (process == null) {
			// 没有符合的审批流程
			if (data.getApprovalStatus() != emApprovalStatus.UNAFFECTED && data.isNew()) {
				// 重置数据状态
				data.setApprovalStatus(emApprovalStatus.UNAFFECTED);
			}
		} else {
			this.myProcesses.add(process);
		}
		return process;
	}

	/**
	 * 检查数据状态，是否进行审批流程
	 * 
	 * @param data
	 * @return
	 */
	protected boolean checkDataStatus(IApprovalData data) {
		if (data.isDeleted()) {
			return false;
		}
		if (data instanceof IBODocument) {
			// 单据类型
			IBODocument docData = (IBODocument) data;
			if (docData.getDocumentStatus() == emDocumentStatus.PLANNED) {
				// 计划状态
				return false;
			}
		}
		if (data instanceof IBODocumentLine) {
			// 单据行
			IBODocumentLine lineData = (IBODocumentLine) data;
			if (lineData.getLineStatus() == emDocumentStatus.PLANNED) {
				// 计划状态
				return false;
			}
		}
		if (data instanceof IBOTagDeleted) {
			// 引用数据，已标记删除的，不影响业务逻辑
			IBOTagDeleted tagDeleted = (IBOTagDeleted) data;
			if (tagDeleted.getDeleted() == emYesNo.YES) {
				return false;
			}
		}
		if (data instanceof IBOTagCanceled) {
			// 引用数据，已标记取消的，不影响业务逻辑
			IBOTagCanceled tagCanceled = (IBOTagCanceled) data;
			if (tagCanceled.getCanceled() == emYesNo.YES) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 创建审批流程
	 * 
	 * @param boCode 业务对象编码
	 * @return
	 */
	protected abstract Iterator<IApprovalProcess> createApprovalProcess(String boCode);

	/**
	 * 加载审批流程
	 * 
	 * @param boKey 业务对象标记
	 * @return
	 */
	protected abstract IApprovalProcess loadApprovalProcess(String boKey);
}
