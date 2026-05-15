package org.colorcoding.ibas.bobas.bo;

public interface IBOSimpleLine extends IBusinessObject, IBOLine, IBOStorageTag {
	/**
	 * 主要的主键名称
	 */
	final static String MASTER_PRIMARY_KEY_NAME = "ObjectKey";

	/**
	 * 获取-主键
	 *
	 * @return 主键值
	 */
	Integer getObjectKey();

	/**
	 * 设置-主键
	 *
	 * @param value 主键值
	 */
	void setObjectKey(Integer value);

	/**
	 * 获取-行编号 主键
	 *
	 * @return 行编号
	 */
	Integer getLineId();

	/**
	 * 设置-行编号 主键
	 *
	 * @param value 行编号
	 */
	void setLineId(Integer value);
}