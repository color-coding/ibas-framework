package org.colorcoding.ibas.bobas.rules;

import java.util.List;

import org.colorcoding.ibas.bobas.bo.IBODescription;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IManageProperties;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

public abstract class BusinessRule implements IBusinessRule {

    private ArrayList<IPropertyInfo<?>> inputProperties;

    @Override
    public final List<IPropertyInfo<?>> getInputProperties() {
        if (this.inputProperties == null) {
            this.inputProperties = new ArrayList<>();
        }
        return this.inputProperties;
    }

    private ArrayList<IPropertyInfo<?>> affectedProperties;

    @Override
    public final List<IPropertyInfo<?>> getAffectedProperties() {
        if (this.affectedProperties == null) {
            this.affectedProperties = new ArrayList<>();
        }
        return this.affectedProperties;
    }

    @Override
    public final void execute(IBusinessObjectBase bo) throws BusinessRuleException {
        try {
            BusinessRuleContext context = new BusinessRuleContext();
            context.setRule(this);
            context.setBO(bo);
            // 赋值输入属性
            if (bo instanceof IManageProperties) {
                IManageProperties boProperties = (IManageProperties) bo;
                for (IPropertyInfo<?> propertyInfo : this.getInputProperties()) {
                    Object value = boProperties.getProperty(propertyInfo);
                    context.getInputPropertyValues().put(propertyInfo, value);
                }
            }
            // 执行规则
            RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_RULES_EXECUTING, bo, this.getClass().getName(),
                    this.getName());
            this.execute(context);
            // 赋值输出属性
            if (bo instanceof IManageProperties) {
                IManageProperties boProperties = (IManageProperties) bo;
                for (IPropertyInfo<?> propertyInfo : this.getAffectedProperties()) {
                    @SuppressWarnings("unchecked")
                    IPropertyInfo<Object> property = (IPropertyInfo<Object>) propertyInfo;
                    if (context.getOutputPropertyValues().containsKey(propertyInfo)) {
                        Object value = context.getOutputPropertyValues().get(propertyInfo);
                        boProperties.setProperty(property, value);
                    }
                }
            }
        } catch (Exception e) {
            String boMsg = null;
            // 获取业务对象实例的描述
            if (bo instanceof IBODescription) {
                IBODescription description = (IBODescription) bo;
                boMsg = description.getDescription(this);
            }
            // 没有描述取默认
            if (boMsg == null || boMsg.isEmpty())
                boMsg = bo.toString();
            throw new BusinessRuleException(
                    i18n.prop("msg_bobas_bo_executing_business_rule_faild", boMsg, this.getName(), e.getMessage()));
        }
    }

    @Override
    public String toString() {
        return String.format("{business rule: %s}", this.getName());
    }

    /**
     * 执行业务逻辑
     * 
     * @param context
     *            内容
     */
    protected abstract void execute(BusinessRuleContext context) throws Exception;

    /**
     * 规则名称
     * 
     * @return
     */
    protected abstract String getName();
}
