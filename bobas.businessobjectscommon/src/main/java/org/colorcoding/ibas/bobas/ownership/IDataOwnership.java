package org.colorcoding.ibas.bobas.ownership;

/**
 * 数据所有权
 */
public interface IDataOwnership {

	/**
	 * 获取-数据类型
	 * 
	 * @return
	 */
	String getObjectCode();

	/**
	 * 获取-数据所有者
	 * 
	 * @return 值
	 */
	Integer getDataOwner();

	/**
	 * 设置-数据所有者
	 * 
	 * @param value
	 *            值
	 */
	void setDataOwner(Integer value);

	/**
	 * 获取-数据所属组织
	 * 
	 * @return 值
	 */
	String getOrganization();

	/**
	 * 设置-数据所属组织
	 * 
	 * @param value
	 *            值
	 */
	void setOrganization(String value);
}
