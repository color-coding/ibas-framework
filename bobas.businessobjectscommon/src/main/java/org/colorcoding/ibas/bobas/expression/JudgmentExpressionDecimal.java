package org.colorcoding.ibas.bobas.expression;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.common.Decimals;

/**
 * 十进制数值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionDecimal extends JudgmentExpressionComparable<BigDecimal> {

	public JudgmentExpressionDecimal() {
	}

	public JudgmentExpressionDecimal(BigDecimal leftValue, JudmentOperation operation, BigDecimal rightValue) {
		super(leftValue, operation, rightValue);
	}

	private BigDecimal leftValue;

	@Override
	public final BigDecimal getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof BigDecimal) {
			this.setLeftValue((BigDecimal) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		} else if (value instanceof Double) {
			this.setLeftValue((double) value);
		} else if (value instanceof Integer) {
			this.setLeftValue((int) value);
		} else if (value instanceof Long) {
			this.setLeftValue((long) value);
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(Decimals.valueOf(value));
	}

	public final void setLeftValue(double value) {
		this.setLeftValue(Decimals.valueOf(value));
	}

	public final void setLeftValue(int value) {
		this.setLeftValue(Decimals.valueOf(value));
	}

	public final void setLeftValue(long value) {
		this.setLeftValue(Decimals.valueOf(value));
	}

	public final void setLeftValue(BigDecimal value) {
		this.leftValue = value;
	}

	private BigDecimal rightValue;

	@Override
	public final BigDecimal getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof BigDecimal) {
			this.setRightValue((BigDecimal) value);
		} else if (value instanceof BigDecimal) {
			this.setRightValue((BigDecimal) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		} else if (value instanceof Double) {
			this.setRightValue((double) value);
		} else if (value instanceof Integer) {
			this.setRightValue((int) value);
		} else if (value instanceof Long) {
			this.setRightValue((long) value);
		}
	}

	public final void setRightValue(String value) {
		this.setRightValue(Decimals.valueOf(value));
	}

	public final void setRightValue(double value) {
		this.setRightValue(Decimals.valueOf(value));
	}

	public final void setRightValue(int value) {
		this.setRightValue(Decimals.valueOf(value));
	}

	public final void setRightValue(long value) {
		this.setRightValue(Decimals.valueOf(value));
	}

	public final void setRightValue(BigDecimal value) {
		this.rightValue = value;
	}
}
