package org.colorcoding.ibas.bobas.bo;

/**
 * 系列自动编号
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOSeriesKey extends IBOStorageTag {

	/**
	 * 获取-编号系列
	 * 
	 * @return
	 */
	Integer getSeries();

	/**
	 * 设置-系列编号
	 * 
	 * @param value
	 */
	void setSeriesValue(Object value);
}
