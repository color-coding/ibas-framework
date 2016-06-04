package org.colorcoding.ibas.bobas.bo;

public interface IBOMasterData extends IBusinessObject, IBOStorageTag {
	/**
	 * 获取-编码 主键
	 * 
	 * @return
	 */
	String getCode();

	/**
	 * 设置-编码 主键
	 * 
	 * @param value
	 */
	void setCode(String value);

	/**
	 * 获取-名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 设置-名称
	 * 
	 * @param value
	 */
	void setName(String value);

	/**
	 * 获取-单据号 （可从ONNM获取）
	 * 
	 * @return
	 */
	Integer getDocEntry();

	/**
	 * 设置-单据号 （可从ONNM获取）
	 * 
	 * @param value
	 */
	void setDocEntry(Integer value);

}
