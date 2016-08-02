package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 
 * 查询
 *
 * @author Niuren.Zhu
 *
 */
public interface ICriteria {

	/**
	 * 获取-业务对象编码
	 * 
	 * @return
	 */
	String getBusinessObjectCode();

	/**
	 * 设置业务对象编码
	 * 
	 * @param value
	 *            业务对象编码
	 */
	void setBusinessObjectCode(String value);

	/**
	 * 获取查询结果数量
	 * 
	 * @return 查询结果数
	 */
	int getResultCount();

	/**
	 * 设置查询结果数量
	 * 
	 * @param value
	 *            数量
	 */
	void setResultCount(int value);

	/**
	 * 结果是否加载子项
	 * 
	 * @return 是否
	 */
	boolean getNotLoadedChildren();

	/**
	 * 设置是否查询子项
	 * 
	 * @param value
	 *            是否
	 */
	void setNotLoadedChildren(boolean value);

	/**
	 * 获取备注
	 * 
	 * @return 备注
	 */
	String getRemarks();

	/**
	 * 设置备注
	 * 
	 * @param value
	 *            备注
	 */
	void setRemarks(String value);

	/**
	 * 获取查询条件
	 * 
	 * @return 条件集合
	 */
	IConditions getConditions();

	/**
	 * 获取子项查询
	 * 
	 * @return 子项查询
	 */
	IChildCriterias getChildCriterias();

	/**
	 * 获取排序字段
	 * 
	 * @return
	 */
	ISorts getSorts();

	/**
	 * 克隆
	 * 
	 * @return
	 */
	ICriteria clone();

	/**
	 * 转换为字符串
	 * 
	 * @param type
	 * @return
	 */
	String toString(String type);

	/**
	 * 计算下一结果集的查询条件
	 * 
	 * 注意BO多主键情况下，请自行修正。
	 * 
	 * @param lastBO
	 *            起始业务对象
	 * @return 查询
	 */
	ICriteria nextResultCriteria(IBusinessObjectBase lastBO);

	/**
	 * 计算上一个结果集的查询条件
	 * 
	 * 注意BO多主键情况下，请自行修正。
	 * 
	 * @param firstBO
	 *            起始业务对象
	 * @return 查询
	 */
	ICriteria previousResultCriteria(IBusinessObjectBase firstBO);

	/**
	 * 复制查询条件
	 * 
	 * @param criteria
	 *            基于的查询
	 * @return 查询
	 */
	ICriteria copyFrom(ICriteria criteria);

}