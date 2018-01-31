package org.colorcoding.ibas.bobas.rule.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-求和运算
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleSummation extends BusinessRuleCommon {

	public BusinessRuleSummation() {
		this.setName(I18N.prop("msg_bobas_business_rule_summation"));
	}

	/**
	 * 构造方法
	 * 
	 * @param result
	 *            属性-结果
	 * @param addends
	 *            属性-加数（数组）
	 */
	@SafeVarargs
	public BusinessRuleSummation(IPropertyInfo<Decimal> result, IPropertyInfo<Decimal>... addends) {
		this();
		this.setResult(result);
		if (addends.length == 0) {
			throw new RuntimeException(I18N.prop("msg_bobas_business_rule_lack_summation"));
		}
		this.setAddends(addends);
		// 要输入的参数
		this.getInputProperties().addAll(this.getAddends());
		// 结果
		this.getAffectedProperties().add(this.getResult());
	}

	private List<IPropertyInfo<Decimal>> addends;

	public final List<IPropertyInfo<Decimal>> getAddends() {
		if (this.addends == null) {
			this.addends = new ArrayList<>();
		}
		return addends;
	}

	public final void setAddends(IPropertyInfo<Decimal>[] addends) {
		for (IPropertyInfo<Decimal> item : addends) {
			this.getAddends().add(item);
		}
	}

	public final void setAddends(Iterable<IPropertyInfo<Decimal>> addends) {
		for (IPropertyInfo<Decimal> item : addends) {
			this.getAddends().add(item);
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
	protected void execute(BusinessRuleContext context) throws Exception {
		Decimal result = Decimal.ZERO;
		for (IPropertyInfo<Decimal> item : this.getAddends()) {
			Decimal additive = (Decimal) context.getInputValues().get(item);
			if (additive == null) {
				continue;
			}
			result = result.add(additive);
		}
		context.getOutputValues().put(this.getResult(), result);
	}

}
