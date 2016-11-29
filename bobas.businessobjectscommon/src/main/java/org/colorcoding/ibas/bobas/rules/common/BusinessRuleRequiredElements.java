package org.colorcoding.ibas.bobas.rules.common;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.rules.BusinessRule;
import org.colorcoding.ibas.bobas.rules.BusinessRuleContext;

/**
 * 业务规则-要求集合有元素
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleRequiredElements extends BusinessRule {

    /**
     * 构造
     * 
     * @param propertyInfo
     *            要求元素的属性
     * @param propertyInfos
     *            要求元素的属性数组
     */
    public BusinessRuleRequiredElements(IPropertyInfo<?> propertyInfo, IPropertyInfo<?>... propertyInfos) {
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
        return i18n.prop("msg_bobas_business_rule_required_elements");
    }

    @Override
    protected void execute(BusinessRuleContext context) throws Exception {
        for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputPropertyValues().entrySet()) {
            if (entry.getValue() == null) {
                throw new Exception(
                        i18n.prop("msg_bobas_business_rule_required_elements_error", entry.getKey().getName()));
            }
            Class<?> valueType = entry.getValue().getClass();
            if (valueType.isArray()) {
                // 数组
                if (Array.getLength(entry.getValue()) == 0)
                    throw new Exception(
                            i18n.prop("msg_bobas_business_rule_required_elements_error", entry.getKey().getName()));
            } else if (entry.getValue() instanceof Collection<?>) {
                // 是集合
                Collection<?> collection = (Collection<?>) entry.getValue();
                if (collection.isEmpty())
                    throw new Exception(
                            i18n.prop("msg_bobas_business_rule_required_elements_error", entry.getKey().getName()));
            }
        }
    }

}
