package org.colorcoding.ibas.bobas.expressions;

/**
 * 表达式基类
 * 
 * @author Niuren.Zhu
 *
 * @param <T>
 *            比较的值类型
 */
public abstract class JudgmentExpression<T> implements IJudgmentExpression {

    public JudgmentExpression() {

    }

    public JudgmentExpression(T leftValue, JudmentOperations operation, T rightValue) {
        this.setLeftValue(leftValue);
        this.setRightValue(rightValue);
        this.setOperation(operation);
    }

    private JudmentOperations operation;

    @Override
    public final JudmentOperations getOperation() {
        return this.operation;
    }

    @Override
    public final void setOperation(JudmentOperations value) {
        this.operation = value;
    }

    @Override
    public boolean result() throws NotSupportOperationException {
        // 不支持的计算
        throw new NotSupportOperationException();
    }

    @Override
    public String toString() {
        return String.format(("{judgment expression: %s %s %s}"), this.getLeftValue(), this.getOperation(),
                this.getRightValue());
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
