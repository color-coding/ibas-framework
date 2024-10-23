package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 期间的数据
 */
public interface IPeriodData {
	/**
	 * 获取-数据类型
	 * 
	 * @return
	 */
	String getObjectCode();

	/**
	 * 获取-单据日期
	 * 
	 * @return
	 */
	DateTime getDocumentDate();

	/**
	 * 获取-期间
	 * 
	 * @return
	 */
	Integer getPeriod();

	/**
	 * 设置-期间
	 * 
	 * @param value
	 */
	void setPeriod(Integer value);
}
