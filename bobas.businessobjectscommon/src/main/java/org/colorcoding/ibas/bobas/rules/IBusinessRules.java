package org.colorcoding.ibas.bobas.rules;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 业务规则集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessRules {

    /**
     * 是否已初始化
     * 
     * @return
     */
    boolean isInitialized();

    /**
     * 注册规则
     * 
     * @param rules
     */
    void registerRules(IBusinessRule[] rules);

    /**
     * 运行业务规则
     * 
     * @param bo
     *            执行规则的业务对象
     * @param property
     *            变化的属性
     */
    void execute(IBusinessObjectBase bo, IPropertyInfo<?>... properties) throws BusinessRuleException;

}
