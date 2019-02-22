package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

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

	protected BusinessRuleSummation() {
		this.setName(I18N.prop("msg_bobas_business_rule_summation"));
	}

	/**
	 * 构造方法
	 * 
	 * @param result  属性-结果
	 * @param addends 属性-加数（数组）
	 */
	@SafeVarargs
	public BusinessRuleSummation(IPropertyInfo<BigDecimal> result, IPropertyInfo<BigDecimal>... addends) {
		this();
		this.setResult(result);
		this.setAddends(addends);
		// 要输入的参数
		this.getInputProperties().addAll(this.getAddends());
		// 结果
		this.getAffectedProperties().add(this.getResult());
	}

	private List<IPropertyInfo<BigDecimal>> addends;

	public final List<IPropertyInfo<BigDecimal>> getAddends() {
		if (this.addends == null) {
			this.addends = new ArrayList<>();
		}
		return addends;
	}

	public final void setAddends(IPropertyInfo<BigDecimal>[] addends) {
		for (IPropertyInfo<BigDecimal> item : addends) {
			this.getAddends().add(item);
		}
	}

	public final void setAddends(Iterable<IPropertyInfo<BigDecimal>> addends) {
		for (IPropertyInfo<BigDecimal> item : addends) {
			this.getAddends().add(item);
		}
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
		BigDecimal result = Decimal.ZERO;
		for (IPropertyInfo<BigDecimal> item : this.getAddends()) {
			Object additive = context.getInputValues().get(item);
			if (additive instanceof BigDecimal) {
				result = Decimal.add(result, (BigDecimal) additive);
			}
		}
		context.getOutputValues().put(this.getResult(), result);
	}

}
