package org.colorcoding.ibas.bobas.expressions;

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

	public JudgmentExpressionDateTime(DateTime leftValue, JudmentOperations operation, DateTime rightValue) {
		super(leftValue, operation, rightValue);
	}

	@Override
	public boolean result() throws NotSupportOperationException {

		// 等于
		if (this.getOperation() == JudmentOperations.EQUAL) {
			// 左值为空
			if (this.getLeftValue() == null) {
				if (this.getRightValue() == null) {
					return true;
				}
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().equals(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 不等于
		else if (this.getOperation() == JudmentOperations.NOT_EQUAL) {
			// 左值为空
			if (this.getLeftValue() == null) {
				if (this.getRightValue() != null) {
					return true;
				}
				return false;
			}
			// 比较左右值
			if (!this.getLeftValue().equals(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 大于
		else if (this.getOperation() == JudmentOperations.GRATER_THAN) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().after(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 小于
		else if (this.getOperation() == JudmentOperations.LESS_THAN) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().before(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 大于等于
		else if (this.getOperation() == JudmentOperations.GRATER_EQUAL) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().after(this.getRightValue()) || this.getLeftValue().equals(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 小于等于
		else if (this.getOperation() == JudmentOperations.LESS_EQUAL) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().before(this.getRightValue()) || this.getLeftValue().equals(this.getRightValue())) {
				return true;
			}
			return false;
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
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(DateTime.valueOf(value));
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
		}
	}

	public final void setRightValue(String value) {
		this.setRightValue(DateTime.valueOf(value));
	}

	public final void setRightValue(DateTime value) {
		this.rightValue = value;
	}
}
