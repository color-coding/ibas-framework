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
	 * 获取-数据所有人
	 * 
	 * @return
	 */
	int getDataOwner();

	/**
	 * 获取-数据所属组织
	 * 
	 * @return
	 */
	String getOrganization();
}
