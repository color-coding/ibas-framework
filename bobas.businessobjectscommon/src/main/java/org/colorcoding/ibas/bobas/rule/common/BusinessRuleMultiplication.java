package org.colorcoding.ibas.bobas.rule.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-乘法运算
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleMultiplication extends BusinessRuleCommon {

	public BusinessRuleMultiplication() {
		this.setName(I18N.prop("msg_bobas_business_rule_multiplication"));
	}

	/**
	 * 构造方法
	 * 
	 * @param result
	 *            属性-结果
	 * @param dividend
	 *            属性-被乘数
	 * @param divisor
	 *            属性-乘数
	 */
	public BusinessRuleMultiplication(IPropertyInfo<Decimal> result, IPropertyInfo<Decimal> multiplicand,
			IPropertyInfo<Decimal> multiplier) {
		this();
		this.setMultiplicand(multiplicand);
		this.setMultiplier(multiplier);
		this.setResult(result);
		// 要输入的参数
		this.getInputProperties().add(this.getMultiplicand());
		this.getInputProperties().add(this.getMultiplier());
		// 结果
		this.getAffectedProperties().add(this.getResult());
	}

	private IPropertyInfo<Decimal> multiplicand;

	public final IPropertyInfo<Decimal> getMultiplicand() {
		return multiplicand;
	}

	public final void setMultiplicand(IPropertyInfo<Decimal> multiplicand) {
		this.multiplicand = multiplicand;
	}

	private IPropertyInfo<Decimal> multiplier;

	public final IPropertyInfo<Decimal> getMultiplier() {
		return multiplier;
	}

	public final void setMultiplier(IPropertyInfo<Decimal> multiplier) {
		this.multiplier = multiplier;
	}

	private IPropertyInfo<Decimal> result;

	public final IPropertyInfo<Decimal> getResult() {
		return result;
	}

	public final void setResult(IPropertyInfo<Decimal> result) {
		this.result = result;
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		Decimal multiplicand = (Decimal) context.getInputValues().get(this.getMultiplicand());
		if (multiplicand == null) {
			multiplicand = Decimal.ZERO;
		}
		Decimal multiplier = (Decimal) context.getInputValues().get(this.getMultiplier());
		if (multiplier == null) {
			multiplier = Decimal.ZERO;
		}
		Decimal result = multiplicand.multiply(multiplier);
		// 截取精度
		result = Decimal.round(result, Decimal.RESERVED_DECIMAL_PLACES_RUNNING);
		context.getOutputValues().put(this.getResult(), result);
	}

}
