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
public class ApprovalProcessManager implements IApprovalProcessManager {

	@Override
	public IApprovalProcess checkProcess(IApprovalData data) {
		if (!this.checkDataStatus(data)) {
			// 状态检测未通过
			return null;
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

}
