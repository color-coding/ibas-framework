package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;
import java.util.function.Predicate;

import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCollection;

/**
 * 业务规则-集合元素属性求和
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleSumElements extends BusinessRuleCollection {

	protected BusinessRuleSumElements() {
		this.setName(I18N.prop("msg_bobas_business_rule_sum_elements"));
	}

	/**
	 * 构造方法
	 * 
	 * @param affectedProperty   属性-求和
	 * @param collectionProperty 属性-集合
	 * @param summingProperty    属性-被求和
	 */
	public BusinessRuleSumElements(IPropertyInfo<BigDecimal> affectedProperty, IPropertyInfo<?> collectionProperty,
			IPropertyInfo<BigDecimal> summingProperty) {
		this();
		this.setCollection(collectionProperty);
		this.setResult(affectedProperty);
		this.setSumming(summingProperty);
		// 要输入的参数
		this.getInputProperties().add(this.getSumming());
		// 结果
		this.getAffectedProperties().add(this.getResult());
	}

	/**
	 * 构造方法
	 * 
	 * @param affectedProperty   属性-求和
	 * @param collectionProperty 属性-集合
	 * @param summingProperty    属性-被求和
	 * @param filter             集合元素过滤器，true保留；false过滤
	 */
	public <T> BusinessRuleSumElements(IPropertyInfo<BigDecimal> affectedProperty, IPropertyInfo<?> collectionProperty,
			IPropertyInfo<BigDecimal> summingProperty, Predicate<T> filter) {
		this(affectedProperty, collectionProperty, summingProperty);
		this.setCollectionFilter(filter);
	}

	private IPropertyInfo<BigDecimal> result;

	public final IPropertyInfo<BigDecimal> getResult() {
		return result;
	}

	public final void setResult(IPropertyInfo<BigDecimal> result) {
		this.result = result;
	}

	private IPropertyInfo<BigDecimal> summing;

	public final IPropertyInfo<BigDecimal> getSumming() {
		return summing;
	}

	public final void setSumming(IPropertyInfo<BigDecimal> summing) {
		this.summing = summing;
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		BigDecimal result = Decimals.VALUE_ZERO;
		Object[] values = context.getInputValues().get(this.getSumming());
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value instanceof BigDecimal) {
					result = Decimals.add(result, (BigDecimal) value);
				}
			}
		}
		context.getOutputValues().put(this.getResult(), result);
	}

}
