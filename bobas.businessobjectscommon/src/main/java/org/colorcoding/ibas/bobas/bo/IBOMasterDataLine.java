package org.colorcoding.ibas.bobas.bo;

public interface IBOMasterDataLine extends IBusinessObject, IBOLine, IBOStorageTag {
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
	 * 获取-行编号 主键
	 * 
	 * @return
	 */
	Integer getLineId();

	/**
	 * 设置-行编号 主键
	 * 
	 * @param value
	 */
	void setLineId(Integer value);
}
