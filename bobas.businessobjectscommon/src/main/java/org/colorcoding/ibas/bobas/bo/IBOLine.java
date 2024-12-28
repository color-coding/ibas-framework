package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.common.Strings;

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
	 * 获取行对象的第一主键值
	 * 
	 * @return
	 */
	default String getMasterPrimaryKey() {
		if (this instanceof IBODocumentLine) {
			return Strings.valueOf(((IBODocumentLine) this).getDocEntry());
		} else if (this instanceof IBOSimpleLine) {
			return Strings.valueOf(((IBOSimpleLine) this).getObjectKey());
		} else if (this instanceof IBOMasterDataLine) {
			return ((IBOMasterDataLine) this).getCode();
		}
		throw new AbstractMethodError();
	}

	/**
	 * 获取-行编号 主键
	 * 
	 * @return
	 */
	Integer getLineId();

	/**
	 * 设置-行编号 主键
	 * 
	 * @param value
	 */
	void setLineId(Integer value);
}
