package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 用户字段集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IUserFields extends Iterable<IUserField> {

	/**
	 * 获取用户字段
	 * 
	 * @param name 名称
	 * @return
	 */
	IUserField get(String name);

	/**
	 * 获取用户字段
	 * 
	 * @param index 索引
	 * @return
	 */
	IUserField get(int index);

	/**
	 * 注册用户字段
	 * 
	 * @param name 名称
	 * @param type 值类型
	 * @return
	 */
	IUserField register(String name, DbFieldType valueType);

	/**
	 * 注册用户字段到全局
	 */
	void register();

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
	int indexOf(IUserField item);
}
