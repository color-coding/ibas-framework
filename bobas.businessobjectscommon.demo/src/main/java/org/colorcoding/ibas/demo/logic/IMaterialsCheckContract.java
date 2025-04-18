package org.colorcoding.ibas.demo.logic;

import org.colorcoding.ibas.bobas.logic.IBusinessLogicContract;

/**
 * 物料检查契约
 * 
 * @author Niuren.Zhu
 *
 */
public interface IMaterialsCheckContract extends IBusinessLogicContract {
	/**
	 * 物料编码
	 * 
	 * @return
	 */
	String getItemCode();

	/**
	 * 获取-物料/服务描述
	 * 
	 * @return 值
	 */
	String getItemDescription();

	/**
	 * 设置-物料/服务描述
	 * 
	 * @param value 值
	 */
	void setItemDescription(String value);

}
