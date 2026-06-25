package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 短数值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionShort extends JudgmentExpressionComparable<Short> {
	public JudgmentExpressionShort() {

	}

	public JudgmentExpressionShort(short leftValue, JudgmentOperation operation, short rightValue) {
		super(leftValue, operation, rightValue);
	}

	private Short leftValue;

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
		} else if (value != null) {
			this.setLeftValue(Strings.valueOf(value));
		} else {
			this.setLeftValue((Short) null);
		}
	}

	public final void setLeftValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setLeftValue((Short) null);
			return;
		}
		this.setLeftValue(Short.valueOf(value));
	}

	public final void setLeftValue(Short value) {
		this.leftValue = value;
	}

	private Short rightValue;

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
		} else if (value != null) {
			this.setRightValue(Strings.valueOf(value));
		} else {
			this.setRightValue((Short) null);
		}
	}

	public final void setRightValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setRightValue((Short) null);
			return;
		}
		this.setRightValue(Short.valueOf(value));
	}

	public final void setRightValue(Short value) {
		this.rightValue = value;
	}
}
