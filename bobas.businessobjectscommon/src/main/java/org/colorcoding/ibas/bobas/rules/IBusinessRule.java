package org.colorcoding.ibas.bobas.rules;

import java.util.List;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 业务规则
 * 
 * 运行时点：1.输入属性集合的属性值发生变化时。2.保存数据之前。
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
     * 仅当属于此集合的属性变化时，才运行此规则
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
