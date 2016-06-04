package org.colorcoding.ibas.btulz.models;

/**
 * 业务对象域
 * 
 * @author Niuren.Zhu
 *
 */
public interface IDomain {
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
	 * 获取-短名称
	 * 
	 * @return
	 */
	String getShortName();

	/**
	 * 设置-短名称
	 * 
	 * @param shortName
	 */
	void setShortName(String shortName);

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
	 * 获取-模型集合
	 * 
	 * @return
	 */
	IModels getModels();
}
