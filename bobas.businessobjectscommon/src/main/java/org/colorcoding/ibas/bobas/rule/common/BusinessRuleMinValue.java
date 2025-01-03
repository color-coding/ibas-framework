package org.colorcoding.ibas.bobas.rule.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-最小值检查
 * 
 * @author Niuren.Zhu
 *
 * @param <T> 值类型，需要实现Comparable
 */
public class BusinessRuleMinValue<T extends Comparable<?>> extends BusinessRuleCommon {

	protected BusinessRuleMinValue() {
		this.setName(I18N.prop("msg_bobas_business_rule_min_value"));
	}

	/**
	 * 构造
	 * 
	 * @param minValue      最小值
	 * @param propertyInfos 属性数组
	 */
	@SafeVarargs
	public BusinessRuleMinValue(T minValue, IPropertyInfo<T>... propertyInfos) {
		this();
		this.setMinValue(minValue);
		// 要输入的参数
		if (propertyInfos != null) {
			for (IPropertyInfo<?> item : propertyInfos) {
				this.getInputProperties().add(item);
			}
		}
	}

	private T minValue;

	public final T getMinValue() {
		return minValue;
	}

	public final void setMinValue(T minValue) {
		this.minValue = minValue;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void execute(BusinessRuleContext context) throws Exception {
		if (this.getMinValue() == null) {
			// 比较值为空，则永远成立
			return;
		}
		T value = null;
		Comparable<T> minValue = (Comparable<T>) this.getMinValue();
		for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputValues().entrySet()) {
			if (entry.getValue() == null) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
			}
			value = (T) entry.getValue();
			if (minValue.compareTo(value) > 0) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_min_value_error", entry.getKey().getName(),
						value, this.getMinValue()));
			}
		}
	}

}
