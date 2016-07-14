package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public interface IFieldDataDb extends IFieldData {
	/**
	 * 数据库字段名称
	 */
	String getDbField();

	/**
	 * 数据库表名称
	 */
	String getDbTable();

	/**
	 * 数据库字段索引
	 */
	int getDbIndex();

	/**
	 * 字段类型
	 * 
	 * @return
	 */
	DbFieldType getFieldType();
}
