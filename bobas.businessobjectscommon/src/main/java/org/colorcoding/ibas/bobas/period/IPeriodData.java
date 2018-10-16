package org.colorcoding.ibas.bobas.period;

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
	 * 获取-过账日期
	 * 
	 * @return
	 */
	DateTime getPostingDate();

	/**
	 * 设置-过账日期
	 * 
	 * @param value
	 */
	void setPostingDate(DateTime value);

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
