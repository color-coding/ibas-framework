package org.colorcoding.ibas.bobas.bo;

import java.io.Serializable;

import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 用户字段
 * 
 * @author Niuren.Zhu
 *
 */
public interface IUserField extends Serializable {
	/**
	 * 获取-名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取-类型
	 * 
	 * @return
	 */
	DbFieldType getValueType();

	/**
	 * 获取-值
	 * 
	 * @return
	 */
	Object getValue();

	/**
	 * 设置-值
	 * 
	 * @return true,改变;false,未改变
	 */
	boolean setValue(Object value);

}
