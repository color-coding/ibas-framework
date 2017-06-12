package org.colorcoding.ibas.bobas.expressions;

/**
 * 对象属性值操作者
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPropertyValueOperator extends IValueOperator {
	/**
	 * 设置属性名称
	 * 
	 * @return
	 */
	String getPropertyName();

	/**
	 * 获取属性名称
	 * 
	 * @param value
	 */
	void setPropertyName(String value);
}
