package org.colorcoding.ibas.bobas.rule.common;

import java.util.Map;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-去空格
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleTrim extends BusinessRuleCommon {

	protected BusinessRuleTrim() {
		this.setName(I18N.prop("msg_bobas_business_rule_trim"));
	}

	/**
	 * 构造
	 * 
	 * @param propertyInfo  去空格的属性
	 * @param propertyInfos 去空格的属性数组
	 */
	public BusinessRuleTrim(IPropertyInfo<?> propertyInfo, IPropertyInfo<?>... propertyInfos) {
		this();
		// 要输入的参数
		this.getInputProperties().add(propertyInfo);
		if (propertyInfos != null) {
			for (IPropertyInfo<?> item : propertyInfos) {
				this.getInputProperties().add(item);
			}
		}
		// 可能被影响的参数
		this.getAffectedProperties().add(propertyInfo);
		if (propertyInfos != null) {
			for (IPropertyInfo<?> item : propertyInfos) {
				this.getAffectedProperties().add(item);
			}
		}
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		for (Map.Entry<IPropertyInfo<?>, Object> entry : context.getInputValues().entrySet()) {
			if (entry.getValue() instanceof String) {
				String nValue = (String) entry.getValue();
				if (!nValue.isEmpty()) {
					nValue = nValue.trim();
					if (!nValue.equals(entry.getValue())) {
						context.getOutputValues().put(entry.getKey(), nValue);
					}
				}
			}
		}
	}

}
