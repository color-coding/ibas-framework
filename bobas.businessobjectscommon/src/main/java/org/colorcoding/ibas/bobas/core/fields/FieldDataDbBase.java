package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public abstract class FieldDataDbBase<T> extends FieldDataBase<T> implements IFieldDataDb {

	private String dbField;

	@Override
	public String getDbField() {
		if (this.fieldMapping != null) {
			return this.fieldMapping.name();
		}
		return this.dbField;
	}

	public void setDbField(String value) {
		this.dbField = value;
	}

	private String dbTable;

	@Override
	public String getDbTable() {
		if (this.fieldMapping != null) {
			return MyConfiguration.applyVariables(this.fieldMapping.table());
		}
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

	private DbFieldType fieldType;

	@Override
	public DbFieldType getFieldType() {
		if (this.fieldMapping != null) {
			return this.fieldMapping.type();
		}
		return this.fieldType;
	}

	public void setFieldType(DbFieldType value) {
		this.fieldType = value;
	}

	private DbField fieldMapping;

	public void mapping(DbField mapping) {
		this.setPrimaryKey(mapping.primaryKey());
		this.setUniqueKey(mapping.uniqueKey());
		this.setSavable(true);
		this.setOriginal(true);
		this.fieldMapping = mapping;
	}
}
