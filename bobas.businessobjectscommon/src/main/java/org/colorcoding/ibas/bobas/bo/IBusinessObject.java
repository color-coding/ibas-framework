package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

public interface IBusinessObject extends IBusinessObjectBase {

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
}
