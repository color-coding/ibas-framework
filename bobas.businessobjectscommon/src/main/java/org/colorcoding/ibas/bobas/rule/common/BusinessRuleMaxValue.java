package org.colorcoding.ibas.bobas.rule.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-最大值检查
 * 
 * @author Niuren.Zhu
 *
 * @param <T> 值类型，需要实现Comparable
 */
public class BusinessRuleMaxValue<T extends Comparable<?>> extends BusinessRuleCommon {

	protected BusinessRuleMaxValue() {
		this.setName(I18N.prop("msg_bobas_business_rule_max_value"));
	}

	/**
	 * 构造
	 * 
	 * @param maxValue      最大值
	 * @param propertyInfos 属性数组
	 */
	@SafeVarargs
	public BusinessRuleMaxValue(T maxValue, IPropertyInfo<T>... propertyInfos) {
		this();
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
	@SuppressWarnings("unchecked")
	protected void execute(BusinessRuleContext context) throws Exception {
		if (this.getMaxValue() == null) {
			// 比较值为空，则永远成立
			return;
		}
		T value = null;
		Comparable<T> maxValue = (Comparable<T>) this.getMaxValue();
		for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputValues().entrySet()) {
			if (entry.getValue() == null) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
			}
			value = (T) entry.getValue();
			if (maxValue.compareTo(value) < 0) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_max_value_error", entry.getKey().getName(),
						value, this.getMaxValue()));
			}
		}

	}

}
