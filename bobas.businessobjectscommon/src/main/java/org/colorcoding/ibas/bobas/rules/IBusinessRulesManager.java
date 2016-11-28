package org.colorcoding.ibas.bobas.rules;

/**
 * 业务规则管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessRulesManager {

    /**
     * 获取规则
     * 
     * @param type
     * @return
     */
    IBusinessRules getRules(Class<?> type);
}
