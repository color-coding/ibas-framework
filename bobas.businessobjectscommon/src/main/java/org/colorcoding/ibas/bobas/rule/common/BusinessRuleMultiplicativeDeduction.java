package org.colorcoding.ibas.bobas.rule.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-乘法推导（根据被乘数，用乘除法推导乘数或结果）
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleMultiplicativeDeduction extends BusinessRuleCommon {

	public BusinessRuleMultiplicativeDeduction() {
		this.setName(I18N.prop("msg_bobas_business_rule_multiplicative_deduction"));
	}

	/**
	 * 构造方法
	 * 
	 * @param multiplicand
	 *            属性-被乘数
	 * @param multiplier
	 *            属性-乘数
	 * @param result
	 *            属性-结果
	 */
	public BusinessRuleMultiplicativeDeduction(IPropertyInfo<Decimal> multiplicand, IPropertyInfo<Decimal> multiplier,
			IPropertyInfo<Decimal> result) {
		this();
		this.setMultiplicand(multiplicand);
		this.setMultiplier(multiplier);
		this.setResult(result);
		// 要输入的参数
		this.getInputProperties().add(this.getMultiplicand());
		this.getInputProperties().add(this.getMultiplier());
		this.getInputProperties().add(this.getResult());
		// 结果
		this.getAffectedProperties().add(this.getResult());
		this.getAffectedProperties().add(this.getMultiplier());
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
		Decimal result = (Decimal) context.getInputValues().get(this.getResult());
		if (result == null) {
			result = Decimal.ZERO;
		}
		if (multiplicand.isZero()) {
			context.getOutputValues().put(this.getResult(), Decimal.ZERO);
			return;
		}
		if (!multiplier.isZero() && result.isZero()) {
			// 结果 = 乘数 * 被乘数
			result = multiplicand.multiply(multiplier);
			context.getOutputValues().put(this.getResult(),
					Decimal.round(result, Decimal.RESERVED_DECIMAL_PLACES_RUNNING));
		} else if (multiplier.isZero() && !result.isZero()) {
			// 乘数 = 结果 / 被乘数
			multiplier = result.divide(multiplicand);
			context.getOutputValues().put(this.getMultiplier(),
					Decimal.round(multiplier, Decimal.RESERVED_DECIMAL_PLACES_RUNNING));
		}
	}

}
