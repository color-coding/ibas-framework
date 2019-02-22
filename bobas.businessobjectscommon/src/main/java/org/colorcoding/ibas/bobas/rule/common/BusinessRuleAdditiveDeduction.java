package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-加减法推导（根据被加数，用加减法推导加数或结果）
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleAdditiveDeduction extends BusinessRuleCommon {

	protected BusinessRuleAdditiveDeduction() {
		this.setName(I18N.prop("msg_bobas_business_rule_additive_deduction"));
	}

	/**
	 * 构造方法
	 * 
	 * @param augend 属性-被加数
	 * @param addend 属性-加数
	 * @param result 属性-结果
	 */
	public BusinessRuleAdditiveDeduction(IPropertyInfo<BigDecimal> augend, IPropertyInfo<BigDecimal> addend,
			IPropertyInfo<BigDecimal> result) {
		this();
		this.setAugend(augend);
		this.setAddend(addend);
		this.setResult(result);
		// 要输入的参数
		this.getInputProperties().add(this.getAugend());
		this.getInputProperties().add(this.getAddend());
		this.getInputProperties().add(this.getResult());
		// 结果
		this.getAffectedProperties().add(this.getResult());
		this.getAffectedProperties().add(this.getAddend());
	}

	private IPropertyInfo<BigDecimal> augend;

	public final IPropertyInfo<BigDecimal> getAugend() {
		return augend;
	}

	public final void setAugend(IPropertyInfo<BigDecimal> augend) {
		this.augend = augend;
	}

	private IPropertyInfo<BigDecimal> addend;

	public final IPropertyInfo<BigDecimal> getAddend() {
		return addend;
	}

	public final void setAddend(IPropertyInfo<BigDecimal> addend) {
		this.addend = addend;
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
		BigDecimal augend = (BigDecimal) context.getInputValues().get(this.getAugend());
		if (augend == null) {
			augend = Decimal.ZERO;
		}
		BigDecimal addend = (BigDecimal) context.getInputValues().get(this.getAddend());
		if (addend == null) {
			addend = Decimal.ZERO;
		}
		BigDecimal result = (BigDecimal) context.getInputValues().get(this.getResult());
		if (result == null) {
			result = Decimal.ZERO;
		}
		if (Decimal.isZero(augend)) {
			context.getOutputValues().put(this.getResult(), addend);
			return;
		}
		if (!Decimal.isZero(addend) && Decimal.isZero(result)) {
			// 结果 = 加数 + 被加数
			result = Decimal.add(addend, augend);
			context.getOutputValues().put(this.getResult(), result);
		} else if (Decimal.isZero(addend) && !Decimal.isZero(result)) {
			// 加数 = 结果 - 被加数
			addend = Decimal.subtract(result, augend);
			context.getOutputValues().put(this.getAddend(), addend);
		}
	}

}
