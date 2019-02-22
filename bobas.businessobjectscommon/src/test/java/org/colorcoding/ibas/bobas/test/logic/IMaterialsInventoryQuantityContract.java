package org.colorcoding.ibas.bobas.test.logic;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.logic.IBusinessLogicContract;

/**
 * 物料库存数量逻辑契约
 * 
 * @author Niuren.Zhu
 *
 */
public interface IMaterialsInventoryQuantityContract extends IBusinessLogicContract {
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
	BigDecimal getQuantity();
}
