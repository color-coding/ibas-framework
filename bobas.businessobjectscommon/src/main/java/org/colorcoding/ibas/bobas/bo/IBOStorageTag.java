package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.DateTime;

public interface IBOStorageTag {
	/**
	 * 获取-获取对象编号
	 * 
	 * @return
	 */
	String getObjectCode();

	/**
	 * 获取-创建日期
	 * 
	 * @return
	 */
	DateTime getCreateDate();

	/**
	 * 设置-创建日期
	 * 
	 * @param value
	 */
	void setCreateDate(DateTime value);

	/**
	 * 获取-创建时间
	 * 
	 * @return
	 */
	Short getCreateTime();

	/**
	 * 设置-创建时间
	 * 
	 * @param value
	 */
	void setCreateTime(Short value);

	/**
	 * 获取-更新日期
	 * 
	 * @return
	 */
	DateTime getUpdateDate();

	/**
	 * 设置-更新日期
	 * 
	 * @param value
	 */
	void setUpdateDate(DateTime value);

	/**
	 * 获取-更细时间
	 * 
	 * @return
	 */
	Short getUpdateTime();

	/**
	 * 设置-更细时间
	 * 
	 * @param value
	 */
	void setUpdateTime(Short value);

	/**
	 * 获取-实例号
	 * 
	 * @return
	 */
	Integer getLogInst();

	/**
	 * 设置-实例号
	 * 
	 * @param value
	 */
	void setLogInst(Integer value);

	/**
	 * 获取-创建用户
	 * 
	 * @return
	 */
	Integer getCreateUserSign();

	/**
	 * 获取-设置-创建用户
	 * 
	 * @param value
	 */
	void setCreateUserSign(Integer value);

	/**
	 * 获取-更新用户
	 * 
	 * @return
	 */
	Integer getUpdateUserSign();

	/**
	 * 设置-更新用户
	 * 
	 * @param value
	 */
	void setUpdateUserSign(Integer value);

	/**
	 * 获取-创建动作标识
	 * 
	 * @return
	 */
	String getCreateActionId();

	/**
	 * 设置-创建动作标识
	 * 
	 * @param value
	 */
	void setCreateActionId(String value);

	/**
	 * 获取-更新动作标识
	 * 
	 * @return
	 */
	String getUpdateActionId();

	/**
	 * 设置-更新动作标识
	 * 
	 * @param value
	 */
	void setUpdateActionId(String value);

	/**
	 * 获取-数据源
	 * 
	 * @return
	 */
	String getDataSource();

	/**
	 * 设置-数据源
	 * 
	 * @param value
	 */
	void setDataSource(String value);
}
