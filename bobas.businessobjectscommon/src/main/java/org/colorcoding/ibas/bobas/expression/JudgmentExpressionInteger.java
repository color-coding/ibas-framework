package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 整数值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionInteger extends JudgmentExpressionComparable<Integer> {
	public JudgmentExpressionInteger() {

	}

	public JudgmentExpressionInteger(int leftValue, JudgmentOperation operation, int rightValue) {
		super(leftValue, operation, rightValue);
	}

	private int leftValue;

	@Override
	public final Integer getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof Integer) {
			this.setLeftValue((Integer) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		} else if (value != null) {
			this.setLeftValue(Strings.valueOf(value));
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(Integer.valueOf(value));
	}

	public final void setLeftValue(Integer value) {
		this.leftValue = value;
	}

	private int rightValue;

	@Override
	public final Integer getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof Integer) {
			this.setRightValue((Integer) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		} else if (value != null) {
			this.setRightValue(Strings.valueOf(value));
		}
	}

	public final void setRightValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			if (this.getOperation() == JudgmentOperation.IS_NULL || this.getOperation() == JudgmentOperation.NOT_NULL) {
				value = Strings.VALUE_ZERO;
			}
		}
		this.setRightValue(Integer.valueOf(value));
	}

	public final void setRightValue(Integer value) {
		this.rightValue = value;
	}
}
