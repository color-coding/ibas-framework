package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.core.ICloneable;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.db.DataType;

/**
 * 查询条件
 * 
 * @author Niuren.Zhu
 *
 */
public interface ICondition extends ICloneable {
	/**
	 * 获取-条件字段（属性）名
	 * 
	 * @return 名
	 */
	String getAlias();

	/**
	 * 设置-条件字段（属性）名
	 * 
	 * @param value 名
	 */
	void setAlias(String value);

	/**
	 * 设置-条件字段（属性）
	 * 
	 * @param value 名
	 */
	void setAlias(IPropertyInfo<?> property);

	/**
	 * 获取-闭括号数量
	 *
	 * @return 闭括号个数
	 */
	int getBracketClose();

	/**
	 * 设置-几个闭括号"）"
	 * 
	 * @param value 个数
	 */
	void setBracketClose(int value);

	/**
	 * 增加闭括号数（+1）
	 */
	int addBracketClose();

	/**
	 * 获取-几个开括号"（"
	 * 
	 * @return 个数
	 */
	int getBracketOpen();

	/**
	 * 设置-几个开括号"（"
	 * 
	 * @param value 个数
	 */
	void setBracketOpen(int value);

	/**
	 * 增加开括号数（+1）
	 */
	int addBracketOpen();

	/**
	 * 获取-比较的字段（属性）名
	 *
	 * @return 字段名
	 */
	String getComparedAlias();

	/**
	 * 设置-比较的字段（属性）名
	 * 
	 * @param value 名
	 */
	void setComparedAlias(String value);

	/**
	 * 设置-比较的字段（属性）
	 * 
	 * @param value 名
	 */
	void setComparedAlias(IPropertyInfo<?> property);

	/**
	 * 获取-比较的值
	 * 
	 * @return 值
	 */
	String getValue();

	/**
	 * 设置-比较的值
	 * 
	 * @param value 值
	 */
	void setValue(Object value);

	/**
	 * 设置-比较的值
	 * 
	 * @param value 值
	 */
	void setValue(String value);

	/**
	 * 获取-比较方法
	 * 
	 * @return 方法
	 */
	ConditionOperation getOperation();

	/**
	 * 设置-比较方法
	 * 
	 * @param value 方法
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
	 * @param value 关系
	 */
	void setRelationship(ConditionRelationship value);

	/**
	 * 获取-条件字段的数据类型
	 *
	 * @return 数据库字段类型
	 */
	DataType getAliasDataType();

	/**
	 * 设置-条件字段的数据类型
	 *
	 * @param value 数据库字段类型
	 */
	void setAliasDataType(DataType value);

	/**
	 * 克隆
	 *
	 * @return 克隆后的条件
	 */
	@Override
	ICondition clone();
}