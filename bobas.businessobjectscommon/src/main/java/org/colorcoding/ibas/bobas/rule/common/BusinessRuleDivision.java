package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-除法运算
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleDivision extends BusinessRuleCommon {

	protected BusinessRuleDivision() {
		this.setName(I18N.prop("msg_bobas_business_rule_division"));
	}

	/**
	 * 构造方法
	 * 
	 * @param result   属性-结果
	 * @param dividend 属性-被除数
	 * @param divisor  属性-除数
	 */
	public BusinessRuleDivision(IPropertyInfo<BigDecimal> result, IPropertyInfo<BigDecimal> dividend,
			IPropertyInfo<BigDecimal> divisor) {
		this(result, dividend, divisor, Decimal.DECIMAL_PLACES_RUNNING);
	}

	/**
	 * 构造方法
	 * 
	 * @param result   属性-结果
	 * @param dividend 属性-被除数
	 * @param divisor  属性-除数
	 * @param scale    小数位
	 */
	public BusinessRuleDivision(IPropertyInfo<BigDecimal> result, IPropertyInfo<BigDecimal> dividend,
			IPropertyInfo<BigDecimal> divisor, int scale) {
		this();
		this.setDividend(dividend);
		this.setDivisor(divisor);
		this.setResult(result);
		// 要输入的参数
		this.getInputProperties().add(this.getDividend());
		this.getInputProperties().add(this.getDivisor());
		this.getInputProperties().add(this.getResult());
		// 结果
		this.getAffectedProperties().add(this.getResult());
		// 小数位
		this.setScale(scale);
	}

	private IPropertyInfo<BigDecimal> dividend;

	public final IPropertyInfo<BigDecimal> getDividend() {
		return dividend;
	}

	public final void setDividend(IPropertyInfo<BigDecimal> dividend) {
		this.dividend = dividend;
	}

	private IPropertyInfo<BigDecimal> divisor;

	public final IPropertyInfo<BigDecimal> getDivisor() {
		return divisor;
	}

	public final void setDivisor(IPropertyInfo<BigDecimal> divisor) {
		this.divisor = divisor;
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
		BigDecimal dividend = (BigDecimal) context.getInputValues().get(this.getDividend());
		if (dividend == null) {
			dividend = Decimal.ZERO;
		}
		BigDecimal divisor = (BigDecimal) context.getInputValues().get(this.getDivisor());
		if (divisor == null || Decimal.isZero(divisor)) {
			// 除数为0，直接退出
			return;
		}
		BigDecimal result = Decimal.divide(dividend, divisor);
		// 截取精度
		result = Decimal.round(result, this.getScale());
		BigDecimal oResult = (BigDecimal) context.getInputValues().get(this.getResult());
		if (oResult != null) {
			BigDecimal tOld = Decimal.round(oResult, this.getScale());
			BigDecimal tNew = Decimal.round(result, this.getScale());
			if (tOld.compareTo(tNew) == 0) {
				// 存储精度内保持一致，则退出
				return;
			}
		}
		context.getOutputValues().put(this.getResult(), result);
	}

}
