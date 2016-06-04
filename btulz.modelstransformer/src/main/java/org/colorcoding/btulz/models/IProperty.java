package org.colorcoding.btulz.models;

import org.colorcoding.btulz.data.emPropertyType;

/**
 * 基本属性
 * 
 * @author Niuren.Zhu
 *
 */
public interface IProperty {
	/**
	 * 获取-名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 设置-名称
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * 获取-描述
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * 设置-描述
	 * 
	 * @param description
	 */
	void setDescription(String description);

	/**
	 * 获取属性类型
	 * 
	 * @return
	 */
	emPropertyType getPropertyType();

	/**
	 * 获取-声明类型
	 * 
	 * @return
	 */
	String getDeclaredType();

	/**
	 * 设置-声明类型
	 * 
	 * @param declaredType
	 */
	void setDeclaredType(String declaredType);
}
