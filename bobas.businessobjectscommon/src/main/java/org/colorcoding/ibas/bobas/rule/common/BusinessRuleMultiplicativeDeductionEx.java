package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;

/**
 * 业务规则-乘除法推导（根据被乘数，用乘除法推导乘数或结果）
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleMultiplicativeDeductionEx extends BusinessRuleMultiplicativeDeduction {

	public static final BigDecimal PRECISION_VALUE = new BigDecimal("0.00001");

	protected BusinessRuleMultiplicativeDeductionEx() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param multiplicand 属性-被乘数
	 * @param multiplier   属性-乘数
	 * @param result       属性-结果
	 */
	public BusinessRuleMultiplicativeDeductionEx(IPropertyInfo<BigDecimal> multiplicand,
			IPropertyInfo<BigDecimal> multiplier, IPropertyInfo<BigDecimal> result) {
		this(multiplicand, multiplier, result, Decimal.DECIMAL_PLACES_RUNNING);
	}

	/**
	 * 构造方法
	 * 
	 * @param multiplicand 属性-被乘数
	 * @param multiplier   属性-乘数
	 * @param result       属性-结果
	 * @param scale        小数位
	 */
	public BusinessRuleMultiplicativeDeductionEx(IPropertyInfo<BigDecimal> multiplicand,
			IPropertyInfo<BigDecimal> multiplier, IPropertyInfo<BigDecimal> result, int scale) {
		super(multiplicand, multiplier, result);
		this.getAffectedProperties().add(this.getMultiplicand());
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		if (context.getTrigger() == null || context.getTrigger().isEmpty()) {
			super.execute(context);
		} else {
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
			if (context.getTrigger().equalsIgnoreCase(this.getResult().getName())) {
				// 结果触发
				if (!Decimal.isZero(multiplicand) && Decimal.isZero(multiplier)) {
					// 乘数 = 结果 / 被乘数
					multiplier = Decimal.divide(result, multiplicand);
					context.getOutputValues().put(this.getMultiplier(), Decimal.round(multiplier, this.getScale()));
				} else if (!Decimal.isZero(multiplier)) {
					// 被乘数 = 结果 / 乘数
					BigDecimal newMultiplicand = Decimal.divide(result, multiplier);
					if (Decimal.subtract(newMultiplicand, multiplicand).abs().compareTo(PRECISION_VALUE) > 0) {
						context.getOutputValues().put(this.getMultiplicand(),
								Decimal.round(newMultiplicand, this.getScale()));
					}
				}
			} else if (context.getTrigger().equalsIgnoreCase(this.getMultiplicand().getName())
					|| context.getTrigger().equalsIgnoreCase(this.getMultiplier().getName())) {
				// 结果 = 乘数 * 被乘数
				BigDecimal newResult = Decimal.multiply(multiplicand, multiplier);
				if (Decimal.subtract(newResult, result).abs().compareTo(PRECISION_VALUE) > 0) {
					context.getOutputValues().put(this.getResult(), Decimal.round(newResult, this.getScale()));
				}
			} else {
				super.execute(context);
			}
		}
	}
}
