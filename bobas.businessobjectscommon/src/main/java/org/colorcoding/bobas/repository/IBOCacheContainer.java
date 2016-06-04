package org.colorcoding.bobas.repository;

import org.colorcoding.bobas.core.IBusinessObjectBase;
import org.colorcoding.bobas.data.DateTime;

/**
 * 业务对象缓存容器
 */
public interface IBOCacheContainer {
	/**
	 * 获取容器数据的键值
	 * 
	 * @return
	 */
	String getDataKey();

	/**
	 * 获取缓存的时间
	 */
	DateTime getCacheTime();

	/**
	 * 设置缓存的时间
	 */
	void setCacheTime(DateTime time);

	/**
	 * 获取数据
	 */
	IBusinessObjectBase getData();

	/**
	 * 设置数据
	 */
	void setData(IBusinessObjectBase data);

	/**
	 * 数据是否过期
	 */
	boolean isExpired();

	/**
	 * 获取有效时间
	 */
	long getEffectiveTime();

	/**
	 * 设置有效时间，分钟
	 * 
	 * @param minute
	 *            有效分钟
	 */
	void setEffectiveTime(long minute);

}
