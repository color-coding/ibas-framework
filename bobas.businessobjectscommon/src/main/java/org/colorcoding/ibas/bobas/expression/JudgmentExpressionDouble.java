package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 双精度值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionDouble extends JudgmentExpressionComparable<Double> {
	public JudgmentExpressionDouble() {

	}

	public JudgmentExpressionDouble(double leftValue, JudgmentOperation operation, double rightValue) {
		super(leftValue, operation, rightValue);
	}

	private Double leftValue;

	@Override
	public final Double getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof Double) {
			this.setLeftValue((Double) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		} else if (value != null) {
			this.setLeftValue(Strings.valueOf(value));
		} else {
			this.setLeftValue((Double) null);
		}
	}

	public final void setLeftValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setLeftValue((Double) null);
			return;
		}
		this.setLeftValue(Double.valueOf(value));
	}

	public final void setLeftValue(Double value) {
		this.leftValue = value;
	}

	private Double rightValue;

	@Override
	public final Double getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof Double) {
			this.setRightValue((Double) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		} else if (value != null) {
			this.setRightValue(Strings.valueOf(value));
		} else {
			this.setRightValue((Double) null);
		}
	}

	public final void setRightValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setRightValue((Double) null);
			return;
		}
		this.setRightValue(Double.valueOf(value));
	}

	public final void setRightValue(Double value) {
		this.rightValue = value;
	}
}
