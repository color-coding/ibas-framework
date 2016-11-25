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
	 * 获取-单据号 主键
	 * 
	 * @return
	 */
	Integer getDocEntry();

	/**
	 * 设置-单据号 主键
	 * 
	 * @param value
	 */
	void setDocEntry(Integer value);

	/**
	 * 获取-单据流水号
	 * 
	 * @return
	 */
	Integer getDocNum();

	/**
	 * 设置-单据流水号
	 * 
	 * @param value
	 */
	void setDocNum(Integer value);

	/**
	 * 获取-期间
	 * 
	 * @return
	 */
	Integer getPeriod();

	/**
	 * 设置-期间
	 * 
	 * @param value
	 */
	void setPeriod(Integer value);

	/**
	 * 获取-状态
	 * 
	 * @return
	 */
	emBOStatus getStatus();

	/**
	 * 设置-状态
	 * 
	 * @param value
	 */
	void setStatus(emBOStatus value);

	/**
	 * 获取-单据状态
	 * 
	 * @return
	 */
	emDocumentStatus getDocumentStatus();

	/**
	 * 设置-单据状态
	 * 
	 * @param value
	 */
	void setDocumentStatus(emDocumentStatus value);

	/**
	 * 获取-过账日期
	 * 
	 * @return
	 */
	DateTime getPostingDate();

	/**
	 * 设置-过账日期
	 * 
	 * @param value
	 */
	void setPostingDate(DateTime value);

	/**
	 * 获取-到期日
	 * 
	 * @return
	 */
	DateTime getDeliveryDate();

	/**
	 * 设置-到期日
	 * 
	 * @param value
	 */
	void setDeliveryDate(DateTime value);

	/**
	 * 获取-凭证日期
	 * 
	 * @return
	 */
	DateTime getDocumentDate();

	/**
	 * 设置-凭证日期
	 * 
	 * @param value
	 */
	void setDocumentDate(DateTime value);
}
