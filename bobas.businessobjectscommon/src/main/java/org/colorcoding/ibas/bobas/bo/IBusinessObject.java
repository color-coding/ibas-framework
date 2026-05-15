package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.ITrackable;

public interface IBusinessObject extends ITrackable {

	/**
	 * 是否处于忙状态
	 *
	 * @return 是否忙
	 */
	boolean isBusy();

	/**
	 * 设置忙状态
	 *
	 * @param value 是否忙
	 */
	void setBusy(boolean value);

	/**
	 * 是否有效（无效数据会被过滤）
	 *
	 * @return 是否有效
	 */
	boolean isValid();

	/**
	 * 设置有效状态
	 *
	 * @param value 是否有效
	 */
	void setValid(boolean value);

	/**
	 * 获取对象查询条件（无条件时返回null）
	 *
	 * @return 当前的查询条件
	 */
	ICriteria getCriteria();

	/**
	 * 对象识别码（格式如 {[Code][DocEntry = 123]})
	 *
	 * @return 识别码字符串
	 */
	String getIdentifiers();

	/**
	 * 删除数据（包括子项及属性）
	 */
	void delete();

	/**
	 * 取消删除标记（包括子项及属性）
	 */
	void undelete();

}