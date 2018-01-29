package org.colorcoding.ibas.bobas.rule.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRule;
import org.colorcoding.ibas.bobas.rule.BusinessRuleContext;

/**
 * 业务规则-求和运算
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleSubtraction extends BusinessRule {
	/**
	 * 构造方法
	 * 
	 * @param result
	 *            属性-结果
	 * @param subtrahend
	 *            属性-被减数
	 * @param subtractors
	 *            属性-减数（数组）
	 */
	@SafeVarargs
	public BusinessRuleSubtraction(IPropertyInfo<Decimal> result, IPropertyInfo<Decimal> subtrahend,
			IPropertyInfo<Decimal>... subtractors) {
		this.setResult(result);
		this.setSubtrahend(subtrahend);
		if (subtractors.length == 0) {
			throw new RuntimeException(I18N.prop("msg_bobas_business_rule_lack_subtraction"));
		}
		// 要输入的参数
		this.getInputProperties().add(this.getSubtrahend());
		this.getInputProperties().addAll(this.getSubtractors());
		// 结果
		this.getAffectedProperties().add(this.getResult());
	}

	private IPropertyInfo<Decimal> subtrahend;

	public final IPropertyInfo<Decimal> getSubtrahend() {
		return subtrahend;
	}

	public final void setSubtrahend(IPropertyInfo<Decimal> subtrahend) {
		this.subtrahend = subtrahend;
	}

	private List<IPropertyInfo<Decimal>> subtractors;

	public final List<IPropertyInfo<Decimal>> getSubtractors() {
		if (this.subtractors == null) {
			this.subtractors = new ArrayList<>();
		}
		return subtractors;
	}

	public final void setSubtractors(IPropertyInfo<Decimal>[] subtractors) {
		for (IPropertyInfo<Decimal> item : subtractors) {
			this.getSubtractors().add(item);
		}
	}

	public final void setSubtractors(Iterable<IPropertyInfo<Decimal>> subtractors) {
		for (IPropertyInfo<Decimal> item : subtractors) {
			this.getSubtractors().add(item);
		}
	}

	private IPropertyInfo<Decimal> result;

	public final IPropertyInfo<Decimal> getResult() {
		return result;
	}

	public final void setResult(IPropertyInfo<Decimal> result) {
		this.result = result;
	}

	@Override
	protected String getName() {
		return I18N.prop("msg_bobas_business_rule_subtraction");
	}

	@Override
	protected void execute(BusinessRuleContext context) throws Exception {
		Decimal result = (Decimal) context.getInputPropertyValues().get(this.getSubtrahend());
		if (result == null) {
			result = Decimal.ZERO;
		}
		for (IPropertyInfo<Decimal> item : this.getSubtractors()) {
			Decimal subtractor = (Decimal) context.getInputPropertyValues().get(item);
			if (subtractor == null) {
				continue;
			}
			result = result.subtract(subtractor);
		}
		context.getOutputPropertyValues().put(this.getResult(), result);
	}

}
