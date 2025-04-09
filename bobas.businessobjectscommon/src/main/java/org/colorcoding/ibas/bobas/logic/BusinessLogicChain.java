package org.colorcoding.ibas.bobas.logic;

import java.util.Objects;
import java.util.function.Function;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.repository.ITransaction;
import org.colorcoding.ibas.bobas.repository.RepositoryException;

/**
 * 业务逻辑链
 */
class BusinessLogicChain implements IBusinessLogicChain {

	public BusinessLogicChain(ITransaction transaction) {
		this.setTransaction(transaction);
		this.setId();
	}

	private String id;

	public final String getId() {
		return id;
	}

	private final void setId() {
		this.id = Strings.format("%s_%s", this.getTransaction().getId(), this.hashCode());
	}

	private Object root;

	public final Object getRoot() {
		return root;
	}

	private final void setRoot(Object root) {
		this.root = root;
	}

	private IBusinessObject trigger;

	@Override
	@SuppressWarnings("unchecked")
	public final <T extends IBusinessObject> T getTrigger() {
		return (T) this.trigger;
	}

	@Override
	public final <T extends IBusinessObject> void setTrigger(T trigger) {
		this.trigger = trigger;
	}

	private IBusinessObject triggerCopy;

	@SuppressWarnings("unchecked")
	protected final <T extends IBusinessObject> T getTriggerCopy() {
		return (T) this.triggerCopy;
	}

	@Override
	public final <T extends IBusinessObject> void setTriggerCopy(T trigger) {
		this.triggerCopy = trigger;

	}

	private ITransaction transaction;

	protected final ITransaction getTransaction() {
		return this.transaction;
	}

	final void setTransaction(ITransaction transaction) {
		Objects.requireNonNull(transaction);
		this.transaction = transaction;
	}

	private IUser user;

	protected final IUser getUser() {
		return user;
	}

	final void setUser(IUser user) {
		this.user = user;
	}

	/**
	 * 执行正向逻辑
	 */
	private final void forwardLogics() {
		// 执行正向逻辑
		for (IBusinessLogic<?> logic : this.getTriggerLogics()) {
			if (logic == null) {
				continue;
			}
			Logger.log(LoggingLevel.INFO, "logics chain [%s]: forward logic [%s].", this.hashCode(), logic.toString());
			logic.forward();
		}
	}

	/**
	 * 执行反向逻辑
	 */
	private final void reverseLogics() {
		// 执行反向逻辑
		for (IBusinessLogic<?> logic : this.getTriggerCopyLogics()) {
			if (logic == null) {
				continue;
			}
			Logger.log(LoggingLevel.INFO, "logics chain [%s]: reverse logic [%s].", this.hashCode(), logic.toString());
			logic.reverse();
		}
	}

