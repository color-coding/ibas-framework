package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

/**
 * 业务逻辑工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsFactory extends ConfigurableFactory<IBusinessLogicsManager> {

	private BusinessLogicsFactory() {
	}

	private volatile static BusinessLogicsFactory instance;

	public synchronized static BusinessLogicsFactory create() {
		if (instance == null) {
			synchronized (BusinessLogicsFactory.class) {
				if (instance == null) {
					instance = new BusinessLogicsFactory();
				}
			}
		}
		return instance;
	}

	private volatile static IBusinessLogicsManager defaultManager = null;

	@Override
	protected IBusinessLogicsManager createDefault(String typeName) {
		return new BusinessLogicsManager();
	}

	public synchronized IBusinessLogicsManager createManager() throws BusinessLogicException {
		if (defaultManager == null) {
			defaultManager = this.create(MyConfiguration.CONFIG_ITEM_BUSINESS_LOGICS_WAY, "BusinessLogicsManager");
		}
		return defaultManager;

	}

}
