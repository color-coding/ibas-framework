package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 字符串值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionString extends JudgmentExpressionComparable<String> {
	public JudgmentExpressionString() {

	}

	public JudgmentExpressionString(String leftValue, JudmentOperation operation, String rightValue) {
		super(leftValue, operation, rightValue);
	}

	private String leftValue;

	@Override
	public final String getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		this.setLeftValue(Strings.valueOf(value));
	}

	public final void setLeftValue(String value) {
		this.leftValue = value;
	}

	private String rightValue;

	@Override
	public final String getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		this.setRightValue(Strings.valueOf(value));
	}

	public final void setRightValue(String value) {
		this.rightValue = value;
	}

	@Override
	public boolean result() throws ExpressionException {
		// 开始与
		if (this.getOperation() == JudmentOperation.BEGIN_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue().startsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 结束于
		else if (this.getOperation() == JudmentOperation.END_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue().endsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 非开始于
		else if (this.getOperation() == JudmentOperation.NOT_BEGIN_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (!this.getLeftValue().startsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 非结束于
		else if (this.getOperation() == JudmentOperation.NOT_END_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (!this.getLeftValue().endsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 包含
		else if (this.getOperation() == JudmentOperation.CONTAIN) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue().indexOf(this.getRightValue()) >= 0) {
				return true;
			}
			return false;
		}
		// 不包含
		else if (this.getOperation() == JudmentOperation.NOT_CONTAIN) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue().indexOf(this.getRightValue()) < 0) {
				return true;
			}
			return false;
		}
		// 空值
		else if (this.getOperation() == JudmentOperation.IS_NULL) {
			if (this.getLeftValue() == null) {
				return true;
			}
			return false;
		}
		// 非空值
		else if (this.getOperation() == JudmentOperation.NOT_NULL) {
			if (this.getLeftValue() != null) {
				return false;
			}
			return true;
		}
		// 其他
		return super.result();
	}
}
