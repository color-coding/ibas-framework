package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public abstract class FieldDataDbBase<T> extends FieldDataBase<T> implements IFieldDataDb {

	private String dbField = "";

	@Override
	public String getDbField() {
		return this.dbField;
	}

	public void setDbField(String value) {
		this.dbField = value;
	}

	private String dbTable = "";

	@Override
	public String getDbTable() {
		return this.dbTable;
	}

	public void setDbTable(String value) {
		this.dbTable = value;
	}

	private int dbIndex = -1;

	@Override
	public int getDbIndex() {
		return this.dbIndex;
	}

	public void setDbIndex(int value) {
		this.dbIndex = value;
	}

	private DbFieldType fieldType = DbFieldType.db_Unknown;

	@Override
	public DbFieldType getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(DbFieldType value) {
		this.fieldType = value;
	}

	public void mapping(DbField mapping) {
		this.setPrimaryKey(mapping.primaryKey());
		this.setSavable(true);
		this.setOriginal(true);
		this.setDbField(mapping.name());
		this.setDbTable(mapping.table());
		this.setFieldType(mapping.type());
	}
}
