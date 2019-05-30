package org.colorcoding.ibas.bobas.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.mapping.LogicContract;

/**
 * 业务逻辑管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsManager implements IBusinessLogicsManager {

	@Override
	public synchronized IBusinessLogicChain createChain() {
		IBusinessLogicChain logicChain = new BusinessLogicChain(this);
		this.getLogicChains().add(logicChain);
		return logicChain;
	}

	@Override
	public synchronized IBusinessLogicChain getChain(IBusinessObject host) {
		if (host == null) {
			return null;
		}
		for (int i = this.getLogicChains().size() - 1; i >= 0; i--) {
			IBusinessLogicChain logicChain = this.getLogicChains().get(i);
			if (logicChain == null) {
				continue;
			}
			if (logicChain.getTrigger() != host) {
				continue;
			}
			return logicChain;
		}
		return null;
	}

	@Override
	public synchronized void closeChains(String transId) {
		if (transId == null) {
			return;
		}

		for (int i = this.getLogicChains().size() - 1; i >= 0; i--) {
			IBusinessLogicChain logicChain = this.getLogicChains().get(i);
			if (logicChain == null) {
				continue;
			}
			if (!transId.equals(logicChain.getGroup())) {
				continue;
			}
			this.getLogicChains().remove(i);
		}
	}

	private volatile List<IBusinessLogicChain> logicChains;

	protected List<IBusinessLogicChain> getLogicChains() {
		if (logicClasses == null) {
			synchronized (this) {
				if (this.logicChains == null) {
					this.logicChains = new Vector<>();
				}
			}
		}
		return this.logicChains;
	}

	private volatile HashMap<Class<?>, Class<?>> logicClasses;

	protected HashMap<Class<?>, Class<?>> getLogicClasses() {
		if (logicClasses == null) {
			synchronized (this) {
				if (logicClasses == null) {
					logicClasses = new HashMap<>();
				}
			}
		}
		return logicClasses;
	}

	@Override
	public IBusinessLogic<?> createLogic(Class<?> contract) {
		if (contract == null) {
			return null;
		}
		Class<?> logicClass = this.getLogicClasses().get(contract);
		if (logicClass == null) {
			// 优先从契约接口所在命名空间查询
			Class<?>[] packClass = BOFactory.create().loadClasses(contract.getPackage().getName());
			for (Class<?> item : packClass) {
				// 检查是否标记契约
				LogicContract annotation = item.getAnnotation(LogicContract.class);
				if (annotation != null) {
					// 检查是否有效（派生自BusinessLogic）
					Class<?> tmpClass = item;
					while (tmpClass != null) {
						if (tmpClass.equals(BusinessLogic.class)) {
							// 有效的业务逻辑类
							this.getLogicClasses().put(annotation.value(), item);
							if (logicClass == null && contract.equals(annotation.value())) {
								logicClass = item;
							}
							break;
						}
						tmpClass = tmpClass.getSuperclass();
					}
				}
			}
		}
		if (logicClass != null) {
			// 创建实例
			try {
				return (IBusinessLogic<?>) logicClass.newInstance();
			} catch (Exception e) {
				throw new NotFoundBusinessLogicException(e);
			}
		}
		return null;
	}

}
