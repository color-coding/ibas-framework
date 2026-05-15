package org.colorcoding.ibas.bobas.bo;

public interface IUserField<P> {
	/**
	 * 获取字段名称
	 *
	 * @return 字段名称
	 */
	String getName();

	/**
	 * 获取值类型
	 *
	 * @return 值的Class类型
	 */
	Class<?> getValueType();

	/**
	 * 获取字段值
	 *
	 * @return 字段值（可能为null）
	 */
	P getValue();

	/**
	 * 设置字段值
	 *
	 * @param value 新值
	 */
	void setValue(Object value);
}
