package org.colorcoding.ibas.bobas.data;

/**
 * 单值对象
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISingleValue {
	/**
	 * 设置值
	 * 
	 * @param value
	 */
	void setValue(Object value);

	/**
	 * 获取值
	 * 
	 * @return 值
	 */
	Object getValue();
}
