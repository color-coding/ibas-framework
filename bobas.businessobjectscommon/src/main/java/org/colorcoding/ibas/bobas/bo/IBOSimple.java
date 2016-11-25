package org.colorcoding.ibas.bobas.bo;

public interface IBOSimple extends IBusinessObject, IBOStorageTag {
	/**
	 * 主要的主键名称
	 */
	final static String MASTER_PRIMARY_KEY_NAME = "ObjectKey";

	/**
	 * 获取- 主键
	 * 
	 * @return
	 */
	Integer getObjectKey();

	/**
	 * 设置-主键
	 * 
	 * @param value
	 */
	void setObjectKey(Integer value);
}
