package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.db.DataType;

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
	 * 字段类型
	 *
	 * @return 数据库字段类型
	 */
	DataType getFieldType();
}
