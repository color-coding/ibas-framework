package org.colorcoding.ibas.bobas.rule;

import java.util.HashMap;

/**
 * 业务规则管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRulesManager {

	private BusinessRulesManager() {
	}

	private volatile static BusinessRulesManager instance;

	public synchronized static BusinessRulesManager create() {
		if (instance == null) {
			synchronized (BusinessRulesManager.class) {
				if (instance == null) {
					instance = new BusinessRulesManager();
				}
			}
		}
		return instance;
	}

	private volatile HashMap<Class<?>, IBusinessRules> rules;

	/**
	 * 配置项
	 */
	protected final HashMap<Class<?>, IBusinessRules> getRules() {
		if (rules == null) {
			synchronized (this) {
				if (rules == null) {
					rules = new HashMap<Class<?>, IBusinessRules>(256);
				}
			}
		}
		return rules;
	}

	public final IBusinessRules getRules(Class<?> type) {
		if (type == null) {
			return null;
		}
		synchronized (this.getRules()) {
			IBusinessRules businessRules = this.getRules().get(type);
			if (businessRules == null) {
				businessRules = new BusinessRules();
				this.getRules().put(type, businessRules);
				return businessRules;
			}
			return businessRules;
		}
	}

}
