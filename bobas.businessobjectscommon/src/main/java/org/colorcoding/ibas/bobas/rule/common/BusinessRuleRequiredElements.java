package org.colorcoding.ibas.bobas.rule.common;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-要求集合有元素
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleRequiredElements extends BusinessRuleCommon {

	protected BusinessRuleRequiredElements() {
		this.setName(I18N.prop("msg_bobas_business_rule_required_elements"));
	}

	/**
	 * 构造
	 * 
	 * @param propertyInfo  要求元素的属性
	 * @param propertyInfos 要求元素的属性数组
	 */
	public BusinessRuleRequiredElements(IPropertyInfo<?> propertyInfo, IPropertyInfo<?>... propertyInfos) {
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
		// 主对象删除时，不检查
		if (context.getSource().isDeleted()) {
			return;
		}
		// 标记删除，不检查
		if (context.getSource() instanceof IBOTagDeleted) {
			if (((IBOTagDeleted) context.getSource()).getDeleted() == emYesNo.YES) {
				return;
			}
		}
		for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputValues().entrySet()) {
			if (entry.getValue() == null) {
				throw new Exception(
						I18N.prop("msg_bobas_business_rule_required_elements_error", entry.getKey().getName()));
			}
			Class<?> valueType = entry.getValue().getClass();
			if (valueType.isArray()) {
				// 数组
				if (Array.getLength(entry.getValue()) == 0)
					throw new Exception(
							I18N.prop("msg_bobas_business_rule_required_elements_error", entry.getKey().getName()));
			} else if (entry.getValue() instanceof IBusinessObjects<?, ?>) {
				// 是业务对象集合，非删除的
				IBusinessObjects<?, ?> objects = (IBusinessObjects<?, ?>) entry.getValue();
				if (objects.where(c -> c.isSavable() == true && c.isDeleted() == false).size() == 0)
					throw new Exception(
							I18N.prop("msg_bobas_business_rule_required_elements_error", entry.getKey().getName()));
			} else if (entry.getValue() instanceof Collection<?>) {
				// 是集合
				Collection<?> collection = (Collection<?>) entry.getValue();
				if (collection.isEmpty())
					throw new Exception(
							I18N.prop("msg_bobas_business_rule_required_elements_error", entry.getKey().getName()));
			}
		}
	}

}
