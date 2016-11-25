package org.colorcoding.ibas.bobas.expressions;

/**
 * 短数值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionShort extends JudgmentExpressionComparable<Short> {
	public JudgmentExpressionShort() {

	}

	public JudgmentExpressionShort(short leftValue, JudmentOperations operation, short rightValue) {
		super(leftValue, operation, rightValue);
	}

	private short leftValue;

	@Override
	public final Short getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof Short) {
			this.setLeftValue((Short) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(Short.valueOf(value));
	}

	public final void setLeftValue(Short value) {
		this.leftValue = value;
	}

	private short rightValue;

	@Override
	public final Short getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof Short) {
			this.setRightValue((Short) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		}
	}

	public final void setRightValue(String value) {
		this.setRightValue(Short.valueOf(value));
	}

	public final void setRightValue(Short value) {
		this.rightValue = value;
	}
}
