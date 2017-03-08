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
	 * 设置-编号系列
	 * 
	 * @param value
	 */
	void setSeries(Integer value);

	/**
	 * 获取-系列编号
	 * 
	 * @return
	 */
	Object getSeriesValue();

	/**
	 * 设置-系列编号
	 * 
	 * @param value
	 */
	void setSeriesValue(Object value);
}
