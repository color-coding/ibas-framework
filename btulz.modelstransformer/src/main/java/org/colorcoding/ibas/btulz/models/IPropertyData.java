package org.colorcoding.ibas.btulz.models;

import org.colorcoding.ibas.btulz.data.emDataSubType;
import org.colorcoding.ibas.btulz.data.emDataType;
import org.colorcoding.ibas.btulz.data.emYesNo;

/**
 * 数据属性
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPropertyData extends IProperty {
	/**
	 * 是否主键
	 * 
	 * @return
	 */
	emYesNo isPrimaryKey();

	/**
	 * 设置-主键
	 * 
	 * @param dataType
	 */
	void setPrimaryKey(emYesNo value);

	/**
	 * 是否唯一
	 * 
	 * @return
	 */
	emYesNo isUniqueKey();

	/**
	 * 设置-唯一键
	 * 
	 * @param dataType
	 */
	void setUniqueKey(emYesNo value);

	/**
	 * 获取-数据类型
	 * 
	 * @return
	 */
	emDataType getDataType();

	/**
	 * 设置-数据类型
	 * 
	 * @param dataType
	 */
	void setDataType(emDataType dataType);

	/**
	 * 获取-数据子类型
	 * 
	 * @return
	 */
	emDataSubType getDataSubType();

	/**
	 * 设置-数据子类型
	 * 
	 * @param dataSubType
	 */
	void setDataSubType(emDataSubType dataSubType);

	/**
	 * 获取-数据长度
	 * 
	 * @return
	 */
	int getEditSize();

	/**
	 * 设置-数据长度
	 * 
	 * @param dataType
	 */
	void setEditSize(int editSize);

	/**
	 * 获取-绑定到
	 * 
	 * @return
	 */
	String getMapped();

	/**
	 * 设置-绑定到
	 * 
	 * @param mapped
	 */
	void setMapped(String mapped);
}
