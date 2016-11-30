package org.colorcoding.ibas.bobas.rules;

import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 业务规则执行内容
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleContext {

    private IBusinessRule rule;

    /**
     * 运行的规则
     * 
     * @return
     */
    public final IBusinessRule getRule() {
        return rule;
    }

    final void setRule(IBusinessRule rule) {
        this.rule = rule;
    }

    private IBusinessObjectBase bo;

    /**
     * 运行规则的对象
     * 
     * @return
     */
    public final IBusinessObjectBase getBO() {
        return bo;
    }

    final void setBO(IBusinessObjectBase bo) {
        this.bo = bo;
    }

    private Map<IPropertyInfo<?>, Object> inputPropertyValues;

    public final Map<IPropertyInfo<?>, Object> getInputPropertyValues() {
        if (this.inputPropertyValues == null) {
            this.inputPropertyValues = new HashMap<>(8);
        }
        return this.inputPropertyValues;
    }

    private Map<IPropertyInfo<?>, Object> outputPropertyValues;

    public final Map<IPropertyInfo<?>, Object> getOutputPropertyValues() {
        if (this.outputPropertyValues == null) {
            this.outputPropertyValues = new HashMap<>(8);
        }
        return this.outputPropertyValues;
    }

}
