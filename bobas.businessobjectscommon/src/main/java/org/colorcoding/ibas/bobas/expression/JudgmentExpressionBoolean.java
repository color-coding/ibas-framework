package org.colorcoding.ibas.bobas.expression;

/**
 * 布尔值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionBoolean extends JudgmentExpression<Boolean> {

	public JudgmentExpressionBoolean() {

	}

	public JudgmentExpressionBoolean(Boolean leftValue, JudmentOperation operation, Boolean rightValue) {
		super(leftValue, operation, rightValue);
	}

	@Override
	public boolean result() throws ExpressionException {
		// 等
		if (this.getOperation() == JudmentOperation.EQUAL) {
			if (this.getLeftValue() == this.getRightValue()) {
				return true;
			}
			return false;
		}
		// 不等
		else if (this.getOperation() == JudmentOperation.NOT_EQUAL) {
			if (this.getLeftValue() != this.getRightValue()) {
				return true;
			}
			return false;
		}
		// 且
		else if (this.getOperation() == JudmentOperation.AND) {
			if (this.getLeftValue() && this.getRightValue()) {
				return true;
			}
			return false;
		}
		// 或
		else if (this.getOperation() == JudmentOperation.OR) {
			if (this.getLeftValue() || this.getRightValue()) {
				return true;
			}
			return false;
		}
		// 不支持的
		return super.result();
	}

	private boolean leftValue;

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
		}
	}

	public final void setLeftValue(String value) {
		this.setLeftValue(Boolean.valueOf(value));
	}

	public final void setLeftValue(Boolean value) {
		this.leftValue = value;
	}

	private boolean rightValue;

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
		}
	}

	public final void setRightValue(String value) {
		this.setRightValue(Boolean.valueOf(value));
	}

	public final void setRightValue(Boolean value) {
		this.rightValue = value;
	}
}
