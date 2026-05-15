package org.colorcoding.ibas.bobas.bo;

public interface IBOMasterData extends IBusinessObject, IBOStorageTag {
	/**
	 * 主要的主键名称
	 */
	final static String MASTER_PRIMARY_KEY_NAME = "Code";
	/**
	 * 顺序号名称
	 */
	final static String SERIAL_NUMBER_KEY_NAME = "DocEntry";

	/**
	 * 获取编码（主键）
	 *
	 * @return 编码
	 */
	String getCode();

	/**
	 * 设置编码
	 *
	 * @param value 编码
	 */
	void setCode(String value);

	/**
	 * 获取名称
	 *
	 * @return 名称
	 */
	String getName();

	/**
	 * 设置名称
	 *
	 * @param value 名称
	 */
	void setName(String value);

	/**
	 * 获取单据号（可从ONNM获取）
	 *
	 * @return 单据号
	 */
	Integer getDocEntry();

	/**
	 * 设置单据号
	 *
	 * @param value 单据号
	 */
	void setDocEntry(Integer value);

}
