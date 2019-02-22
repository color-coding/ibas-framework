package org.colorcoding.ibas.bobas.rule.common;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.rule.BusinessRuleCommon;

/**
 * 业务规则-求差运算
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRuleSubtraction extends BusinessRuleCommon {

	protected BusinessRuleSubtraction() {
		this.setName(I18N.prop("msg_bobas_business_rule_subtraction"));
	}

	/**
	 * 构造方法
	 * 
	 * @param result      属性-结果
	 * @param subtrahend  属性-被减数
	 * @param subtractors 属性-减数（数组）
	 */
	@SafeVarargs
	public BusinessRuleSubtraction(IPropertyInfo<BigDecimal> result, IPropertyInfo<BigDecimal> subtrahend,
			IPropertyInfo<BigDecimal>... subtractors) {
		this();
		this.setResult(result);
		this.setSubtrahend(subtrahend);
		this.setSubtractors(subtractors);
		// 要输入的参数
		this.getInputProperties().add(this.getSubtrahend());
		this.getInputProperties().addAll(this.getSubtractors());
		// 结果
		this.getAffectedProperties().add(this.getResult());
	}

	private IPropertyInfo<BigDecimal> subtrahend;

	public final IPropertyInfo<BigDecimal> getSubtrahend() {
		return subtrahend;
	}

	public final void setSubtrahend(IPropertyInfo<BigDecimal> subtrahend) {
		this.subtrahend = subtrahend;
	}

	private List<IPropertyInfo<BigDecimal>> subtractors;

	public final List<IPropertyInfo<BigDecimal>> getSubtractors() {
		if (this.subtractors == null) {
			this.subtractors = new ArrayList<>();
		}
		return subtractors;
	}

	public final void setSubtractors(IPropertyInfo<BigDecimal>[] subtractors) {
		for (IPropertyInfo<BigDecimal> item : subtractors) {
			this.getSubtractors().add(item);
		}
	}

	public final void setSubtractors(Iterable<IPropertyInfo<BigDecimal>> subtractors) {
		for (IPropertyInfo<BigDecimal> item : subtractors) {
			this.getSubtractors().add(item);
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
		BigDecimal result = (BigDecimal) context.getInputValues().get(this.getSubtrahend());
		if (result == null) {
			result = Decimal.ZERO;
		}
		for (IPropertyInfo<BigDecimal> item : this.getSubtractors()) {
			BigDecimal subtractor = (BigDecimal) context.getInputValues().get(item);
			if (subtractor == null) {
				continue;
			}
			result = Decimal.subtract(result, subtractor);
		}
		context.getOutputValues().put(this.getResult(), result);
	}

}
