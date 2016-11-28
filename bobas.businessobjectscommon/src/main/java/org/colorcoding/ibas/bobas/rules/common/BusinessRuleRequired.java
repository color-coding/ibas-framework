package org.colorcoding.ibas.bobas.rules.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.rules.BusinessRule;
import org.colorcoding.ibas.bobas.rules.BusinessRuleContext;

/**
 * 业务规则-要求值检查
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleRequired extends BusinessRule {

    /**
     * 构造
     * 
     * @param propertyInfo
     *            要求值的属性
     * @param propertyInfos
     *            要求值的属性数组
     */
    public BusinessRuleRequired(IPropertyInfo<?> propertyInfo, IPropertyInfo<?>... propertyInfos) {
        // 要输入的参数
        this.getInputProperties().add(propertyInfo);
        if (propertyInfos != null) {
            for (IPropertyInfo<?> item : propertyInfos) {
                this.getInputProperties().add(item);
            }
        }
    }

    @Override
    protected String getName() {
        return i18n.prop("msg_bobas_business_rule_required");
    }

    @Override
    protected void execute(BusinessRuleContext context) throws Exception {
        for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputPropertyValues().entrySet()) {
            if (entry.getValue() == null) {
                throw new Exception(i18n.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
            }
        }
    }

}
