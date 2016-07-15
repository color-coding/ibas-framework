package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 业务逻辑
 * 
 * @author Niuren.Zhu
 *
 * @param <L>
 *            业务逻辑的契约
 */
public class BusinessLogic<L extends IBusinessLogicContract> implements IBusinessLogic {

	private L contract;

	/**
	 * 获取-契约数据
	 * 
	 * @return
	 */
	public L getContract() {
		return this.contract;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContract(IBusinessLogicContract contract) {
		this.contract = (L) contract;
	}

	private IBORepository repository;

	/**
	 * 获取-仓库
	 * 
	 * @return
	 */
	protected IBORepository getRepository() {
		return this.repository;
	}

	@Override
	public void setRepository(IBORepository repository) {
		this.repository = repository;
	}

	/**
	 * 检查数据状态
	 * 
	 * 审批数据，批准和不影响的有效；单据，非计划和没取消的有效
	 * 
	 * @param data
	 *            检查的数据
	 * @return 有效，true；无效，false
	 */
	protected boolean checkDataStatus(Object data) {
		if (data instanceof IApprovalData) {
			// 审批数据
			IApprovalData apData = (IApprovalData) data;
			if (apData.getApprovalStatus() == emApprovalStatus.Approved
					|| apData.getApprovalStatus() == emApprovalStatus.Unaffected) {
				// 计划状态
				return true;
			}
			return false;
		}
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

	@Override
	public void forward() {
		RuntimeLog.log(RuntimeLog.MSG_LOGICS_RUNNING_LOGIC_FORWARD, this.getClass().getName(),
				this.getContract().toString());

	}

	@Override
	public void reverse() {
		RuntimeLog.log(RuntimeLog.MSG_LOGICS_RUNNING_LOGIC_REVERSE, this.getClass().getName(),
				this.getContract().toString());

	}

}
