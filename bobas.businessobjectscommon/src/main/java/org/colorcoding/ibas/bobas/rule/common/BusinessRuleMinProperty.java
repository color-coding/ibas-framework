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
public class BusinessRuleMinProperty<T extends Comparable<?>> extends BusinessRuleCommon {

	protected BusinessRuleMinProperty() {
		this.setName(I18N.prop("msg_bobas_business_rule_min_property"));
	}

	/**
	 * 构造
	 * 
	 * @param minProperty   最小值属性
	 * @param propertyInfos 属性数组
	 */
	@SafeVarargs
	public BusinessRuleMinProperty(IPropertyInfo<T> minProperty, IPropertyInfo<T>... propertyInfos) {
		this();
		this.setMinProperty(minProperty);
		// 要输入的参数
		if (propertyInfos != null) {
			for (IPropertyInfo<?> item : propertyInfos) {
				this.getInputProperties().add(item);
			}
		}
		this.getInputProperties().add(this.getMinProperty());
	}

	private IPropertyInfo<T> minProperty;

	public final IPropertyInfo<T> getMinProperty() {
		return minProperty;
	}

	public final void setMinProperty(IPropertyInfo<T> minProperty) {
		this.minProperty = minProperty;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void execute(BusinessRuleContext context) throws Exception {
		if (this.getMinProperty() == null) {
			return;
		}
		Comparable<T> minValue = (Comparable<T>) context.getInputValues().get(this.getMinProperty());
		if (minValue == null) {
			return;
		}
		T value = null;
		for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputValues().entrySet()) {
			if (entry.getKey() == this.getMinProperty()) {
				continue;
			}
			if (entry.getValue() == null) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_required_error", entry.getKey().getName()));
			}
			value = (T) entry.getValue();
			if (minValue.compareTo(value) > 0) {
				throw new Exception(I18N.prop("msg_bobas_business_rule_min_property_error", entry.getKey().getName(),
						this.getMinProperty().getName()));
			}
		}
	}

}
