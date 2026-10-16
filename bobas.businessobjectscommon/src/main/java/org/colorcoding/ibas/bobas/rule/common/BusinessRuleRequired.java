package org.colorcoding.ibas.bobas.rule.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.bo.BOIdentifierBuilder;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

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
				throw this.createException(context.getSource(), "msg_bobas_business_rule_required_error",
						BOIdentifierBuilder.propertyReference(context.getSource(), entry.getKey()));
			}
			if (entry.getKey().getValueType() == String.class) {
				if (entry.getValue() == Strings.VALUE_EMPTY || entry.getValue().toString().isEmpty()) {
					throw this.createException(context.getSource(), "msg_bobas_business_rule_required_error",
							BOIdentifierBuilder.propertyReference(context.getSource(), entry.getKey()));
				}
			} else if (entry.getKey().getValueType() == DateTime.class) {
				if (DateTimes.VALUE_MIN.equals(entry.getValue())) {
					throw this.createException(context.getSource(), "msg_bobas_business_rule_required_error",
							BOIdentifierBuilder.propertyReference(context.getSource(), entry.getKey()));
				}
			}
		}
	}

}
