package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBOTagReferenced;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.ITrackStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

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
 * @param <B>
 *            被影响的对象
 */
public abstract class BusinessLogic<L extends IBusinessLogicContract, B extends IBusinessObject>
		implements IBusinessLogic<B> {

	protected static final String MSG_LOGICS_FOUND_DATA_IN_CHAIN = "logics: found affected data in chain.";
	protected static final String MSG_LOGICS_RUNNING_LOGIC_FORWARD = "logics: forward logic [%s].";
	protected static final String MSG_LOGICS_RUNNING_LOGIC_REVERSE = "logics: reverse logic [%s].";
	protected static final String MSG_LOGICS_SKIP_LOGIC_EXECUTION = "logics: skip logic [%s], because [%s = %s].";

	private L contract;

	/**
	 * 获取-契约数据
	 * 
	 * @return
	 */
	protected final L getContract() {
		return this.contract;
	}

	@SuppressWarnings("unchecked")
	final void setContract(IBusinessLogicContract contract) {
		this.contract = (L) contract;
	}

	private IBusinessLogicsHost host;

	/**
	 * 获取-契约数据所属BO
	 * 
	 * @return
	 */
	protected final IBusinessLogicsHost getHost() {
		return host;
	}

	final void setHost(IBusinessLogicsHost host) {
		this.host = host;
	}

	private Object parent;

	/**
	 * 获取-契约数据所属父项BO
	 * 
	 * @return
	 */
	protected final Object getParent() {
		return parent;
	}

	final void setParent(Object parent) {
		this.parent = parent;
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
	 * 第一次执行，检查契约数据父项状态；第二次执行，检查契约数据状态。
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
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(), "isDeleted",
						status.isDeleted());
				return false;
			}
		}
		if (data instanceof IBOTagDeleted) {
			// 引用数据，已标记删除的，不影响业务逻辑
			IBOTagDeleted refData = (IBOTagDeleted) data;
			if (refData.getDeleted() == emYesNo.YES) {
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(), "Deleted",
						refData.getDeleted());
				return false;
			}
		}
		if (data instanceof IBOTagCanceled) {
			// 引用数据，已标记取消的，不影响业务逻辑
			IBOTagCanceled refData = (IBOTagCanceled) data;
			if (refData.getCanceled() == emYesNo.YES) {
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(), "Canceled",
						refData.getCanceled());
				return false;
			}
		}
		if (data instanceof IApprovalData) {
			// 审批数据
			IApprovalData apData = (IApprovalData) data;
			if (apData.getApprovalStatus() == emApprovalStatus.CANCELLED
					|| apData.getApprovalStatus() == emApprovalStatus.PROCESSING
					|| apData.getApprovalStatus() == emApprovalStatus.REJECTED) {
				// 审批中，取消，拒绝
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"ApprovalStatus", apData.getApprovalStatus());
				return false;
			}
		}
		if (data instanceof IBODocument) {
			// 单据类型
			IBODocument docData = (IBODocument) data;
			if (docData.getDocumentStatus() == emDocumentStatus.PLANNED) {
				// 计划状态
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"DocumentStatus", docData.getDocumentStatus());
				return false;
			}
		}
		if (data instanceof IBODocumentLine) {
			// 单据行
			IBODocumentLine lineData = (IBODocumentLine) data;
			if (lineData.getLineStatus() == emDocumentStatus.PLANNED) {
				// 计划状态
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(), "LineStatus",
						lineData.getLineStatus());
				return false;
			}
		}
		return true;
	}

	/**
	 * 运行正向逻辑
	 */
	@Override
	public final void forward() {
		// 检查父项数据状态
		if (this.getParent() != null && !this.checkDataStatus(this.getParent())) {
			// 数据状态不通过，跳过正向逻辑执行
			return;
		}
		// 检查宿主数据状态
		if (!this.checkDataStatus(this.getHost())) {
			// 数据状态不通过，跳过正向逻辑执行
			return;
		}
		// 检查契约数据状态
		if (!this.checkDataStatus(this.getContract())) {
			// 数据状态不通过，跳过正向逻辑执行
			return;
		}
		// 执行正向逻辑
		Logger.log(MessageLevel.DEBUG, MSG_LOGICS_RUNNING_LOGIC_FORWARD, this.getClass().getName());
		if (this.beAffected == null) {
			// 加载被影响的数据
			this.beAffected = this.fetchBeAffected(this.getContract());
		}
		if (this.beAffected instanceof BusinessObject<?>) {
			BusinessObject<?> bo = (BusinessObject<?>) this.beAffected;
			if (bo.isDeleted()) {
				// 恢复bo删除状态
				bo.undelete();
			}
		}
		if (this.beAffected instanceof IBOTagReferenced) {
			// 被影响数据，自动标记引用
			IBOTagReferenced referenced = (IBOTagReferenced) this.beAffected;
			if (referenced.getReferenced() == emYesNo.NO) {
				referenced.setReferenced(emYesNo.YES);
			}
		}
		this.impact(this.getContract());
	}

	/**
	 * 运行方向逻辑
	 */
	@Override
	public final void reverse() {
		// 检查父项数据状态
		if (this.getParent() != null && !this.checkDataStatus(this.getParent())) {
			// 数据状态不通过，跳过正向逻辑执行
			return;
		}
		// 契约的数据为新建时，不执行反向逻辑
		if (this.getHost() instanceof ITrackStatus) {
			ITrackStatus status = (ITrackStatus) this.getHost();
			if (status.isNew()) {
				return;
			}
		}
		// 检查宿主数据状态
		if (!this.checkDataStatus(this.getHost())) {
			// 数据状态不通过，跳过反向逻辑执行
			return;
		}
		// 检查契约数据状态
		if (!this.checkDataStatus(this.getContract())) {
			// 数据状态不通过，跳过正向逻辑执行
			return;
		}
		// 执行撤销逻辑
		Logger.log(MessageLevel.DEBUG, MSG_LOGICS_RUNNING_LOGIC_REVERSE, this.getClass().getName());
		if (this.beAffected == null) {
			// 加载被影响的数据
			this.beAffected = this.fetchBeAffected(this.getContract());
		}
		this.revoke(this.getContract());
	}

	private B beAffected;

	public final B getBeAffected() {
		return this.beAffected;
	}

	/**
	 * 提交
	 */
	@Override
	public void commit() {
		if (this.getBeAffected() != null) {
			BusinessLogicsRepository logicRepository = new BusinessLogicsRepository();
			logicRepository.setRepository(this.getRepository());
			OperationResult<B> operationResult = logicRepository.saveData(this.getBeAffected());
			logicRepository.setRepository(null);// 移出监听
			if (operationResult.getError() != null) {
				if (operationResult.getError() instanceof BusinessLogicException) {
					throw (BusinessLogicException) operationResult.getError();
				} else {
					throw new BusinessLogicException(operationResult.getError());
				}
			}
		}
	}

	private BusinessLogicChain logicChain;

	protected final BusinessLogicChain getLogicChain() {
		return this.logicChain;
	}

	final void setLogicChain(BusinessLogicChain value) {
		this.logicChain = value;
	}

	/**
	 * 缓存中查询被影响的数据
	 * 
	 * @param criteria
	 * @param type
	 * @return
	 */
	protected <D> D fetchBeAffected(ICriteria criteria, Class<D> type) {
		if (this.getLogicChain() != null) {
			D data = this.getLogicChain().fetchBeAffected(criteria, type);
			if (data != null) {
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_FOUND_DATA_IN_CHAIN);
				return data;
			}
		}
		return null;
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
