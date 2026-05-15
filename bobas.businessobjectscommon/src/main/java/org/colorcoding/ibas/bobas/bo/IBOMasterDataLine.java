package org.colorcoding.ibas.bobas.bo;

public interface IBOMasterDataLine extends IBusinessObject, IBOLine, IBOStorageTag {
	/**
	 * 主要的主键名称
	 */
	final static String MASTER_PRIMARY_KEY_NAME = "Code";

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
	 * 获取行编号（主键）
	 *
	 * @return 行编号
	 */
	Integer getLineId();

	/**
	 * 设置行编号
	 *
	 * @param value 行编号
	 */
	void setLineId(Integer value);
}
