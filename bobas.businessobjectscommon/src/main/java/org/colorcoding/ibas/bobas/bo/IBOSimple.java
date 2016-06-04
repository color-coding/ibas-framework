package org.colorcoding.ibas.bobas.bo;

public interface IBOSimple extends IBusinessObject, IBOStorageTag {
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
