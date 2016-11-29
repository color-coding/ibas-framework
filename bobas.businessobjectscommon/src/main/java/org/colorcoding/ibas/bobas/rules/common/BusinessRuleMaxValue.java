package org.colorcoding.ibas.bobas.rules.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.rules.BusinessRule;
import org.colorcoding.ibas.bobas.rules.BusinessRuleContext;

/**
 * 业务规则-最小值检查
 * 
 * @author Niuren.Zhu
 *
 * @param <T>
 *            值类型，需要实现Comparable
 */
public class BusinessRuleMaxValue<T extends Comparable<?>> extends BusinessRule {

    /**
     * 构造
     * 
     * @param maxValue
     *            最小值
     * @param propertyInfos
     *            要求值的属性数组
     */
    @SafeVarargs
    public BusinessRuleMaxValue(T maxValue, IPropertyInfo<T>... propertyInfos) {
        this.setMaxValue(maxValue);
        // 要输入的参数
        if (propertyInfos != null) {
            for (IPropertyInfo<?> item : propertyInfos) {
                this.getInputProperties().add(item);
            }
        }
    }

    private T maxValue;

    public final T getMaxValue() {
        return maxValue;
    }

    public final void setMaxValue(T maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    protected String getName() {
        return i18n.prop("msg_bobas_business_rule_max_value");
    }

    @Override
    protected void execute(BusinessRuleContext context) throws Exception {
        if (this.getMaxValue() == null) {
            // 比较值为空，则永远成立
            return;
        }
        for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputPropertyValues().entrySet()) {
            if (entry.getValue() == null) {
                throw new Exception(i18n.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
            }
            @SuppressWarnings("unchecked")
            T value = (T) entry.getValue();
            @SuppressWarnings("unchecked")
            Comparable<T> maxValue = (Comparable<T>) this.getMaxValue();
            if (maxValue.compareTo(value) < 0) {
                throw new Exception(i18n.prop("msg_bobas_business_rule_max_value_error", entry.getKey().getName(),
                        value, this.getMaxValue()));
            }
        }

    }

}
