package org.colorcoding.ibas.bobas.logic;

import java.util.HashMap;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.repository.ITransaction;

/**
 * 业务逻辑管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsManager {

	private BusinessLogicsManager() {
		this.logicClasses = new HashMap<>(128);
	}

	private volatile static BusinessLogicsManager instance;

	public synchronized static BusinessLogicsManager create() {
		if (instance == null) {
			synchronized (BusinessLogicsManager.class) {
				if (instance == null) {
					instance = new BusinessLogicsManager();
				}
			}
		}
		return instance;
	}

	public synchronized IBusinessLogicChain createChain(ITransaction transaction) {
		return new BusinessLogicChain(transaction);
	}

	private HashMap<Class<?>, Class<?>> logicClasses;

	public synchronized BusinessLogic<?, ?> createLogic(Class<?> contract) {
		Class<?> logicClass = this.logicClasses.get(contract);
		if (logicClass == null) {
			// 优先从契约接口所在命名空间查询
			Class<?>[] packClass = BOFactory.loadClasses(contract.getPackage().getName());
			for (Class<?> item : packClass) {
				// 检查是否标记契约
				LogicContract annotation = item.getAnnotation(LogicContract.class);
				if (annotation != null) {
					// 检查是否有效（派生自BusinessLogic）
					Class<?> tmpClass = item;
					while (tmpClass != null) {
						if (tmpClass.equals(BusinessLogic.class)) {
							// 有效的业务逻辑类
							this.logicClasses.put(annotation.value(), item);
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
		if (logicClass != null && BusinessLogic.class.isAssignableFrom(logicClass)) {
			try {
				return (BusinessLogic<?, ?>) logicClass.newInstance();
			} catch (Exception e) {
				throw new BusinessLogicException(e);
			}
		}
		return null;
	}

}
