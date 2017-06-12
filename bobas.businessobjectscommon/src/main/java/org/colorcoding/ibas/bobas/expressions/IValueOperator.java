package org.colorcoding.ibas.bobas.expressions;

/**
 * 值操作员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IValueOperator {
	/**
	 * 获取值
	 * 
	 * @return
	 */
	Object getValue();

	/**
	 * 设置值
	 * 
	 * @param value
	 *            值
	 */
	void setValue(Object value);

	/**
	 * 获取值的类型
	 * 
	 * @return
	 */
	Class<?> getValueClass();
}
