package org.colorcoding.ibas.bobas.expressions;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.data.Decimal;

/**
 * 十进制数值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionDecimal extends JudgmentExpressionComparable<BigDecimal> {

	public JudgmentExpressionDecimal() {

	}

	public JudgmentExpressionDecimal(Decimal leftValue, JudmentOperations operation, Decimal rightValue) {
		super(leftValue, operation, rightValue);
	}

	private Decimal leftValue;

	@Override
	public final Decimal getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof Decimal) {
			this.setLeftValue((Decimal) value);
		} else if (value instanceof BigDecimal) {
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
		this.setLeftValue(Decimal.valueOf(value));
	}

	public final void setLeftValue(double value) {
		this.setLeftValue(Decimal.valueOf(value));
	}

	public final void setLeftValue(int value) {
		this.setLeftValue(Decimal.valueOf(value));
	}

	public final void setLeftValue(long value) {
		this.setLeftValue(Decimal.valueOf(value));
	}

	public final void setLeftValue(BigDecimal value) {
		this.setLeftValue(new Decimal(value));
	}

	public final void setLeftValue(Decimal value) {
		this.leftValue = value;
	}

	private Decimal rightValue;

	@Override
	public final Decimal getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof Decimal) {
			this.setRightValue((Decimal) value);
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
		this.setRightValue(Decimal.valueOf(value));
	}

	public final void setRightValue(double value) {
		this.setRightValue(Decimal.valueOf(value));
	}

	public final void setRightValue(int value) {
		this.setRightValue(Decimal.valueOf(value));
	}

	public final void setRightValue(long value) {
		this.setRightValue(Decimal.valueOf(value));
	}

	public final void setRightValue(BigDecimal value) {
		this.setRightValue(new Decimal(value));
	}

	public final void setRightValue(Decimal value) {
		this.rightValue = value;
	}
}
