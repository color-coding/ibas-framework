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
public class BusinessRuleMaxProperty<T extends Comparable<?>> extends BusinessRuleCommon {

	protected BusinessRuleMaxProperty() {
		this.setName(I18N.prop("msg_bobas_business_rule_max_property"));
	}

	/**
	 * 构造
	 * 
	 * @param maxProperty   最大值属性
	 * @param propertyInfos 属性数组
	 */
	@SafeVarargs
	public BusinessRuleMaxProperty(IPropertyInfo<T> maxProperty, IPropertyInfo<T>... propertyInfos) {
		this();
		this.setMaxProperty(maxProperty);
		// 要输入的参数
		if (propertyInfos != null) {
			for (IPropertyInfo<?> item : propertyInfos) {
				this.getInputProperties().add(item);
			}
		}
		this.getInputProperties().add(this.getMaxProperty());
	}

	private IPropertyInfo<T> maxProperty;

	public final IPropertyInfo<T> getMaxProperty() {
		return maxProperty;
	}

	public final void setMaxProperty(IPropertyInfo<T> maxProperty) {
		this.maxProperty = maxProperty;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void execute(BusinessRuleContext context) throws Exception {
		if (this.getMaxProperty() == null) {
			return;
		}
		Comparable<T> maxValue = (Comparable<T>) context.getInputValues().get(this.getMaxProperty());
		if (maxValue == null) {
			return;
		}
		T value = null;
		for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputValues().entrySet()) {
			if (entry.getKey() == this.getMaxProperty()) {
				continue;
			}
			if (entry.getValue() == null) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
			}
			value = (T) entry.getValue();
			if (maxValue.compareTo(value) < 0) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_max_property_error", entry.getKey().getName(),
						this.getMaxProperty().getName()));
			}
		}
	}

}
