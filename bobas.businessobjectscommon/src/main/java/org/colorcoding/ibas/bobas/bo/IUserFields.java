package org.colorcoding.ibas.bobas.bo;

public interface IUserFields extends Iterable<IUserField<?>> {

	/**
	 * 获取用户字段
	 * 
	 * @param name 名称
	 * @return
	 */
	<P> IUserField<P> get(String name);

	/**
	 * 获取用户字段
	 * 
	 * @param index 索引
	 * @return
	 */
	<P> IUserField<P> get(int index);

	/**
	 * 注册用户字段
	 * 
	 * @param name 名称
	 * @param type 值类型
	 * @return
	 */
	<P> IUserField<P> register(String name, Class<P> valueType);

	/**
	 * 用户字段长度
	 * 
	 * @return
	 */
	int size();

	/**
	 * 获取索引
	 * 
	 * @param item 对象
	 * @return
	 */
	int indexOf(IUserField<?> item);

	/**
	 * 获取索引
	 * 
	 * @param name
	 * @return
	 */
	int indexOf(String name);
}
