package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.ITrackable;

public interface IBusinessObject extends Cloneable, ITrackable {

	/**
	 * 获取对象查询条件
	 * 
	 * @return 当前的查询条件
	 */
	ICriteria getCriteria();

	/**
	 * 对象识别码
	 * 
	 * @return
	 */
	String getIdentifiers();

	/**
	 * 删除数据
	 */
	void delete();

	/**
	 * 取消删除数据
	 */
	void undelete();
}
