package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 业务逻辑
 * 
 * @author Niuren.Zhu
 *
 * @param <L>
 *            业务逻辑的契约
 */
public abstract class BusinessLogic<L extends IBusinessLogicContract, B extends IBusinessObjectBase>
		implements IBusinessLogic<B> {

	private L contract;

	/**
	 * 获取-契约数据
	 * 
	 * @return
	 */
	public final L getContract() {
		return this.contract;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void setContract(IBusinessLogicContract contract) {
		this.contract = (L) contract;
	}

	private IBORepository repository;

	/**
	 * 获取-仓库
	 * 
	 * @return
	 */
	protected final IBORepository getRepository() {
		return this.repository;
	}

	@Override
	public final void setRepository(IBORepository repository) {
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

	private L oldContract;

	/**
	 * 获取-旧的契约数据
	 * 
	 * @return
	 */
	public final L getOldContract() {
		if (this.oldContract == null) {
			L tmpData = this.fetchExistingContract();
			if (tmpData != null && tmpData.getClass().equals(this.getContract().getClass())) {
				// 查询的数据有效
				this.oldContract = tmpData;
			} else {
				throw new BusinessLogicsException(i18n.prop("msg_bobas_not_found_bo_copy", this.getContract()));
			}
		}
		return this.oldContract;
	}

	/**
	 * 查询契约数据的已存在副本（用于回滚旧逻辑影响）
	 * 
	 * 默认实现查询业务对象副本，可重载
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected L fetchExistingContract() {
		try {
			if (this.getContract() instanceof IBusinessObjectBase) {
				IBusinessObjectBase bo = (IBusinessObjectBase) this.getContract();
				IOperationResult<?> opRslt = this.getRepository().fetchCopy(bo);
				if (opRslt.getError() != null) {
					throw opRslt.getError();
				}
				if (opRslt.getResultCode() != 0) {
					throw new Exception(opRslt.getMessage());
				}
				return (L) opRslt.getResultObjects().firstOrDefault();
			}
			throw new BusinessLogicsException(i18n.prop("msg_bobas_not_supported"));
		} catch (Exception e) {
			throw new BusinessLogicsException(e);
		}
	}

	@Override
	public final void forward() {
		// 检查数据状态
		if (!this.checkDataStatus(this.getContract())) {
			// 数据状态不通过，跳过正向逻辑执行
			return;
		}
		// 执行正向逻辑
		RuntimeLog.log(RuntimeLog.MSG_LOGICS_RUNNING_LOGIC_FORWARD, this.getClass().getName(),
				this.getContract().getIdentifiers());
		this.impact(this.getContract());
	}

	@Override
	public final void reverse() {
		// 检查数据状态
		if (!this.checkDataStatus(this.getOldContract())) {
			// 数据状态不通过，跳过反向逻辑执行
			return;
		}
		// 执行撤销逻辑
		RuntimeLog.log(RuntimeLog.MSG_LOGICS_RUNNING_LOGIC_REVERSE, this.getClass().getName(),
				this.getContract().getIdentifiers());
		this.revoke(this.getOldContract());
	}

	private B beAffected;

	public B getBeAffected() {
		if (this.beAffected == null) {
			this.beAffected = this.fetchBeAffected(this.getContract());

		}
		return this.beAffected;
	}

	private boolean done;

	@Override
	public boolean isDone() {
		return this.done;
	}

	@Override
	public void commit() {
		IOperationResult<?> operationResult = this.getRepository().saveEx(this.getBeAffected());
		if (operationResult.getError() != null) {
			throw new BusinessLogicsException(operationResult.getError());
		}
		if (operationResult.getResultCode() != 0) {
			throw new BusinessLogicsException(operationResult.getMessage());
		}
		this.done = true;
	}

	/**
	 * 检索被影响的数据
	 * 
	 * @return
	 */
	protected abstract B fetchBeAffected(L contract);

	/**
	 * 执行逻辑
	 */
	protected abstract void impact(L contract);

	/**
	 * 撤销逻辑
	 */
	protected abstract void revoke(L contract);
}
