package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 默认流程管理员
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcessManager implements IApprovalProcessManager {

	@Override
	public IApprovalProcess checkProcess(IApprovalData data) {
		if (data == null) {
			return null;
		}
		if (data.isNew()) {
			// 新数据
			if (!this.checkDataStatus(data)) {
				// 状态检测未通过
				return null;
			}
			// 创建审批流程并尝试开始
			IApprovalProcess aProcess = this.createApprovalProcess(data.getObjectCode());
			if (aProcess.start(data))
				return aProcess;// 审批流程开始
		} else {
			// 不是新建的数据
			IApprovalProcess aProcess = this.loadApprovalProcess(data.getIdentifiers());
			return aProcess;
		}
		return null;
	}

	/**
	 * 检查数据状态，是否进行审批流程
	 * 
	 * @param data
	 * @return
	 */
	protected boolean checkDataStatus(IApprovalData data) {
		if (data instanceof IBODocument) {
			// 单据类型
			IBODocument docData = (IBODocument) data;
			if (docData.getDocumentStatus() == emDocumentStatus.Planned) {
				// 计划状态
				return false;
			}
			if (docData.getCanceled() == emYesNo.Yes) {
				// 取消的
				return false;
			}
		}
		if (data instanceof IBODocumentLine) {
			// 单据行
			IBODocumentLine lineData = (IBODocumentLine) data;
			if (lineData.getCanceled() == emYesNo.Yes) {
				// 取消的
				return false;
			}
		}
		return true;
	}

	/**
	 * 创建审批流程
	 * 
	 * @param boCode
	 *            业务对象编码
	 * @return
	 */
	protected abstract IApprovalProcess createApprovalProcess(String boCode);

	/**
	 * 加载审批流程
	 * 
	 * @param boCode
	 * @return
	 */
	protected abstract IApprovalProcess loadApprovalProcess(String boKey);
}
