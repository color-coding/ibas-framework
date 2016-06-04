package org.colorcoding.ibas.bobas.expressions;

/**
 * 判断表达式
 * 
 * @author Niuren.Zhu
 *
 */
public interface IJudgmentExpression extends IExpression {
	/**
	 * 表达式结果
	 * 
	 * @return true，成立；false，不成立
	 * @throws NotSupportOperationException
	 */
	boolean result() throws NotSupportOperationException;

	/**
	 * 获取表达式-左值
	 * 
	 * @return
	 */
	Object getLeftValue();

	/**
	 * 设置表达式-右值
	 * 
	 * @param value
	 */
	void setLeftValue(Object value);

	/**
	 * 获取表达式-右值
	 * 
	 * @return
	 */
	Object getRightValue();

	/**
	 * 设置表达式-右值
	 * 
	 * @param value
	 */
	void setRightValue(Object value);

	/**
	 * 获取表达式-计算方式
	 * 
	 * @return
	 */
	JudmentOperations getOperation();

	/**
	 * 设置表达式-计算方式
	 * 
	 * @param value
	 */
	void setOperation(JudmentOperations value);

}
