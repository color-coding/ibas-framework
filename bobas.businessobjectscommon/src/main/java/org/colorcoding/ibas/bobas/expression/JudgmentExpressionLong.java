package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 整数值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionLong extends JudgmentExpressionComparable<Long> {
	public JudgmentExpressionLong() {

	}

	public JudgmentExpressionLong(long leftValue, JudgmentOperation operation, long rightValue) {
		super(leftValue, operation, rightValue);
	}

	private Long leftValue;

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
		} else if (value != null) {
			this.setLeftValue(Strings.valueOf(value));
		} else {
			this.setLeftValue((Long) null);
		}
	}

	public final void setLeftValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setLeftValue((Long) null);
			return;
		}
		this.setLeftValue(Long.valueOf(value));
	}

	public final void setLeftValue(Long value) {
		this.leftValue = value;
	}

	private Long rightValue;

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
		} else if (value != null) {
			this.setRightValue(Strings.valueOf(value));
		} else {
			this.setRightValue((Long) null);
		}
	}

	public final void setRightValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setRightValue((Long) null);
			return;
		}
		this.setRightValue(Long.valueOf(value));
	}

	public final void setRightValue(Long value) {
		this.rightValue = value;
	}
}
