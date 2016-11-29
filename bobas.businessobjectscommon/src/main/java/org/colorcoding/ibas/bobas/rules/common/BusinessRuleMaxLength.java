package org.colorcoding.ibas.bobas.rules.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.rules.BusinessRule;
import org.colorcoding.ibas.bobas.rules.BusinessRuleContext;

/**
 * 业务规则-最大字符长度
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleMaxLength extends BusinessRule {

    /**
     * 构造
     * 
     * @param maxLength
     *            最大长度
     * @param propertyInfos
     *            要求值的属性数组
     */
    public BusinessRuleMaxLength(int maxLength, IPropertyInfo<?>... propertyInfos) {
        this.setMaxLength(maxLength);
        // 要输入的参数
        if (propertyInfos != null) {
            for (IPropertyInfo<?> item : propertyInfos) {
                this.getInputProperties().add(item);
            }
        }
    }

    private int maxLength;

    public final int getMaxLength() {
        return maxLength;
    }

    public final void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    protected String getName() {
        return i18n.prop("msg_bobas_business_rule_max_length");
    }

    @Override
    protected void execute(BusinessRuleContext context) throws Exception {
        for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputPropertyValues().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (String.valueOf(entry.getValue()).length() > this.getMaxLength()) {
                throw new Exception(i18n.prop("msg_bobas_business_rule_max_length_error", entry.getKey().getName(),
                        entry.getValue(), this.getMaxLength()));
            }
        }
    }

}
