package org.colorcoding.ibas.bobas.data;

/**
 * 键-值对
 *
 * @author Niuren.Zhu
 *
 */
public interface IKeyValue {
	/**
	 * 获取键
	 *
	 * @return 键值
	 */
	String getKey();

	/**
	 * 设置键
	 *
	 * @param value 键值
	 */
	void setKey(String value);

	/**
	 * 获取值
	 *
	 * @return 值对象（可能为null）
	 */
	Object getValue();

	/**
	 * 设置值
	 *
	 * @param value 值对象
	 */
	void setValue(Object value);

	/**
	 * 获取强类型值
	 *
	 * @param <T> 目标类型
	 * @return 类型转换后的值
	 */
	<T> T asValue();

	/**
	 * 输出字符串
	 *
	 * @return 字符串表示
	 */
	String toString();
}
