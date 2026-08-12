package org.colorcoding.ibas.bobas.expression;

import java.util.Objects;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 布尔值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionBoolean extends JudgmentExpression<Boolean> {

	public JudgmentExpressionBoolean() {

	}

	public JudgmentExpressionBoolean(Boolean leftValue, JudgmentOperation operation, Boolean rightValue) {
		super(leftValue, operation, rightValue);
	}

	@Override
	public boolean result()  {
		// 等
		if (this.getOperation() == JudgmentOperation.EQUAL) {
			return Objects.equals(this.getLeftValue(), this.getRightValue());
		}
		// 不等
		else if (this.getOperation() == JudgmentOperation.NOT_EQUAL) {
			return !Objects.equals(this.getLeftValue(), this.getRightValue());
		}
		// 且
		else if (this.getOperation() == JudgmentOperation.AND) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			return this.getLeftValue() && this.getRightValue();
		}
		// 或
		else if (this.getOperation() == JudgmentOperation.OR) {
			if (this.getLeftValue() == null && this.getRightValue() == null) {
				return false;
			}
			return Boolean.TRUE.equals(this.getLeftValue()) || Boolean.TRUE.equals(this.getRightValue());
		}
		// 不支持的
		return super.result();
	}

	private Boolean leftValue;

	@Override
	public final Boolean getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof Boolean) {
			this.setLeftValue((Boolean) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		} else if (value == null) {
			this.setLeftValue((Boolean) null);
		}
	}

	public final void setLeftValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setLeftValue((Boolean) null);
			return;
		}
		this.setLeftValue(Boolean.valueOf(value));
	}

	public final void setLeftValue(Boolean value) {
		this.leftValue = value;
	}

	private Boolean rightValue;

	@Override
	public final Boolean getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof Boolean) {
			this.setRightValue((Boolean) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		} else if (value == null) {
			this.setRightValue((Boolean) null);
		}
	}

	public final void setRightValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			this.setRightValue((Boolean) null);
			return;
		}
		this.setRightValue(Boolean.valueOf(value));
	}

	public final void setRightValue(Boolean value) {
		this.rightValue = value;
	}
}
