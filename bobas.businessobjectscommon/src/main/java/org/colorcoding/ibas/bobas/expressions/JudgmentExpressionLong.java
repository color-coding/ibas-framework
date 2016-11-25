package org.colorcoding.ibas.bobas.expressions;

/**
 * 整数值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionLong extends JudgmentExpressionComparable<Long> {
	public JudgmentExpressionLong() {

	}

	public JudgmentExpressionLong(long leftValue, JudmentOperations operation, long rightValue) {
		super(leftValue, operation, rightValue);
	}

	private long leftValue;

	@Override
	public final Long getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof Long) {
			this.setLeftValue((Long) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(Long.valueOf(value));
	}

	public final void setLeftValue(Long value) {
		this.leftValue = value;
	}

	private long rightValue;

	@Override
	public final Long getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof Long) {
			this.setRightValue((Long) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		}
	}

	public final void setRightValue(String value) {
		this.setRightValue(Long.valueOf(value));
	}

	public final void setRightValue(Long value) {
		this.rightValue = value;
	}
}
