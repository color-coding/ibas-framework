package org.colorcoding.ibas.bobas.common;

import java.util.List;

import org.colorcoding.ibas.bobas.data.KeyValue;

/**
 * 存储过程
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISqlStoredProcedure {
	/**
	 * 获取-存储过程名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 设置-存储过程名称
	 * 
	 * @return
	 */
	void setName(String value);

	/**
	 * 获取-存储过程调用参数
	 * 
	 * @return
	 */
	List<KeyValue> getParameters();

	/**
	 * 添加存储过程参数
	 * 
	 * @param name
	 *            名称
	 * @param value
	 *            值
	 */
	void addParameters(String name, Object value);

}
