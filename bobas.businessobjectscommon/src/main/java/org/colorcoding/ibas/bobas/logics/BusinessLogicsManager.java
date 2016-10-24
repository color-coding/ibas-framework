package org.colorcoding.ibas.bobas.logics;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.mapping.LogicContract;

/**
 * 业务逻辑管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsManager implements IBusinessLogicsManager {

	@Override
	public synchronized IBusinessLogicsChain getChain(String transId) {
		if (transId != null) {
			if (this.getLogicsChains().containsKey(transId)) {
				return this.getLogicsChains().get(transId);
			}
		}
		return null;
	}

	@Override
	public synchronized IBusinessLogicsChain registerChain(String transId) {
		if (transId == null || transId.isEmpty()) {
			throw new BusinessLogicsException(i18n.prop("msg_bobas_invalid_data"));
		}
		IBusinessLogicsChain chain = new BusinessLogicsChain();
		chain.setId(transId);
		this.getLogicsChains().put(chain.getId(), chain);
		return chain;
	}

	@Override
	public synchronized boolean closeChain(String transId) {
		if (transId != null) {
			if (this.getLogicsChains().containsKey(transId)) {
				this.getLogicsChains().remove(transId);
				return true;
			}
		}
		return false;
	}

	private volatile HashMap<String, IBusinessLogicsChain> logicsChains;

	protected HashMap<String, IBusinessLogicsChain> getLogicsChains() {
		if (this.logicsChains == null) {
			synchronized (BusinessLogicsManager.class) {
				if (this.logicsChains == null) {
					this.logicsChains = new HashMap<String, IBusinessLogicsChain>();
				}
			}
		}
		return this.logicsChains;
	}

	private volatile HashMap<Class<?>, Class<?>> logicClasses;

	protected HashMap<Class<?>, Class<?>> getLogicClasses() {
		if (logicClasses == null) {
			synchronized (BusinessLogicsManager.class) {
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
		Class<?> logicClass = null;
		if (this.getLogicClasses().containsKey(contract)) {
			logicClass = this.getLogicClasses().get(contract);
		}
		if (logicClass == null) {
			// 优先从契约接口所在命名空间查询
			Class<?>[] packClass = BOFactory.create().getClasses(contract.getPackage().getName());
			for (Class<?> item : packClass) {
				// 检查是否标记契约
				Annotation annotation = item.getAnnotation(LogicContract.class);
				if (annotation != null) {
					// 检查是否有效（派生自BusinessLogic）
					Class<?> tmpClass = item;
					while (tmpClass != null) {
						if (tmpClass.equals(BusinessLogic.class)) {
							// 有效的业务逻辑类
							this.getLogicClasses().put(((LogicContract) annotation).value(), item);
							if (logicClass == null && contract.equals(((LogicContract) annotation).value())) {
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
				throw new NotFoundBusinessLogicsException(e);
			}
		}
		return null;
	}

}
