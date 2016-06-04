package org.colorcoding.ibas.btulz.models;

import org.colorcoding.ibas.btulz.data.emModelType;

/**
 * 业务对象模型
 * 
 * @author Niuren.Zhu
 *
 */
public interface IModel {
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
	 * 获取-短名称
	 * 
	 * @return
	 */
	String getShortName();

	/**
	 * 设置-短名称
	 * 
	 * @param name
	 */
	void setShortName(String name);

	/**
	 * 获取-模型类型
	 * 
	 * @return
	 */
	emModelType getModelType();

	/**
	 * 设置-模型类型
	 * 
	 * @param modelType
	 */
	void setModelType(emModelType modelType);

	/**
	 * 获取-属性集合
	 * 
	 * @return
	 */
	IProperties getProperties();

	/**
	 * 获取-绑定到
	 * 
	 * @return
	 */
	String getMapped();

	/**
	 * 设置-绑定到
	 * 
	 * @param mapped
	 */
	void setMapped(String mapped);

}
