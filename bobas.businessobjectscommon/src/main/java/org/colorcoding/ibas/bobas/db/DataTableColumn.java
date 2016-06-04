package org.colorcoding.ibas.bobas.db;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.mapping.db.DbFieldType;

@XmlType(name = "DataTableColumn")
@XmlRootElement(name = "DataTableColumn")
public class DataTableColumn implements IDataTableColumn {
	/**
	 * 列名
	 */
	private String columnName;
	/**
	 * 索引
	 */
	private int index;
	/**
	 * 类型
	 */
	private DbFieldType columnType;

	@XmlElement(name = "ColumnName")
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@XmlElement(name = "Index")
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@XmlElement(name = "ColumnType")
	public DbFieldType getColumnType() {
		return columnType;
	}

	public void setColumnType(DbFieldType columnType) {
		this.columnType = columnType;
	}

	public DataTableColumn() {
		this("default");
	}

	public DataTableColumn(String columnName) {
		this(columnName, DbFieldType.db_Unknown);
	}

	public DataTableColumn(String columnName, DbFieldType columnType) {
		this.columnName = columnName;
		this.columnType = columnType;
	}

}
