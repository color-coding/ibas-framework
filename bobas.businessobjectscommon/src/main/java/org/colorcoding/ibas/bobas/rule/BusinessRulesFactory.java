package org.colorcoding.ibas.bobas.rule;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

/**
 * 业务规则工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRulesFactory extends ConfigurableFactory<IBusinessRulesManager> {
	private BusinessRulesFactory() {
	}

	private volatile static BusinessRulesFactory instance;

	public synchronized static BusinessRulesFactory create() {
		if (instance == null) {
			synchronized (BusinessRulesFactory.class) {
				if (instance == null) {
					instance = new BusinessRulesFactory();
				}
			}
		}
		return instance;
	}

	private volatile static IBusinessRulesManager defaultManager = null;

	@Override
	protected IBusinessRulesManager createDefault(String typeName) {
		return new BusinessRulesManager();
	}

	public synchronized IBusinessRulesManager createManager() throws BusinessRuleException {
		if (defaultManager == null) {
			defaultManager = this.create(MyConfiguration.CONFIG_ITEM_BUSINESS_RULES_WAY, "BusinessRulesManager");
		}
		return defaultManager;

	}

}
