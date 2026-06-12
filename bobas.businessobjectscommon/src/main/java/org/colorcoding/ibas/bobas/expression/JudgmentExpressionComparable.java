package org.colorcoding.ibas.bobas.expression;

/**
 * 可比较的类型表达式比较
 * 
 * @author Niuren.Zhu
 *
 * @param <T> 可比较类型子类
 */
public abstract class JudgmentExpressionComparable<T extends Comparable<T>> extends JudgmentExpression<T> {
	public JudgmentExpressionComparable() {

	}

	public JudgmentExpressionComparable(T leftValue, JudgmentOperation operation, T rightValue) {
		super(leftValue, operation, rightValue);
	}

	@Override
	public boolean result() throws ExpressionException {
		// 等于
		if (this.getOperation() == JudgmentOperation.EQUAL) {
			// 左值为空
			if (this.getLeftValue() == null) {
				if (this.getRightValue() == null) {
					return true;
				}
				return false;
			}
			if (this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().compareTo(this.getRightValue()) == 0) {
				return true;
			}
			return false;
		}
		// 不等于
		else if (this.getOperation() == JudgmentOperation.NOT_EQUAL) {
			// 左值为空
			if (this.getLeftValue() == null) {
				if (this.getRightValue() != null) {
					return true;
				}
				return false;
			}
			if (this.getRightValue() == null) {
				return true;
			}
			// 比较左右值
			if (this.getLeftValue().compareTo(this.getRightValue()) != 0) {
				return true;
			}
			return false;
		}
		// 大于
		else if (this.getOperation() == JudgmentOperation.GREATER_THAN) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().compareTo(this.getRightValue()) > 0) {
				return true;
			}
			return false;
		}
		// 小于
		else if (this.getOperation() == JudgmentOperation.LESS_THAN) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().compareTo(this.getRightValue()) < 0) {
				return true;
			}
			return false;
		}
		// 大于等于
		else if (this.getOperation() == JudgmentOperation.GREATER_EQUAL) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().compareTo(this.getRightValue()) >= 0) {
				return true;
			}
			return false;
		}
		// 小于等于
		else if (this.getOperation() == JudgmentOperation.LESS_EQUAL) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			// 比较左右值
			if (this.getLeftValue().compareTo(this.getRightValue()) <= 0) {
				return true;
			}
			return false;
		}
		return super.result();
	}

}
