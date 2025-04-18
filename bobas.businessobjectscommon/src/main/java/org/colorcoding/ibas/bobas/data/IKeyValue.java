package org.colorcoding.ibas.bobas.data;

/**
 * 键，值
 * 
 * @author Niuren.Zhu
 *
 */
public interface IKeyValue<T> {
	/**
	 * 获取-键
	 * 
	 * @return
	 */
	String getKey();

	/**
	 * 设置-键
	 * 
	 * @param value
	 */
	void setKey(String value);

	/**
	 * 获取-值
	 * 
	 * @return
	 */
	T getValue();

	/**
	 * 设置-值
	 * 
	 * @param value
	 */
	void setValue(T value);

	/**
	 * 输出字符串
	 * 
	 * @return
	 */
	String toString();
}