	/**
	 * 执行逻辑
	 */
	@Override
	public final void execute() {
		// 执行当前逻辑，再执行被影响对象逻辑，最后保存触发对象
		Objects.requireNonNull(this.getTransaction());
		// 执行逻辑，先反向，再正向
		DateTime startTime = DateTimes.now();
		Logger.log(LoggingLevel.INFO, "logics chain [%s]: starts at [%s].", this.hashCode(),
				DateTimes.toString(startTime, DateTimes.FORMAT_TIME));
		if (!this.getTrigger().isNew() && this.getTriggerCopy() != null) {
			// 非新建，则先反向逻辑
			this.reverseLogics();
		}
		if (!this.getTrigger().isDeleted()) {
			// 非删除，则执行正向逻辑
			this.forwardLogics();
		}
		Logger.log(LoggingLevel.INFO, "logics chain [%s]: logics done at [%s].", this.hashCode(),
				DateTimes.toString(DateTimes.now(), DateTimes.FORMAT_TIME));
		// 保存逻辑影响对象
		IBusinessLogic<?>[] logics = this.getLogics();
		ArrayList<IBusinessObject> beAffecteds = new ArrayList<>(logics.length);
		for (IBusinessLogic<?> logic : logics) {
			if (logic == null) {
				continue;
			}
			if (logic.getBeAffected() == null) {
				// 空对象
				continue;
			}
			if (logic.getBeAffected() == BOUtilities.VALUE_EMPTY) {
				// 代理对象
				continue;
			}
			// 无效状态数据
			if (logic.getBeAffected().isNew() && logic.getBeAffected().isDeleted()) {
				continue;
			}
			// 对象集合
			if (logic.getBeAffected() instanceof IBusinessObjectGroup) {
				for (IBusinessObject item : (IBusinessObjectGroup) logic.getBeAffected()) {
					if (item == null) {
						// 空对象
						continue;
					}
					if (item == BOUtilities.VALUE_EMPTY) {
						// 代理对象
						continue;
					}
					if (beAffecteds.contains(item)) {
						// 重复的被影响对象，位置后移
						beAffecteds.remove(item);
					}
					beAffecteds.add(item);
				}
				continue;
			}
			// 重复的被影响对象，位置后移
			if (beAffecteds.contains(logic.getBeAffected())) {
				beAffecteds.remove(logic.getBeAffected());
			}
			beAffecteds.add(logic.getBeAffected());
		}
		try {
			// 保存触发对象
			this.getTransaction().save(new IBusinessObject[] { this.getTrigger() });
			// 执行被影响的对象的逻辑
			ICriteria criteria;
			IBusinessObject[] tmpDatas;
			for (IBusinessObject item : beAffecteds) {
				if (item.isDirty() == false) {
					// 没有被修改，则跳过
					continue;
				}
				try (BusinessLogicChain logicChain = BusinessLogicsManager.create()
						.createChain(this.getTransaction())) {
					logicChain.setUser(this.getUser());
					logicChain.setRoot(this.getTrigger());
					logicChain.setTrigger(item);
					if (item.isNew() == false) {
						// 非新建，则查询副本
						criteria = item.getCriteria();
						if (criteria == null || criteria.getConditions().isEmpty()) {
							throw new RepositoryException(I18N.prop("msg_bobas_invaild_criteria"));
						}
						criteria.setResultCount(1);
						tmpDatas = this.getTransaction().fetch(item.getClass(), criteria);
						if (tmpDatas == null || tmpDatas.length != 1) {
							throw new RepositoryException(I18N.prop("msg_bobas_fetch_bo_copy_faild", item));
						}
						logicChain.setTriggerCopy(tmpDatas[0]);
					}
					logicChain.execute();
				}
			}
			DateTime endTime = DateTimes.now();
			Logger.log(LoggingLevel.INFO, "logics chain [%s]: ends at [%s].", this.hashCode(),
					DateTimes.toString(endTime, DateTimes.FORMAT_TIME));
			startTime = null;
			endTime = null;
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	protected BusinessLogic<?, ?>[] analyzeContracts(IBusinessObject data) {
		/**
		 * 分析并创建契约服务
		 */
		Function<IBusinessLogicContract, BusinessLogic<?, ?>> analyzer = new Function<IBusinessLogicContract, BusinessLogic<?, ?>>() {

			@Override
			public BusinessLogic<?, ?> apply(IBusinessLogicContract contract) {
				Class<?> tmpClass = contract.getClass();
				// 开始检查契约
				while (tmpClass != null) {
					for (Class<?> item : tmpClass.getInterfaces()) {
						if (!IBusinessLogicContract.class.isAssignableFrom(IBusinessLogicContract.class)) {
							continue;
						}
						// 存在契约，创建契约对应的逻辑实例
						BusinessLogic<?, ?> logic = BusinessLogicsManager.create().createLogic(item);
						if (logic == null) {
							throw new BusinessLogicException(I18N.prop("msg_bobas_not_found_bo_logic", item.getName()));
						}
						logic.setLogicChain(BusinessLogicChain.this);
						logic.setContract(contract);
						return logic;
					}
					// 检查基类的契约
					tmpClass = tmpClass.getSuperclass();
					if (tmpClass.equals(BusinessObject.class)) {
						tmpClass = null;
					}
				}
				return null;
			}
		};
		BusinessLogic<?, ?> logic;
		ArrayList<IBusinessLogic<?>> logics = new ArrayList<>(16);
		// 仅业务对象增加默认逻辑
		if (data instanceof IBusinessObject) {
			// 主键编号
			if (data.isSavable() && data.isNew()) {
				logic = analyzer.apply(new BOKeysContract(data));
				logic.setHost(data);
				logics.add(logic);
			}
			// 业务逻辑执行
			logic = analyzer.apply(new BORulesContract(data));
			logic.setHost(data);
			logics.add(logic);
			// 数据标记
			if (data.isSavable() && !data.isDeleted() && data.isDirty() && data instanceof IBOStorageTag) {
				logic = analyzer.apply(new BOStorageTagContract((IBOStorageTag) data));
				logic.setHost(data);
				logics.add(logic);
			}
			// 审批流程（仅触发对象）
			if (data.isSavable() && data instanceof IApprovalData) {
				if (data == this.getTrigger() && this.getTrigger().isDirty()) {
					logic = analyzer.apply(new BOApprovalContract((IApprovalData) data));
					logic.setHost(data);
					logics.add(logic);
				} else if (data == this.getTriggerCopy() && this.getTrigger().isDirty()) {
					logic = analyzer.apply(new BOApprovalContract((IApprovalData) data));
					logic.setHost(data);
					logics.add(logic);
				}
			}
		}
		// 先子项，再自身（注意：避免嵌套后无限循环寻找契约）
		if (data instanceof BusinessObject) {
			Object cData;
			BusinessObject<?> boData = (BusinessObject<?>) data;
			for (IPropertyInfo<?> propertyInfo : boData.properties()) {
				// 跳过值类型
				if (BOUtilities.isValueType(propertyInfo)) {
					continue;
				}
				if (IBusinessObject.class.isAssignableFrom(propertyInfo.getValueType())) {
					cData = BOUtilities.propertyValue(boData, propertyInfo);
					if (cData instanceof IBusinessObject) {
						for (BusinessLogic<?, ?> item : this.analyzeContracts((IBusinessObject) cData)) {
							if (item.getParent() == null) {
								// 仅未赋值的
								item.setParent(data);
							}
							logics.add(item);
						}
					}
				} else if (IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
					cData = BOUtilities.propertyValue(boData, propertyInfo);
					if (cData instanceof IBusinessObjects) {
						for (IBusinessObject item : ((IBusinessObjects<?, ?>) cData)) {
							for (BusinessLogic<?, ?> sItem : this.analyzeContracts(item)) {
								if (sItem.getParent() == null) {
									// 仅未赋值的
									sItem.setParent(data);
								}
								logics.add(sItem);
							}
						}
					}
				}
			}
			cData = null;
			boData = null;
		}
		// 获取自身契约
		if (data instanceof IBusinessLogicsHost) {
			IBusinessLogicContract[] hostContracts = ((IBusinessLogicsHost) data).getContracts();
			if (hostContracts != null) {
				for (IBusinessLogicContract item : hostContracts) {
					logic = analyzer.apply(item);
					logic.setHost(data);
					logics.add(logic);
				}
			}
			hostContracts = null;
		}
		// 仅业务对象增加默认逻辑
		if (data instanceof IBusinessObject) {
			// 数据日志（仅触发对象）
			if (data.isSavable() && data instanceof IBOStorageTag) {
				if (data == this.getTrigger() && this.getTrigger().isDirty()) {
					logic = analyzer.apply(new BOInstanceLogContract((IBOStorageTag) data));
					logic.setHost(data);
					logics.add(logic);
				} else if (data == this.getTriggerCopy() && this.getTrigger().isDirty()
						&& this.getTrigger().isDeleted()) {
					// 仅对象删除时，触发反向逻辑
					logic = analyzer.apply(new BOInstanceLogContract((IBOStorageTag) data));
					logic.setHost(data);
					logics.add(logic);
				}
			}
		}
		// 返回并剔除无效的
		return logics.where(c -> c != null).toArray(new BusinessLogic<?, ?>[] {});
	}

	private BusinessLogic<?, ?>[] triggerLogics;

	/**
	 * 触发者的业务逻辑
	 * 
	 * @return
	 */
	protected final synchronized IBusinessLogic<?>[] getTriggerLogics() {
		if (this.triggerLogics == null) {
			this.triggerLogics = this.analyzeContracts(this.getTrigger());
			Logger.log(LoggingLevel.INFO, "logics chain [%s]: trigger [%s] has [%s] contracts.", this.hashCode(),
					this.getTrigger(), this.triggerLogics.length);
		}
		return this.triggerLogics;
	}

	private BusinessLogic<?, ?>[] triggerCopyLogics;

	/**
	 * 触发者副本的业务逻辑
	 * 
	 * @return
	 */
	protected final synchronized IBusinessLogic<?>[] getTriggerCopyLogics() {
		if (this.triggerCopyLogics == null) {
			this.triggerCopyLogics = this.analyzeContracts(this.getTriggerCopy());
			Logger.log(LoggingLevel.INFO, "logics chain [%s]: trigger's copy [%s] has [%s] contracts.", this.hashCode(),
					this.getTriggerCopy(), this.triggerCopyLogics.length);
		}
		return this.triggerCopyLogics;
	}

	/**
	 * 全部业务逻辑
	 * 
	 * @return
	 */
	protected final IBusinessLogic<?>[] getLogics() {
		ArrayList<IBusinessLogic<?>> logics = new ArrayList<>(
				(this.triggerCopyLogics != null ? this.triggerCopyLogics.length : 0)
						+ (this.triggerLogics != null ? this.triggerLogics.length : 0));
		if (this.triggerCopyLogics != null) {
			for (int i = 0; i < this.triggerCopyLogics.length; i++) {
				// for (int i = this.triggerCopyLogics.length - 1; i >= 0; i--) {
				logics.add(this.triggerCopyLogics[i]);
			}
		}
		if (this.triggerLogics != null) {
			for (int i = 0; i < this.triggerLogics.length; i++) {
				// for (int i = this.triggerLogics.length - 1; i >= 0; i--) {
				logics.add(this.triggerLogics[i]);
			}
		}
		return logics.toArray(new IBusinessLogic<?>[] {});
	}

	@Override
	public void close() throws Exception {
		this.root = null;
		this.user = null;
		this.trigger = null;
		this.triggerCopy = null;
		this.transaction = null;
		this.triggerLogics = null;
		this.triggerCopyLogics = null;
	}

}
