package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 浮点值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionFloat extends JudgmentExpressionComparable<Float> {
	public JudgmentExpressionFloat() {

	}

	public JudgmentExpressionFloat(float leftValue, JudmentOperation operation, float rightValue) {
		super(leftValue, operation, rightValue);
	}

	private float leftValue;

	@Override
	public final Float getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof Float) {
			this.setLeftValue((Float) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		} else if (value != null) {
			this.setLeftValue(Strings.valueOf(value));
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(Float.valueOf(value));
	}

	public final void setLeftValue(Float value) {
		this.leftValue = value;
	}

	private float rightValue;

	@Override
	public final Float getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof Float) {
			this.setRightValue((Float) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		} else if (value != null) {
			this.setRightValue(Strings.valueOf(value));
		}
	}

	public final void setRightValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			if (this.getOperation() == JudmentOperation.IS_NULL || this.getOperation() == JudmentOperation.NOT_NULL) {
				value = Strings.VALUE_ZERO;
			}
		}
		this.setRightValue(Float.valueOf(value));
	}

	public final void setRightValue(Float value) {
		this.rightValue = value;
	}
}
