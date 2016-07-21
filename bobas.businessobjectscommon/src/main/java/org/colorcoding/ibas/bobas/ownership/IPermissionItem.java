package org.colorcoding.ibas.bobas.ownership;

/**
 * 权限项
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPermissionItem {
	/**
	 * 获取-组
	 * 
	 * @return
	 */
	String getGroup();

	/**
	 * 设置-组
	 * 
	 * @param group
	 */
	void setGroup(String group);

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
	 * 获取-权限值
	 * 
	 * @return
	 */
	PermissionValue getValue();

	/**
	 * 设置-权限值
	 * 
	 * @param value
	 */
	void setValue(PermissionValue value);
}
