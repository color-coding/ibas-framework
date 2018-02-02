package org.colorcoding.ibas.bobas.rule.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-加法推导（根据被加数，用加减法推导加数或结果）
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleAdditiveDeduction extends BusinessRuleCommon {

	public BusinessRuleAdditiveDeduction() {
		this.setName(I18N.prop("msg_bobas_business_rule_additive_deduction"));
	}

	/**
	 * 构造方法
	 * 
	 * @param augend
	 *            属性-被加数
	 * @param addend
	 *            属性-加数
	 * @param result
	 *            属性-结果
	 */
	public BusinessRuleAdditiveDeduction(IPropertyInfo<Decimal> augend, IPropertyInfo<Decimal> addend,
			IPropertyInfo<Decimal> result) {
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

	private IPropertyInfo<Decimal> augend;

	public final IPropertyInfo<Decimal> getAugend() {
		return augend;
	}

	public final void setAugend(IPropertyInfo<Decimal> augend) {
		this.augend = augend;
	}

	private IPropertyInfo<Decimal> addend;

	public final IPropertyInfo<Decimal> getAddend() {
		return addend;
	}

	public final void setAddend(IPropertyInfo<Decimal> addend) {
		this.addend = addend;
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
		Decimal augend = (Decimal) context.getInputValues().get(this.getAugend());
		if (augend == null) {
			augend = Decimal.ZERO;
		}
		Decimal addend = (Decimal) context.getInputValues().get(this.getAddend());
		if (addend == null) {
			addend = Decimal.ZERO;
		}
		Decimal result = (Decimal) context.getInputValues().get(this.getResult());
		if (result == null) {
			result = Decimal.ZERO;
		}
		if (augend.isZero()) {
			context.getOutputValues().put(this.getResult(), addend);
			return;
		}
		if (!addend.isZero() && result.isZero()) {
			// 结果 = 加数 + 被加数
			result = addend.add(augend);
			context.getOutputValues().put(this.getResult(), result);
		} else if (addend.isZero() && !result.isZero()) {
			// 加数 = 结果 - 被加数
			addend = result.subtract(augend);
			context.getOutputValues().put(this.getAddend(), addend);
		}
	}

}
