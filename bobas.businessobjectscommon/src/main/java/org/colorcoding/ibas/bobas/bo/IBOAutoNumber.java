package org.colorcoding.ibas.bobas.bo;

/**
 * 自动编号
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOAutoNumber extends IBOStorageTag {

	/**
	 * 获取-编号系列
	 * 
	 * @return
	 */
	Integer getSeries();

	/**
	 * 设置-编号系列
	 * 
	 * @param value
	 */
	void setSeries(Integer value);

	/**
	 * 获取-自动编号
	 * 
	 * @return
	 */
	String getAutoNumber();

	/**
	 * 设置-自动编号
	 * 
	 * @param value
	 */
	void setAutoNumber(String value);
}
