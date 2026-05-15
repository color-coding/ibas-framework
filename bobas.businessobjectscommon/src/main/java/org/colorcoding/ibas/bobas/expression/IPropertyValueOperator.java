package org.colorcoding.ibas.bobas.expression;

/**
 * 对象属性值操作者
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPropertyValueOperator extends IValueOperator {
	/**
	 * 获取属性名称
	 *
	 * @return 属性名称
	 */
	String getPropertyName();

	/**
	 * 设置属性名称
	 *
	 * @param value 属性名称
	 */
	void setPropertyName(String value);
}
