package org.colorcoding.ibas.bobas.rule;

/**
 * 检查业务规则
 * 
 * @author Niuren.Zhu
 *
 */
public interface ICheckRules {

    /**
     * 检查逻辑
     * 
     * @throws BusinessRuleException
     */
    void check() throws BusinessRuleException;
}
