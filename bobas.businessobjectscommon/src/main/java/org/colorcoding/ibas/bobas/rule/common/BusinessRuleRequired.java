package org.colorcoding.ibas.bobas.rule.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;
import org.colorcoding.ibas.bobas.rule.BusinessRuleException;

/**
 * 业务规则-必填值检查
 *
 * null、空字符串、最小日期(DateTimes.VALUE_MIN)均视为未填写
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleRequired extends BusinessRuleCommon {

	protected BusinessRuleRequired() {
		this.setName(I18N.prop("msg_bobas_business_rule_required"));
	}

	/**
	 * 构造
	 * 
	 * @param propertyInfo  要求值的属性
	 * @param propertyInfos 要求值的属性数组
	 */
	public BusinessRuleRequired(IPropertyInfo<?> propertyInfo, IPropertyInfo<?>... propertyInfos) {
		this();
		// 要输入的参数
		this.getInputProperties().add(propertyInfo);
		if (propertyInfos != null) {
			for (IPropertyInfo<?> item : propertyInfos) {
				this.getInputProperties().add(item);
			}
		}
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputValues().entrySet()) {
			if (entry.getValue() == null) {
				throw new BusinessRuleException(I18N.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
			}
			if (entry.getKey().getValueType() == String.class) {
				if (entry.getValue() == Strings.VALUE_EMPTY || entry.getValue().toString().isEmpty()) {
					throw new BusinessRuleException(I18N.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
				}
			} else if (entry.getKey().getValueType() == DateTimes.class) {
				if (entry.getValue() == DateTimes.VALUE_MIN) {
					throw new BusinessRuleException(I18N.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
				}
			}
		}
	}

}
