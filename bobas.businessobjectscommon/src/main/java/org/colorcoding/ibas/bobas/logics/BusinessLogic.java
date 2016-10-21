package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.ITrackStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 业务逻辑
 * 
 * 注意：
 * 
 * 1. fetchBeAffected()方法中要先调用fetchBeAffected(ICriteria,Class <?>)没有返回值后再重新查询。
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
	final void setContract(IBusinessLogicContract contract) {
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

	final void setRepository(IBORepository repository) {
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
		if (data instanceof ITrackStatus) {
			// 标记删除的数据无效
			ITrackStatus status = (ITrackStatus) data;
			if (status.isDeleted()) {
				return false;
			}
		}
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
			if (lineData.getLineStatus() == emDocumentStatus.Planned) {
				// 计划状态
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
		if (this.beAffected == null) {
			// 加载被影响的数据
			this.beAffected = this.fetchBeAffected(this.getContract());
		}
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
		if (this.beAffected == null) {
			// 加载被影响的数据
			this.beAffected = this.fetchBeAffected(this.getContract());
		}
		this.revoke(this.getOldContract());
	}

	private B beAffected;

	public B getBeAffected() {
		return this.beAffected;
	}

	private boolean done;

	@Override
	public boolean isDone() {
		return this.done;
	}

	@Override
	public void commit() {
		if (this.getBeAffected() != null) {
			BusinessLogicsRepository logicRepository = new BusinessLogicsRepository();
			logicRepository.setRepository(this.getRepository());
			IOperationResult<?> operationResult = logicRepository.save(this.getBeAffected());
			this.getRepository().removeSaveActionsListener(logicRepository);// 移出监听
			if (operationResult.getError() != null) {
				throw new BusinessLogicsException(operationResult.getError());
			}
			if (operationResult.getResultCode() != 0) {
				throw new BusinessLogicsException(operationResult.getMessage());
			}
		}
		this.done = true;
	}

	IBusinessLogicsChain logicsChain;

	IBusinessLogicsChain getLogicsChain() {
		return this.logicsChain;
	}

	void setLogicsChain(IBusinessLogicsChain value) {
		this.logicsChain = value;
	}

	/**
	 * 在逻辑所处仓库中查询被影响数据
	 * 
	 * 多逻辑影响同一个对象时，不能让每个逻辑中的对象为单独实例，此方法是从逻辑链中查询已存在的对象
	 * 
	 * @param criteria
	 *            查询
	 * @param type
	 *            数据类型（即返回值类型）
	 * @return
	 */
	protected B fetchBeAffected(ICriteria criteria, Class<B> type) {
		if (this.getLogicsChain() == null) {
			return null;
		}
		return this.getLogicsChain().fetchBeAffected(criteria, type);
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
