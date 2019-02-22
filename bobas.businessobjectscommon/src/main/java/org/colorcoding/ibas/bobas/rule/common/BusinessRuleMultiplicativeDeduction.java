package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-乘除法推导（根据被乘数，用乘除法推导乘数或结果）
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleMultiplicativeDeduction extends BusinessRuleCommon {

	protected BusinessRuleMultiplicativeDeduction() {
		this.setName(I18N.prop("msg_bobas_business_rule_multiplicative_deduction"));
	}

	/**
	 * 构造方法
	 * 
	 * @param multiplicand 属性-被乘数
	 * @param multiplier   属性-乘数
	 * @param result       属性-结果
	 */
	public BusinessRuleMultiplicativeDeduction(IPropertyInfo<BigDecimal> multiplicand,
			IPropertyInfo<BigDecimal> multiplier, IPropertyInfo<BigDecimal> result) {
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

	private IPropertyInfo<BigDecimal> multiplicand;

	public final IPropertyInfo<BigDecimal> getMultiplicand() {
		return multiplicand;
	}

	public final void setMultiplicand(IPropertyInfo<BigDecimal> multiplicand) {
		this.multiplicand = multiplicand;
	}

	private IPropertyInfo<BigDecimal> multiplier;

	public final IPropertyInfo<BigDecimal> getMultiplier() {
		return multiplier;
	}

	public final void setMultiplier(IPropertyInfo<BigDecimal> multiplier) {
		this.multiplier = multiplier;
	}

	private IPropertyInfo<BigDecimal> result;

	public final IPropertyInfo<BigDecimal> getResult() {
		return result;
	}

	public final void setResult(IPropertyInfo<BigDecimal> result) {
		this.result = result;
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		BigDecimal multiplicand = (BigDecimal) context.getInputValues().get(this.getMultiplicand());
		if (multiplicand == null) {
			multiplicand = Decimal.ZERO;
		}
		BigDecimal multiplier = (BigDecimal) context.getInputValues().get(this.getMultiplier());
		if (multiplier == null) {
			multiplier = Decimal.ZERO;
		}
		BigDecimal result = (BigDecimal) context.getInputValues().get(this.getResult());
		if (result == null) {
			result = Decimal.ZERO;
		}
		if (Decimal.isZero(multiplicand)) {
			context.getOutputValues().put(this.getResult(), BigDecimal.ZERO);
			return;
		}
		if (!Decimal.isZero(multiplier) && Decimal.isZero(result)) {
			// 结果 = 乘数 * 被乘数
			result = Decimal.multiply(multiplicand, multiplier);
			context.getOutputValues().put(this.getResult(), Decimal.round(result, Decimal.DECIMAL_PLACES_RUNNING));
		} else if (!Decimal.isZero(multiplicand) && !Decimal.isZero(result)) {
			// 乘数 = 结果 / 被乘数
			multiplier = Decimal.divide(result, multiplicand);
			context.getOutputValues().put(this.getMultiplier(),
					Decimal.round(multiplier, Decimal.DECIMAL_PLACES_RUNNING));
		}
	}

}
