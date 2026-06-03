package org.colorcoding.ibas.bobas.bo;

public interface IUserFields extends Iterable<IUserField<?>> {

	/**
	 * 获取用户字段
	 *
	 * @param name 字段名称
	 * @return 用户字段实例（不存在时抛IndexOutOfBoundsException）
	 */
	<P> IUserField<P> get(String name);

	/**
	 * 获取用户字段
	 *
	 * @param index 字段索引
	 * @return 用户字段实例
	 */
	<P> IUserField<P> get(int index);

	/**
	 * 用户字段数量
	 *
	 * @return 字段数量
	 */
	int size();

	/**
	 * 获取字段索引
	 *
	 * @param item 用户字段实例
	 * @return 索引位置（未找到返回-1）
	 */
	int indexOf(IUserField<?> item);

	/**
	 * 获取字段索引
	 *
	 * @param name 字段名称
	 * @return 索引位置（未找到返回-1）
	 */
	int indexOf(String name);

	/**
	 * 注册用户字段（已存在则返回已有实例）
	 *
	 * @param name      字段名称
	 * @param valueType 值类型
	 * @return 用户字段实例
	 */
	<P> IUserField<P> register(String name, Class<?> valueType);
}
