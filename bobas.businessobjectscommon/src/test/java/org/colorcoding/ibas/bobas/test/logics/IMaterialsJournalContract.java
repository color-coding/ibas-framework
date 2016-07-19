package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.logics.IBusinessLogicContract;

/**
 * 物料交易记录契约
 * 
 * @author Niuren.Zhu
 *
 */
public interface IMaterialsJournalContract extends IBusinessLogicContract {
	/**
	 * 物料编码
	 * 
	 * @return
	 */
	String getItemCode();

	/**
	 * 数量
	 * 
	 * @return
	 */
	Decimal getQuantity();

	/**
	 * 获取-基本单据类型
	 * 
	 * @return 值
	 */
	String getDocumentType();

	/**
	 * 获取-基本单据内部标识
	 * 
	 * @return 值
	 */
	Integer getDocumentEntry();

	/**
	 * 获取-基本凭证中的行编号
	 * 
	 * @return 值
	 */
	Integer getDocumentLineId();

}
