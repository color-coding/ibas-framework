package org.colorcoding.ibas.bobas.rules;

import java.util.List;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 业务规则
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessRule {

    /**
     * 运行业务规则
     * 
     * @param bo
     *            执行规则的业务对象
     * @param property
     *            变化的属性
     */
    void execute(IBusinessObjectBase bo) throws BusinessRuleException;

    /**
     * 输入的属性集合
     * 
     * @return
     */
    List<IPropertyInfo<?>> getInputProperties();

    /**
     * 被影响的属性集合
     * 
     * @return
     */
    List<IPropertyInfo<?>> getAffectedProperties();
}
