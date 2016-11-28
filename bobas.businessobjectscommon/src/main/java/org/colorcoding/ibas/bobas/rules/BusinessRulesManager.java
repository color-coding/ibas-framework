package org.colorcoding.ibas.bobas.rules;

import java.util.HashMap;

/**
 * 业务规则管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRulesManager implements IBusinessRulesManager {

    private volatile HashMap<Class<?>, IBusinessRules> rules;

    /**
     * 配置项
     */
    protected final HashMap<Class<?>, IBusinessRules> getRules() {
        if (rules == null) {
            synchronized (this) {
                if (rules == null) {
                    rules = new HashMap<Class<?>, IBusinessRules>();
                }
            }
        }
        return rules;
    }

    @Override
    public final IBusinessRules getRules(Class<?> type) {
        if (type == null) {
            return null;
        }
        if (this.getRules().containsKey(type)) {
            return this.getRules().get(type);
        }
        synchronized (this.getRules()) {
            if (!this.getRules().containsKey(type)) {
                IBusinessRules tmpRules = this.createRules(type);
                if (tmpRules != null) {
                    this.getRules().put(type, tmpRules);
                    return tmpRules;
                }
            }
        }
        return null;
    }

    protected IBusinessRules createRules(Class<?> type) {
        return new BusinessRules(type);
    }
}
