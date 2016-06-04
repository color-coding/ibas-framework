package org.colorcoding.ibas.bobas.logics;

/**
 * 业务逻辑工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicFactory {
	private volatile static BusinessLogicFactory instance;

	public static BusinessLogicFactory create() {
		if (instance == null) {
			synchronized (BusinessLogicFactory.class) {
				if (instance == null) {
					instance = new BusinessLogicFactory();
				}
			}
		}
		return instance;
	}

	private BusinessLogicFactory() {
	}

	public IBusinessLogic createLogic(IBusinessLogicContract contract) {
		return this.createLogic(contract.getClass());
	}

	public IBusinessLogic createLogic(Class<?> contractType) {
		// TODO 获取契约对应的逻辑

		throw new NotFoundBusinessLogicException(contractType.getName());
	}
}
