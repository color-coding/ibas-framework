package org.colorcoding.ibas.bobas.expressions;

/**
 * 浮点值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionFloat extends JudgmentExpressionComparable<Float> {
	public JudgmentExpressionFloat() {

	}

	public JudgmentExpressionFloat(float leftValue, JudmentOperations operation, float rightValue) {
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
		}
	}

	public final void setRightValue(String value) {
		this.setRightValue(Float.valueOf(value));
	}

	public final void setRightValue(Float value) {
		this.rightValue = value;
	}
}
