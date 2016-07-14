package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 查询条件
 * 
 * @author Niuren.Zhu
 *
 */
public interface ICondition {
	/**
	 * 获取-条件字段（属性）名
	 * 
	 * @return 名
	 */
	String getAlias();

	/**
	 * 设置-条件字段（属性）名
	 * 
	 * @param value
	 *            名
	 */
	void setAlias(String value);

	/**
	 * 获取-几个闭括号"）"
	 * 
	 * @return 比括号个数
	 */
	int getBracketCloseNum();

	/**
	 * 设置-几个闭括号"）"
	 * 
	 * @param value
	 *            个数
	 */
	void setBracketCloseNum(int value);

	/**
	 * 获取-几个开括号"（"
	 * 
	 * @return 个数
	 */
	int getBracketOpenNum();

	/**
	 * 设置-几个开括号"（"
	 * 
	 * @param value
	 *            个数
	 */
	void setBracketOpenNum(int value);

	/**
	 * 获取- 比较的字段（属性）名
	 * 
	 * @return 个数
	 */
	String getComparedAlias();

	/**
	 * 设置-比较的字段（属性）名
	 * 
	 * @param value
	 *            名
	 */
	void setComparedAlias(String value);

	/**
	 * 获取-比较的值
	 * 
	 * @return 值
	 */
	String getCondVal();

	/**
	 * 设置-比较的值
	 * 
	 * @param value
	 *            值
	 */
	void setCondVal(String value);

	/**
	 * 设置-比较的值
	 * 
	 * @param value
	 *            值
	 */
	void setCondVal(Object value);

	/**
	 * 获取-比较方法
	 * 
	 * @return 方法
	 */
	ConditionOperation getOperation();

	/**
	 * 设置-比较方法
	 * 
	 * @param value
	 *            方法
	 */
	void setOperation(ConditionOperation value);

	/**
	 * 获取-和后续条件关系
	 * 
	 * @return 关系
	 */
	ConditionRelationship getRelationship();

	/**
	 * 设置-和后续条件关系
	 * 
	 * @param value
	 *            关系
	 */
	void setRelationship(ConditionRelationship value);

	/**
	 * 获取-条件字段类型的字段
	 * 
	 * @return
	 */
	DbFieldType getAliasDataType();

	/**
	 * 设置-条件字段类型的字段
	 * 
	 * @param value
	 */
	void setAliasDataType(DbFieldType value);

	/**
	 * 获取-备注
	 * 
	 * @return
	 */
	String getRemarks();

	/**
	 * 设置-备注
	 * 
	 * @param value
	 */
	void setRemarks(String value);
}