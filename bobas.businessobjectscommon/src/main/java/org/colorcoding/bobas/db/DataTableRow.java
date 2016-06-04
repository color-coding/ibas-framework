package org.colorcoding.bobas.db;

import java.util.Hashtable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DataTableRow")
@XmlRootElement(name = "DataTableRow")
public class DataTableRow implements IDataTableRow {

	// 记录row在DataTable所处的位置，默认第一行
	private int index = -1;
	// 每行中所容纳的列
	private IDataTableColumns columns;
	// 记录每一列所对应的值 ，String 为列名，Object 为列值
	private Hashtable<String, Object> rowMap;

	@XmlElement(name = "Index")
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@XmlElementWrapper(name = "DataTableColumns")
	@XmlElement(name = "DataTableColumn", type = DataTableColumns.class)
	public IDataTableColumns getColumns() {
		return columns;
	}

	public void setColumns(IDataTableColumns columns) {
		this.columns = columns;
	}

	@XmlElement(name = "value")
	public Hashtable<String, Object> getRowMap() {
		return rowMap;
	}

	/**
	 * 无参构造函数
	 */
	public DataTableRow() {
		this.columns = new DataTableColumns();
		this.rowMap = new Hashtable<String, Object>();
	}

	/**
	 * 根据列名返回 查找列中的值
	 * 
	 * @param columnName
	 *            列名
	 * @return 返回结果
	 */
	public Object getValue(String columnName) {
		Object object = null;
		try {
			object = this.rowMap.get(columnName);
		} catch (Exception e) {

		}
		return object;
	}

	/**
	 * 添加一列
	 * 
	 * @param column
	 */
	public void addColumn(DataTableColumn column) {
		if (column == null)
			return;
		this.columns.add(column);
		// 更新列索引
	}

	/**
	 * 根据列标示 查找列中的值
	 * 
	 * @param Index
	 *            列中的索引
	 * @return 返回的结果
	 */
	public Object getValue(int index) {
		DataTableColumns columns = (DataTableColumns) this.columns;
		DataTableColumn column = (DataTableColumn) columns.get(index);
		if (column == null) {
			return null;
		} else {
			return getValue(column.getColumnName());
		}
	}

	/**
	 * 根据列名和值，设置行中某一列所对应的值
	 * 
	 * @param columnName
	 *            列名
	 * @param object
	 *            值
	 */
	public void setValue(String columnName, Object object) {
		if (columnName == null || columnName.isEmpty())
			return;
		boolean isExist = true;// 判断列是否存在
		for (IDataTableColumn dataTableColumn : columns) {
			DataTableColumn column = (DataTableColumn) dataTableColumn;
			if (column.getColumnName().equals(columnName)) {
				isExist = false;
				break;
			}
		}
		if (!isExist)
			return;
		if (object == null)
			this.rowMap.put(columnName, "");
		else

			this.rowMap.put(columnName, object);
	}

	/**
	 * 根据列名和值，设置行中某一列所对应的值
	 * 
	 * @param index
	 *            列索引
	 * @param object
	 *            值
	 */
	public void setValue(int index, Object object) {
		DataTableColumns columns = (DataTableColumns) this.columns;
		DataTableColumn column = (DataTableColumn) columns.get(index);
		if (column == null)
			return;
		setValue(column.getColumnName(), object);
	}
}
