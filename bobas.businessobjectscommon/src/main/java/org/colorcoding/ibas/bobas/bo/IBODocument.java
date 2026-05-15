package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;

/**
 * 单据
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBODocument extends IBusinessObject, IBOStorageTag {
	/**
	 * 主要的主键名称
	 */
	final static String MASTER_PRIMARY_KEY_NAME = "DocEntry";

	/**
	 * 获取单据号（主键）
	 *
	 * @return 单据号
	 */
	Integer getDocEntry();

	/**
	 * 设置单据号
	 *
	 * @param value 单据号
	 */
	void setDocEntry(Integer value);

	/**
	 * 获取对象状态
	 *
	 * @return 对象状态
	 */
	emBOStatus getStatus();

	/**
	 * 设置对象状态
	 *
	 * @param value 对象状态
	 */
	void setStatus(emBOStatus value);

	/**
	 * 获取单据状态
	 *
	 * @return 单据状态
	 */
	emDocumentStatus getDocumentStatus();

	/**
	 * 设置单据状态
	 *
	 * @param value 单据状态
	 */
	void setDocumentStatus(emDocumentStatus value);

	/**
	 * 获取过账日期
	 *
	 * @return 过账日期
	 */
	DateTime getPostingDate();

	/**
	 * 设置过账日期
	 *
	 * @param value 过账日期
	 */
	void setPostingDate(DateTime value);

	/**
	 * 获取到期日
	 *
	 * @return 到期日
	 */
	DateTime getDeliveryDate();

	/**
	 * 设置到期日
	 *
	 * @param value 到期日
	 */
	void setDeliveryDate(DateTime value);

	/**
	 * 获取凭证日期
	 *
	 * @return 凭证日期
	 */
	DateTime getDocumentDate();

	/**
	 * 设置凭证日期
	 *
	 * @param value 凭证日期
	 */
	void setDocumentDate(DateTime value);
}
