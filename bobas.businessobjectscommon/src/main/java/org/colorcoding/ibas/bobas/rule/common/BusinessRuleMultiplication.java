package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-乘法运算
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleMultiplication extends BusinessRuleCommon {

	protected BusinessRuleMultiplication() {
		this.setName(I18N.prop("msg_bobas_business_rule_multiplication"));
	}

	/**
	 * 构造方法
	 * 
	 * @param result       属性-结果
	 * @param multiplicand 属性-被乘数
	 * @param multiplier   属性-乘数
	 */
	public BusinessRuleMultiplication(IPropertyInfo<BigDecimal> result, IPropertyInfo<BigDecimal> multiplicand,
			IPropertyInfo<BigDecimal> multiplier) {
		this(result, multiplicand, multiplier, Decimals.DECIMAL_PLACES_RUNNING);
	}

	/**
	 * 构造方法
	 * 
	 * @param result       属性-结果
	 * @param multiplicand 属性-被乘数
	 * @param multiplier   属性-乘数
	 * @param scale        小数位
	 */
	public BusinessRuleMultiplication(IPropertyInfo<BigDecimal> result, IPropertyInfo<BigDecimal> multiplicand,
			IPropertyInfo<BigDecimal> multiplier, int scale) {
		this();
		this.setMultiplicand(multiplicand);
		this.setMultiplier(multiplier);
		this.setResult(result);
		// 要输入的参数
		this.getInputProperties().add(this.getMultiplicand());
		this.getInputProperties().add(this.getMultiplier());
		// 结果
		this.getAffectedProperties().add(this.getResult());
		// 小数位
		this.setScale(scale);
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

	private int scale;

	public final int getScale() {
		return scale;
	}

	public final void setScale(int scale) {
		this.scale = scale;
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		BigDecimal multiplicand = (BigDecimal) context.getInputValues().get(this.getMultiplicand());
		if (multiplicand == null) {
			multiplicand = Decimals.VALUE_ZERO;
		}
		BigDecimal multiplier = (BigDecimal) context.getInputValues().get(this.getMultiplier());
		if (multiplier == null) {
			multiplier = Decimals.VALUE_ZERO;
		}
		BigDecimal result = Decimals.multiply(multiplicand, multiplier);
		// 截取精度
		result = Decimals.round(result, this.getScale());
		context.getOutputValues().put(this.getResult(), result);
	}

}
