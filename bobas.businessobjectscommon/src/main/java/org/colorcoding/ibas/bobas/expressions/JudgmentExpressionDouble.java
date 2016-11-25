package org.colorcoding.ibas.bobas.expressions;

/**
 * 双精度值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionDouble extends JudgmentExpressionComparable<Double> {
	public JudgmentExpressionDouble() {

	}

	public JudgmentExpressionDouble(double leftValue, JudmentOperations operation, double rightValue) {
		super(leftValue, operation, rightValue);
	}

	private double leftValue;

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
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(Double.valueOf(value));
	}

	public final void setLeftValue(Double value) {
		this.leftValue = value;
	}

	private double rightValue;

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
		}
	}

	public final void setRightValue(String value) {
		this.setRightValue(Double.valueOf(value));
	}

	public final void setRightValue(Double value) {
		this.rightValue = value;
	}
}
