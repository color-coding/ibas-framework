package org.colorcoding.ibas.bobas.bo;

/**
 * 行对象
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOLine extends IBusinessObject {
	/**
	 * 主要的主键名称
	 */
	final static String SECONDARY_PRIMARY_KEY_NAME = "LineId";

	/**
	 * 获取行编号（主键）
	 *
	 * @return 行编号
	 */
	Integer getLineId();

	/**
	 * 设置行编号
	 *
	 * @param value 行编号
	 */
	void setLineId(Integer value);
}
