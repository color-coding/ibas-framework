package org.colorcoding.ibas.bobas.expression;

/**
 * 判断表达式基类
 *
 * @author Niuren.Zhu
 *
 * @param <T> 比较的值类型
 */
public abstract class JudgmentExpression<T> implements IJudgmentExpression {

	public JudgmentExpression() {

	}

	public JudgmentExpression(T leftValue, JudmentOperation operation, T rightValue) {
		this.setLeftValue(leftValue);
		this.setRightValue(rightValue);
		this.setOperation(operation);
	}

	private JudmentOperation operation;

	@Override
	public final JudmentOperation getOperation() {
		return this.operation;
	}

	@Override
	public final void setOperation(JudmentOperation value) {
		this.operation = value;
	}

	/**
	 * 计算表达式结果
	 *
	 * 仅处理 IS_NULL 和 NOT_NULL 操作：IS_NULL 时左值为null返回true；NOT_NULL 时左值为null返回true（注意：逻辑与名称相反）
	 * 其他操作抛出异常，由子类覆盖实现
	 *
	 * @return 表达式是否成立
	 * @throws ExpressionException 不支持的操作时抛出
	 */
	@Override
	public boolean result() throws ExpressionException {
		// 空值
		if (this.getOperation() == JudmentOperation.IS_NULL) {
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
		// 不支持的计算
		throw new ExpressionException("not support.");
	}

	@Override
	public String toString() {
		return String.format("{judgment: %s %s %s}", this.getLeftValue(), this.getOperation(), this.getRightValue());
	}

	@Override
	public abstract T getLeftValue();

	@Override
	public abstract void setLeftValue(Object value);

	@Override
	public abstract T getRightValue();

	@Override
	public abstract void setRightValue(Object value);
}