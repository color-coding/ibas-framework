package org.colorcoding.ibas.bobas.logic;

import java.util.Iterator;
import java.util.Objects;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBOTagReferenced;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.ITrackable;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.repository.ITransaction;
import org.colorcoding.ibas.bobas.repository.Transaction;

/**
 * 业务逻辑基类
 * 
 * @param <L> 契约类型
 * @param <T> 被影响的对象
 */
public abstract class BusinessLogic<L extends IBusinessLogicContract, T extends IBusinessObject>
		implements IBusinessLogic<T> {

	protected static final String MSG_LOGICS_SKIP_LOGIC_EXECUTION = "logics: skip logic [%s], because [%s = %s].";

	private BusinessLogicChain logicChain;

	/**
	 * 获取-业务逻辑链
	 * 
	 * @return
	 */
	final BusinessLogicChain getLogicChain() {
		return this.logicChain;
	}

	final void setLogicChain(BusinessLogicChain value) {
		this.logicChain = value;
	}

	/**
	 * 获取-当前用户
	 * 
	 * @return
	 */
	protected final IUser getUser() {
		return this.getLogicChain().getUser();
	}

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

	private IBusinessObject host;

	/**
	 * 获取-契约数据所属
	 * 
	 * @return
	 */
	protected final IBusinessObject getHost() {
		return host;
	}

	final void setHost(IBusinessObject host) {
		this.host = host;
	}

	/**
	 * 获取-契约链触发对象
	 * 
	 * @return
	 */
	protected final Object getTrigger() {
		return this.getLogicChain().getTrigger();
	}

	private Object parent;

	/**
	 * 获取-契约数据父项
	 * 
	 * @return
	 */
	protected final Object getParent() {
		return parent;
	}

	final void setParent(Object parent) {
		this.parent = parent;
	}

	/**
	 * 获取-契约数据所属根项
	 * 
	 * @return
	 */
	protected final Object getRoot() {
		Object root = this.getLogicChain().getRoot();
		if (root == null) {
			// 如果是根，则使用触发对象
			root = this.getLogicChain().getTrigger();
		}
		return root;
	}

	/**
	 * 获取-事务
	 * 
	 * @return
	 */
	protected final ITransaction getTransaction() {
		return this.getLogicChain().getTransaction();
	}

	private T beAffected;

	/**
	 * 获取-被影响的对象
	 */
	public final T getBeAffected() {
		return this.beAffected;
	}

	/**
	 * 检查数据状态
	 * 
	 * 审批数据，批准和不影响的有效；单据，非计划和没取消的有效
	 * 
	 * 第一次执行，检查契约数据父项状态；第二次执行，检查契约数据状态。
	 * 
	 * @param data 检查的数据
	 * @return 有效，true；无效，false
	 */
	protected boolean checkDataStatus(Object data) {
		if (data instanceof ITrackable) {
			// 标记删除的数据无效
			ITrackable status = (ITrackable) data;
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
					|| apData.getApprovalStatus() == emApprovalStatus.REJECTED
					|| apData.getApprovalStatus() == emApprovalStatus.RETURNED) {
				// 审批中，取消，拒绝，退回
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

	private int forwardCount = 0;

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
		this.forwardCount++;
		Logger.log(this.forwardCount > 1 ? MessageLevel.WARN : MessageLevel.DEBUG,
				"logics: forward logic [%s], %sth time.", this.getClass().getName(), this.forwardCount);
		if (this.forwardCount > 1) {
			if (!this.onRepeatedImpact(this.forwardCount)) {
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"forwardCount", this.forwardCount);
				return;
			}
		}
		if (this.beAffected == null) {
			// 加载被影响的数据
			this.beAffected = this.fetchBeAffected(this.getContract());
			// 自动缓存数据
			if (this.getTransaction() instanceof Transaction) {
				Transaction transaction = (Transaction) this.getTransaction();
				if (BOUtilities.isBusinessObject(this.beAffected)) {
					transaction.cache(this.beAffected);
				}
				// 对象组，子项也缓存
				if (this.beAffected instanceof IBusinessObjectGroup) {
					IBusinessObjectGroup boGroup = (IBusinessObjectGroup) this.beAffected;
					for (IBusinessObject bo : boGroup) {
						if (BOUtilities.isBusinessObject(bo)) {
							transaction.cache(bo);
						}
					}
				}
			}
		}
		if (this.beAffected instanceof IBusinessObject) {
			IBusinessObject bo = (IBusinessObject) this.beAffected;
			if (bo.isDeleted()) {
				// 恢复bo删除状态
				bo.undelete();
			}
		}
		if (this.beAffected instanceof IBOTagDeleted) {
			IBOTagDeleted bo = (IBOTagDeleted) this.beAffected;
			if (bo.getDeleted() == emYesNo.YES) {
				// 恢复bo删除状态
				bo.setDeleted(emYesNo.NO);
			}
		}
		if (this.beAffected instanceof IBOTagReferenced) {
			// 被影响数据，自动标记引用
			IBOTagReferenced bo = (IBOTagReferenced) this.beAffected;
			if (bo.getReferenced() == emYesNo.NO) {
				bo.setReferenced(emYesNo.YES);
			}
		}
		this.impact(this.getContract());
	}

	private int reverseCount = 0;

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
		if (this.getHost() instanceof ITrackable) {
			ITrackable status = (ITrackable) this.getHost();
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
		this.reverseCount++;
		Logger.log(this.reverseCount > 1 ? MessageLevel.WARN : MessageLevel.DEBUG,
				"logics: reverse logic [%s], %sth time.", this.getClass().getName(), this.reverseCount);
		if (this.reverseCount > 1) {
			if (!this.onRepeatedRevoke(this.reverseCount)) {
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"reverseCount", this.reverseCount);
				return;
			}
		}
		if (this.beAffected == null) {
			// 加载被影响的数据
			this.beAffected = this.fetchBeAffected(this.getContract());
			// 自动缓存数据
			if (this.getTransaction() instanceof Transaction) {
				Transaction transaction = (Transaction) this.getTransaction();
				if (BOUtilities.isBusinessObject(this.beAffected)) {
					transaction.cache(this.beAffected);
				}
				// 对象组，子项也缓存
				if (this.beAffected instanceof IBusinessObjectGroup) {
					IBusinessObjectGroup boGroup = (IBusinessObjectGroup) this.beAffected;
					for (IBusinessObject bo : boGroup) {
						if (BOUtilities.isBusinessObject(bo)) {
							transaction.cache(bo);
						}
					}
				}
			}
		}
		// 执行撤销逻辑
		this.revoke(this.getContract());
	}

	/**
	 * 查询被影响对象（优先使用缓存方式）
	 * 
	 * @param <B>
	 * @param boType
	 * @param criteria
	 * @return
	 */
	protected final <B> B fetchBeAffected(Class<B> boType, ICriteria criteria) {
		if (this.getTransaction() instanceof Transaction) {
			return this.fetchBeAffectedInCaches(boType, criteria, false).firstOrDefault();
		} else {
			return this.fetchBeAffectedInLogics(boType, criteria, false).firstOrDefault();
		}
	}

	/**
	 * 查询被影响对象（优先使用缓存方式）
	 * 
	 * @param <B>
	 * @param criteria
	 * @param boType
	 * @return
	 */
	@Deprecated
	protected final <B> B fetchBeAffected(ICriteria criteria, Class<B> boType) {
		return this.fetchBeAffected(boType, criteria);
	}

	/**
	 * 逻辑链中查询被影响对象
	 * 
	 * @param <B>
	 * @param boType
	 * @param criteria
	 * @param all      返回全部
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final <B> List<B> fetchBeAffectedInLogics(Class<B> boType, ICriteria criteria, boolean all) {
		Objects.requireNonNull(boType);
		Objects.requireNonNull(criteria);
		Objects.requireNonNull(this.getLogicChain());

		// 不返回全部，则只第一条
		if (all == false) {
			criteria = criteria.clone();
			criteria.setResultCount(1);
		}

		// 业务逻辑被影响数据迭代器（不一次取出）
		Iterator<B> iterator = new Iterator<B>() {
			// 先查父项被影响数据
			Iterator<IBusinessLogic<?>> parentLogics = BusinessLogic.this.getLogicChain().getParentChain() != null
					? BusinessLogic.this.getLogicChain().getParentChain().getLogics().iterator()
					: null;
			// 再查自身被影响数据
			Iterator<IBusinessLogic<?>> myLogics = BusinessLogic.this.getLogicChain().getLogics().iterator();
			// 数据迭代器
			Iterator<B> current = null;

			@Override
			public boolean hasNext() {
				if (this.current != null && this.current.hasNext()) {
					return true;
				}
				if (this.myLogics != null && this.myLogics.hasNext()) {
					return true;
				}
				if (this.parentLogics != null && this.parentLogics.hasNext()) {
					return true;
				}
				return false;
			}

			@Override
			public B next() {
				if (this.current != null && this.current.hasNext()) {
					return this.current.next();
				}
				this.current = null;
				IBusinessLogic<?> logic = null;
				// 先使用自身逻辑
				if (logic == null) {
					if (this.myLogics != null && this.myLogics.hasNext()) {
						logic = this.myLogics.next();
					}
				}
				// 再使用父项逻辑
				if (logic == null) {
					if (this.parentLogics != null && this.parentLogics.hasNext()) {
						logic = this.parentLogics.next();
					}
				}
				if (logic != null) {
					List<B> results = null;
					if (boType.isInstance(logic.getBeAffected())) {
						if (results == null) {
							results = new ArrayList<>(1);
						}
						results.add((B) logic.getBeAffected());
					} else if (logic.getBeAffected() instanceof IBusinessObjectGroup) {
						if (results == null) {
							results = new ArrayList<>();
						}
						for (Object item : (IBusinessObjectGroup) logic.getBeAffected()) {
							if (boType.isInstance(item)) {
								results.add((B) item);
							}
						}
					}
					if (results != null && !results.isEmpty()) {
						this.current = results.iterator();
						return this.next();
					}
				}
				return null;
			}
		};
		try {
			return BOUtilities.fetch(iterator, criteria);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 缓存中查询被影响对象
	 * 
	 * @param <B>
	 * @param boType
	 * @param criteria
	 * @param all      返回全部
	 * @return
	 */
	protected final <B> List<B> fetchBeAffectedInCaches(Class<B> boType, ICriteria criteria, boolean all) {
		List<B> results = new ArrayList<>();
		try {
			// 不返回全部，则只第一条
			if (all == false) {
				criteria = criteria.clone();
				criteria.setResultCount(1);
			}
			if (this.getTransaction() instanceof Transaction) {
				Transaction transaction = (Transaction) this.getTransaction();
				results.addAll(transaction.fetchInCache(boType, criteria));
			}
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
		return results;
	}

	/**
	 * 重复执行正向逻辑时
	 * 
	 * @param times 次数
	 * @return 是否继续执行
	 */
	protected boolean onRepeatedImpact(int times) {
		return false;
	}

	/**
	 * 重复执行反向逻辑时
	 * 
	 * @param times 次数
	 * @return 是否继续执行
	 */
	protected boolean onRepeatedRevoke(int times) {
		return false;
	}

	/**
	 * 检索被影响的数据
	 * 
	 * @return
	 */
	protected abstract T fetchBeAffected(L contract);

	/**
	 * 执行逻辑
	 */
	protected abstract void impact(L contract);

	/**
	 * 撤销逻辑
	 */
	protected abstract void revoke(L contract);
}
