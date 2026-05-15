package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.DateTime;

public interface IBOStorageTag {

	/**
	 * 是否为新建
	 *
	 * @return 是否新建
	 */
	boolean isNew();

	/**
	 * 获取对象编号
	 *
	 * @return 对象编码
	 */
	String getObjectCode();

	/**
	 * 获取创建日期
	 *
	 * @return 创建日期
	 */
	DateTime getCreateDate();

	/**
	 * 设置创建日期
	 *
	 * @param value 创建日期
	 */
	void setCreateDate(DateTime value);

	/**
	 * 获取创建时间（HHMM格式，如1430表示14:30）
	 *
	 * @return 创建时间
	 */
	Short getCreateTime();

	/**
	 * 设置创建时间（HHMM格式，如1430表示14:30）
	 *
	 * @param value 创建时间
	 */
	void setCreateTime(Short value);

	/**
	 * 获取更新日期
	 *
	 * @return 更新日期
	 */
	DateTime getUpdateDate();

	/**
	 * 设置更新日期
	 *
	 * @param value 更新日期
	 */
	void setUpdateDate(DateTime value);

	/**
	 * 获取更新时间（HHMM格式，如1430表示14:30）
	 *
	 * @return 更新时间
	 */
	Short getUpdateTime();

	/**
	 * 设置更新时间（HHMM格式，如1430表示14:30）
	 *
	 * @param value 更新时间
	 */
	void setUpdateTime(Short value);

	/**
	 * 获取实例号（版本号）
	 *
	 * @return 实例号
	 */
	Integer getLogInst();

	/**
	 * 设置实例号
	 *
	 * @param value 实例号
	 */
	void setLogInst(Integer value);

	/**
	 * 获取创建用户标识
	 *
	 * @return 用户标识
	 */
	Integer getCreateUserSign();

	/**
	 * 设置创建用户标识
	 *
	 * @param value 用户标识
	 */
	void setCreateUserSign(Integer value);

	/**
	 * 获取更新用户标识
	 *
	 * @return 用户标识
	 */
	Integer getUpdateUserSign();

	/**
	 * 设置更新用户标识
	 *
	 * @param value 用户标识
	 */
	void setUpdateUserSign(Integer value);

	/**
	 * 获取创建动作标识
	 *
	 * @return 动作标识
	 */
	String getCreateActionId();

	/**
	 * 设置创建动作标识
	 *
	 * @param value 动作标识
	 */
	void setCreateActionId(String value);

	/**
	 * 获取更新动作标识
	 *
	 * @return 动作标识
	 */
	String getUpdateActionId();

	/**
	 * 设置更新动作标识
	 *
	 * @param value 动作标识
	 */
	void setUpdateActionId(String value);

	/**
	 * 获取数据源
	 *
	 * @return 数据源标识
	 */
	String getDataSource();

	/**
	 * 设置数据源
	 *
	 * @param value 数据源标识
	 */
	void setDataSource(String value);
}
