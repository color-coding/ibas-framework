package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 日期值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionDateTime extends JudgmentExpression<DateTime> {
	public JudgmentExpressionDateTime() {

	}

	public JudgmentExpressionDateTime(DateTime leftValue, JudgmentOperation operation, DateTime rightValue) {
		super(leftValue, operation, rightValue);
	}

	@Override
	public boolean result()  {
		// 等于
		if (this.getOperation() == JudgmentOperation.EQUAL) {
			if (this.getLeftValue() == null && this.getRightValue() == null) {
				return true;
			}
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			return this.getLeftValue().compareTo(this.getRightValue()) == 0;
		}
		// 不等于
		else if (this.getOperation() == JudgmentOperation.NOT_EQUAL) {
			if (this.getLeftValue() == null && this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return true;
			}
			return this.getLeftValue().compareTo(this.getRightValue()) != 0;
		}
		// 大于
		else if (this.getOperation() == JudgmentOperation.GREATER_THAN) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			return this.getLeftValue().compareTo(this.getRightValue()) > 0;
		}
		// 小于
		else if (this.getOperation() == JudgmentOperation.LESS_THAN) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			return this.getLeftValue().compareTo(this.getRightValue()) < 0;
		}
		// 大于等于
		else if (this.getOperation() == JudgmentOperation.GREATER_EQUAL) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			return this.getLeftValue().compareTo(this.getRightValue()) >= 0;
		}
		// 小于等于
		else if (this.getOperation() == JudgmentOperation.LESS_EQUAL) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			return this.getLeftValue().compareTo(this.getRightValue()) <= 0;
		}
		return super.result();
	}

	private DateTime leftValue;

	@Override
	public final DateTime getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		if (value instanceof DateTime) {
			this.setLeftValue((DateTime) value);
		} else if (value instanceof String) {
			this.setLeftValue((String) value);
		} else if (value != null) {
			this.setLeftValue(Strings.valueOf(value));
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(DateTimes.valueOf(value));
	}

	public final void setLeftValue(DateTime value) {
		this.leftValue = value;
	}

	private DateTime rightValue;

	@Override
	public final DateTime getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		if (value instanceof DateTime) {
			this.setRightValue((DateTime) value);
		} else if (value instanceof String) {
			this.setRightValue((String) value);
		} else if (value != null) {
			this.setRightValue(Strings.valueOf(value));
		}
	}

	public final void setRightValue(String value) {
		if (Strings.isNullOrEmpty(value)) {
			if (this.getOperation() == JudgmentOperation.IS_NULL || this.getOperation() == JudgmentOperation.NOT_NULL) {
				value = DateTimes.toString(DateTimes.VALUE_MIN, DateTimes.FORMAT_DATE);
			}
		}
		this.setRightValue(DateTimes.valueOf(value));
	}

	public final void setRightValue(DateTime value) {
		this.rightValue = value;
	}
}
