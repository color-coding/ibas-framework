package org.colorcoding.ibas.bobas.rule.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;

/**
 * 业务规则-乘除法推导（根据被乘数，用乘除法推导乘数或结果）
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleMultiplicativeDeductionEx extends BusinessRuleMultiplicativeDeduction {

	public static final Decimal PRECISION_VALUE = new Decimal("0.00001");

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
	public BusinessRuleMultiplicativeDeductionEx(IPropertyInfo<Decimal> multiplicand, IPropertyInfo<Decimal> multiplier,
			IPropertyInfo<Decimal> result) {
		super(multiplicand, multiplier, result);
		this.getAffectedProperties().add(this.getMultiplicand());
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		if (context.getTrigger() == null || context.getTrigger().isEmpty()) {
			super.execute(context);
		} else {
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
			if (context.getTrigger().equalsIgnoreCase(this.getResult().getName())) {
				// 结果触发
				if (!multiplicand.isZero() && multiplier.isZero()) {
					// 乘数 = 结果 / 被乘数
					multiplier = result.divide(multiplicand);
					context.getOutputValues().put(this.getMultiplier(),
							Decimal.round(multiplier, Decimal.RESERVED_DECIMAL_PLACES_RUNNING));
				} else if (!multiplier.isZero()) {
					// 被乘数 = 结果 / 乘数
					Decimal newMultiplicand = result.divide(multiplier);
					if (newMultiplicand.subtract(multiplicand).abs().compareTo(PRECISION_VALUE) > 0) {
						context.getOutputValues().put(this.getMultiplicand(),
								Decimal.round(newMultiplicand, Decimal.RESERVED_DECIMAL_PLACES_RUNNING));
					}
				}
			} else if (context.getTrigger().equalsIgnoreCase(this.getMultiplicand().getName())
					|| context.getTrigger().equalsIgnoreCase(this.getMultiplier().getName())) {
				// 结果 = 乘数 * 被乘数
				Decimal newResult = multiplicand.multiply(multiplier);
				if (newResult.subtract(result).abs().compareTo(PRECISION_VALUE) > 0) {
					context.getOutputValues().put(this.getResult(),
							Decimal.round(newResult, Decimal.RESERVED_DECIMAL_PLACES_RUNNING));
				}
			} else {
				super.execute(context);
			}
		}
	}
}
