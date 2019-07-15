package org.colorcoding.ibas.bobas.logic;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.BORepositoryBase;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.expression.ExpressionFactory;
import org.colorcoding.ibas.bobas.expression.JudgmentLink;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicChain implements IBusinessLogicChain {

	protected static final String MSG_TRIGGER_EXISTING_CONTRACT = "logics: trigger [%s] has [%s] contracts.";
	protected static final String MSG_TRIGGER_COPY_EXISTING_CONTRACT = "logics: trigger's copy [%s] has [%s] contracts.";
	/**
	 * 业务对象代理
	 */
	protected static final IBusinessObject BO_PROXY_EMPTY = new IBusinessObjectProxy() {

		private static final long serialVersionUID = -1L;

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public boolean isSavable() {
			return false;
		}

		@Override
		public boolean isNew() {
			return false;
		}

		@Override
		public boolean isLoading() {
			return false;
		}

		@Override
		public boolean isDirty() {
			return false;
		}

		@Override
		public boolean isDeleted() {
			return false;
		}

		@Override
		public boolean isBusy() {
			return false;
		}

		@Override
		public String toString(String type) {
			return this.toString();
		}

		@Override
		public String toString() {
			return "{empty proxy}";
		}

		@Override
		public ICriteria getCriteria() {
			return null;
		}

		@Override
		public String getIdentifiers() {
			return this.toString();
		}

		@Override
		public void delete() {
		}

		@Override
		public void undelete() {
		}
	};

	public BusinessLogicChain(IBusinessLogicsManager manager) {
		this.setLogicsManager(manager);
	}

	private IBusinessLogicsManager logicsManager;

	protected final IBusinessLogicsManager getLogicsManager() {
		return logicsManager;
	}

	private final void setLogicsManager(IBusinessLogicsManager logicsManager) {
		this.logicsManager = logicsManager;
	}

	private String id;

	@Override
	public final void setGroup(String value) {
		this.id = value;
	}

	@Override
	public final String getGroup() {
		return this.id;
	}

	private IBusinessObject trigger;

	@Override
	public final IBusinessObject getTrigger() {
		return this.trigger;
	}

	@Override
	public final void setTrigger(IBusinessObject bo) {
		this.trigger = bo;
		if (this.trigger != null && this.trigger.isNew()) {
			// 新建对象，则副本置为无效值
			this.setTriggerCopy(BO_PROXY_EMPTY);
		}
	}

	private IBORepository repository;

	protected final IBORepository getRepository() {
		return this.repository;
	}

	protected final void setRepository(IBORepository boRepository) {
		this.repository = boRepository;
	}

	@Override
	public final void useRepository(IBORepository boRepository) {
		if (this.repository != null) {
			throw new RuntimeException(I18N.prop("msg_bobas_not_supported"));
		}
		// 使用仓库副本并使用系统用户
		if (boRepository instanceof BORepositoryBase) {
			try {
				boRepository = (IBORepository) ((BORepositoryBase) boRepository).clone();
				boRepository.setCurrentUser(OrganizationFactory.SYSTEM_USER);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		this.setRepository(boRepository);
	}

	private IBusinessObject triggerCopy;

	/**
	 * 获取触发者副本
	 * 
	 * @return
	 */
	protected final IBusinessObject getTriggerCopy() {
		if (this.triggerCopy == null) {
			this.setTriggerCopy(this.fetchTriggerCopy());
		}
		return this.triggerCopy;
	}

	private final void setTriggerCopy(IBusinessObject bo) {
		this.triggerCopy = bo;
	}

	public final void setTriggerCopy() {
		if (this.triggerCopy == null) {
			this.triggerCopy = this.trigger;
		}
	}

	/**
	 * 查询触发者副本
	 * 
	 * @return
	 */
	protected IBusinessObject fetchTriggerCopy() {
		IBusinessObject trigger = this.getTrigger();
		IBusinessObject copy = this.fetchBusinessObject(trigger.getCriteria(), trigger.getClass());
		if (trigger instanceof IBOStorageTag && copy instanceof IBOStorageTag) {
			IBOStorageTag hostTag = (IBOStorageTag) trigger;
			IBOStorageTag copyTag = (IBOStorageTag) copy;
			if (copyTag.getLogInst() > hostTag.getLogInst()) {
				// 副本版本更高，不能被覆盖逻辑
				throw new BusinessLogicException(I18N.prop("msg_bobas_bo_copy_is_more_newer"));
			}
		}
		return copy;
	}

	protected IBusinessObject fetchBusinessObject(ICriteria criteria, Class<? extends IBusinessObject> type) {
		BusinessLogicsRepository repository = new BusinessLogicsRepository();
		repository.setRepository(this.getRepository());
		IOperationResult<?> operationResult = repository.fetchData(criteria, type);
		if (operationResult.getError() != null) {
			throw new BusinessLogicException(operationResult.getError());
		}
		return (IBusinessObject) operationResult.getResultObjects().firstOrDefault();
	}

	private IBusinessLogic<?>[] triggerLogics;

	/**
	 * 触发者的业务逻辑
	 * 
	 * @return
	 */
	protected final IBusinessLogic<?>[] getTriggerLogics() {
		if (this.triggerLogics == null) {
			this.triggerLogics = this.analyzeContracts(this.getTrigger());
			Logger.log(MessageLevel.INFO, MSG_TRIGGER_EXISTING_CONTRACT, this.getTrigger(), this.triggerLogics.length);
		}
		return triggerLogics;
	}

	private IBusinessLogic<?>[] triggerCopyLogics;

	/**
	 * 触发者副本的业务逻辑
	 * 
	 * @return
	 */
	protected final IBusinessLogic<?>[] getTriggerCopyLogics() {
		if (this.triggerCopyLogics == null) {
			this.triggerCopyLogics = this.analyzeContracts(this.getTriggerCopy());
			Logger.log(MessageLevel.INFO, MSG_TRIGGER_COPY_EXISTING_CONTRACT, this.getTriggerCopy(),
					this.triggerCopyLogics.length);
		}
		return triggerCopyLogics;
	}

	/**
	 * 全部业务逻辑
	 * 
	 * @return
	 */
	protected final Iterator<IBusinessLogic<?>> getAllLogics() {
		return new Iterator<IBusinessLogic<?>>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				int count = 0;
				if (BusinessLogicChain.this.getTriggerLogics() != null) {
					count += BusinessLogicChain.this.getTriggerLogics().length;
				}
				if (BusinessLogicChain.this.getTriggerCopyLogics() != null) {
					count += BusinessLogicChain.this.getTriggerCopyLogics().length;
				}
				if (index < 0) {
					return false;
				}
				if (index > count) {
					return false;
				}
				return true;
			}

			@Override
			public IBusinessLogic<?> next() {
				IBusinessLogic<?> logic = null;
				if (index >= 0 && index < BusinessLogicChain.this.getTriggerLogics().length) {
					logic = BusinessLogicChain.this.getTriggerLogics()[index];
				} else {
					int cIndex = index - BusinessLogicChain.this.getTriggerLogics().length;
					if (cIndex >= 0 && cIndex < BusinessLogicChain.this.getTriggerCopyLogics().length) {
						logic = BusinessLogicChain.this.getTriggerCopyLogics()[cIndex];
					}
				}
				index++;
				return logic;
			}

		};
	}

	private static final IBusinessLogic<?>[] NO_BUSINESS_LOGICS = new IBusinessLogic[] {};

	/**
	 * 分析数据，获取契约
	 * 
	 * @param bo 数据
	 * @return 具有的契约
	 */
	protected IBusinessLogic<?>[] analyzeContracts(Object bo) {
		if (bo == null || bo instanceof IBusinessObjectProxy) {
			return NO_BUSINESS_LOGICS;
		}
		ArrayList<IBusinessLogic<?>> contracts = new ArrayList<>();
		// 先子项，再自身
		// 注意：避免嵌套后无限循环寻找契约
		if (bo instanceof IManagedFields) {
			IManagedFields boFields = (IManagedFields) bo;
			for (IFieldData item : boFields.getFields()) {
				if (item == null || item.getValue() == null) {
					continue;
				}
				if (item.getValue() instanceof IBusinessObject) {
					IBusinessLogic<?>[] tmpLogics = this.analyzeContracts((IBusinessObject) item.getValue());
					// 记录父项
					for (IBusinessLogic<?> tmpLogic : tmpLogics) {
						if (tmpLogic instanceof BusinessLogic<?, ?>) {
							BusinessLogic<?, ?> logic = (BusinessLogic<?, ?>) tmpLogic;
							logic.setParent(bo);
						}
					}
					contracts.addAll(tmpLogics);
				} else if (item.getValue() instanceof IBusinessObjects<?, ?>) {
					IBusinessObjects<?, ?> bos = (IBusinessObjects<?, ?>) item.getValue();
					for (IBusinessObject boItem : bos) {
						IBusinessLogic<?>[] tmpLogics = this.analyzeContracts(boItem);
						// 记录父项
						for (IBusinessLogic<?> tmpLogic : tmpLogics) {
							if (tmpLogic instanceof BusinessLogic<?, ?>) {
								BusinessLogic<?, ?> logic = (BusinessLogic<?, ?>) tmpLogic;
								logic.setParent(bo);
							}
						}
						contracts.addAll(tmpLogics);
					}
				}
			}
		}
		// 分析数据有哪些契约
		if (bo instanceof IBusinessLogicsHost) {
			IBusinessLogicsHost boHost = (IBusinessLogicsHost) bo;
			IBusinessLogicContract[] hostContracts = boHost.getContracts();
			if (hostContracts != null) {
				for (IBusinessLogicContract contract : hostContracts) {
					Class<?> tmpClass = contract.getClass();// 开始检查契约
					while (tmpClass != null) {
						for (Class<?> item : tmpClass.getInterfaces()) {
							boolean exists = false;
							for (Class<?> subItem : item.getInterfaces()) {
								if (subItem.equals(IBusinessLogicContract.class)) {
									// 业务逻辑契约的扩展类型
									exists = true;
									break;
								}
							}
							if (exists) {
								// 存在契约，创建契约对应的逻辑实例
								IBusinessLogic<?> logic = logicsManager.createLogic(item);
								if (logic == null) {
									throw new NotFoundBusinessLogicException(item.getName());
								}
								if (logic instanceof BusinessLogic<?, ?>) {
									BusinessLogic<?, ?> aLogic = (BusinessLogic<?, ?>) logic;
									aLogic.setContract(contract);
									aLogic.setHost(boHost);
									aLogic.setRepository(this.getRepository());
									aLogic.setLogicChain(this);
								}
								contracts.add(logic);
							}
						}
						// 检查基类的契约
						tmpClass = tmpClass.getSuperclass();
					}
				}
			}
		}
		return contracts.toArray(new IBusinessLogic<?>[] {});
	}

	@SuppressWarnings("unchecked")
	public <B> B fetchBeAffected(ICriteria criteria, Class<B> type) {
		Iterator<IBusinessLogic<?>> logics = this.getAllLogics();
		while (logics.hasNext()) {
			IBusinessLogic<?> logic = logics.next();
			if (logic == null) {
				// 无效值
				continue;
			}
			if (logic.getBeAffected() == null) {
				// 无效值
				continue;
			}
			if (!type.isInstance(logic.getBeAffected())) {
				// 类型不符
				continue;
			}
			if (this.judge(logic.getBeAffected(), criteria)) {
				// 值比较通过
				return (B) logic.getBeAffected();
			}
		}
		return null;
	}

	private boolean judge(Object data, ICriteria criteria) {
		try {
			JudgmentLink judgmentLink = ExpressionFactory.create().createBOJudgmentLink(criteria.getConditions());
			if (judgmentLink.judge(data)) {
				boolean pass = true;
				if (!criteria.getChildCriterias().isEmpty()) {
					// 存在子项查询
					for (IChildCriteria child : criteria.getChildCriterias()) {
						try {
							Method method = data.getClass().getMethod("get" + child.getPropertyPath());
							if (method == null) {
								// 对象没有带比较的属性
								pass = false;
							}
							Object pData = method.invoke(data);
							if (pData == null) {
								// 属性没有值，不能进行比较
								pass = false;
							}
							if (pData instanceof Iterable) {
								for (Object pDataItem : (Iterable<?>) pData) {
									pass = this.judge(pDataItem, child);
									if (!pass) {
										// 比较不通过，后续不在处理
										break;
									}
								}
							} else {
								pass = this.judge(pData, child);
							}
							if (!pass) {
								// 比较不通过，后续不在处理
								break;
							}
						} catch (Exception e) {
							Logger.log(e);
						}
					}
					return pass;
				}
				return pass;
			}
		} catch (JudmentOperationException e) {
			Logger.log(e);
		}
		return false;
	}

	@Override
	public void forwardLogics() {
		// 执行正向逻辑
		for (IBusinessLogic<?> logic : this.getTriggerLogics()) {
			if (logic == null) {
				// 无效值
				continue;
			}
			logic.forward();
		}
	}

	@Override
	public void reverseLogics() {
		// 执行反向逻辑
		for (IBusinessLogic<?> logic : this.getTriggerCopyLogics()) {
			if (logic == null) {
				// 无效值
				continue;
			}
			logic.reverse();
		}

	}

	@Override
	public void commit() {
		// 提交所有逻辑
		Iterator<IBusinessLogic<?>> logics = this.getAllLogics();
		while (logics.hasNext()) {
			IBusinessLogic<?> logic = logics.next();
			if (logic == null) {
				// 无效值
				continue;
			}
			logic.commit();
		}
	}

}
