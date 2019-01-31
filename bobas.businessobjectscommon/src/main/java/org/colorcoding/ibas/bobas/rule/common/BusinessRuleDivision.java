package org.colorcoding.ibas.bobas.rule.common;

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
	public BusinessRuleDivision(IPropertyInfo<Decimal> result, IPropertyInfo<Decimal> dividend,
			IPropertyInfo<Decimal> divisor) {
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
	}

	private IPropertyInfo<Decimal> dividend;

	public final IPropertyInfo<Decimal> getDividend() {
		return dividend;
	}

	public final void setDividend(IPropertyInfo<Decimal> dividend) {
		this.dividend = dividend;
	}

	private IPropertyInfo<Decimal> divisor;

	public final IPropertyInfo<Decimal> getDivisor() {
		return divisor;
	}

	public final void setDivisor(IPropertyInfo<Decimal> divisor) {
		this.divisor = divisor;
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
		Decimal dividend = (Decimal) context.getInputValues().get(this.getDividend());
		if (dividend == null) {
			dividend = Decimal.ZERO;
		}
		Decimal divisor = (Decimal) context.getInputValues().get(this.getDivisor());
		if (divisor == null || divisor.isZero()) {
			// 除数为0，直接退出
			return;
		}
		Decimal result = dividend.divide(divisor);
		// 截取精度
		result = Decimal.round(result, Decimal.RESERVED_DECIMAL_PLACES_RUNNING);
		Decimal oResult = (Decimal) context.getInputValues().get(this.getResult());
		if (oResult != null) {
			Decimal tOld = Decimal.round(oResult, Decimal.RESERVED_DECIMAL_PLACES_STORAGE);
			Decimal tNew = Decimal.round(result, Decimal.RESERVED_DECIMAL_PLACES_STORAGE);
			if (tOld.compareTo(tNew) == 0) {
				// 存储精度内保持一致，则退出
				return;
			}
		}
		context.getOutputValues().put(this.getResult(), result);
	}

}
