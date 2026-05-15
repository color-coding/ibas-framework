package org.colorcoding.ibas.bobas.bo;

/**
 * 系列自动编号（通过ONNM获取编号）
 *
 * @author Niuren.Zhu
 *
 */
public interface IBOSeriesKey extends IBOStorageTag {

	/**
	 * 获取-编号系列
	 *
	 * @return 系列编号
	 */
	Integer getSeries();

	/**
	 * 设置-系列编号（参数为Object以支持多种编号类型）
	 *
	 * @param value 系列编号
	 */
	void setSeriesValue(Object value);
}