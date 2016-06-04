package org.colorcoding.bobas.db;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DataTable")
@XmlRootElement(name = "DataTable")
public class DataTable implements IDataTable {
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 表-行
	 */
	private DataTableRows rows;
	/**
	 * 表-列
	 */
	private DataTableColumns columns;

	@XmlElement(name = "TableName")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@XmlElementWrapper(name = "DataTableRows")
	@XmlElement(name = "DataTableRow", type = DataTableRows.class)
	public DataTableRows getRows() {
		if (this.rows == null) {
			this.rows = new DataTableRows();
		}
		return rows;
	}

	public void setRows(DataTableRows rows) {
		this.rows = rows;
	}

	@XmlTransient
	public IDataTableColumns getColumns() {
		if (this.columns == null) {
			this.columns = new DataTableColumns();
		}
		return columns;
	}

	public void setColumns(DataTableColumns columns) {
		this.columns = columns;
	}

	/**
	 * 带参数构造函数
	 * 
	 * @param tableName
	 *            表名
	 */
	public DataTable(String tableName) {
		this.tableName = tableName;
		this.rows = new DataTableRows();
		this.columns = new DataTableColumns();
	}

	/**
	 * 无参构造函数
	 */
	public DataTable() {
		this("default");
	}

	public int getTotalCount() {
		return rows.size();
	}

	/**
	 * 获取某行某列的值
	 * 
	 * @param row
	 *            行索引
	 * @param colName
	 *            列名
	 * @return 返回值
	 */
	public Object getValue(int row, String colName) {

		return this.rows.get(row).getValue(colName);
	}

	/**
	 * 获取某行某列的值
	 * 
	 * @param row
	 *            行索引
	 * @param col
	 *            列索引
	 * @return 返回值
	 */
	public Object getValue(int row, int col) {
		return this.rows.get(row).getValue(col);
	}

	/**
	 * 设置某行某一列的值
	 * 
	 * @param row
	 *            行索引
	 * @param col
	 *            列索引
	 * @param value
	 *            值
	 */
	public void setValue(int row, int col, Object value) {
		this.rows.get(row).setValue(col, value);
	}

	/**
	 * 设置某行某一列的值
	 * 
	 * @param row
	 *            行索引
	 * @param colName
	 *            列名
	 * @param value
	 *            值
	 */
	public void setValue(int row, String colName, Object value) {
		this.rows.get(row).setValue(colName, value);
	}

	/**
	 * 更加列名升序排列
	 * 
	 * @param columnName
	 *            列名
	 * @return 排序后的结果
	 */
	public DataTable sort(final String columnName) {
		// 判断列是否可以转为数字比较，否则转为字符串比较
		DataTableColumn column = this.columns.geDataTableColumn(columnName);
		List<IDataTableRow> list = this.rows;
		if (column != null) {
			switch (column.getColumnType()) {
			case db_Decimal:
			case db_Numeric:
				// 将列转换为数字比较 排序
				Collections.sort(list, new Comparator<IDataTableRow>() {
					@Override
					public int compare(IDataTableRow row1, IDataTableRow row2) {
						DataTableRow r1 = (DataTableRow) row1;
						DataTableRow r2 = (DataTableRow) row2;
						return ((Integer) r1.getValue(columnName)).intValue()
								- ((Integer) r2.getValue(columnName)).intValue();
					}
				});
				break;
			case db_Alphanumeric:
			case db_Memo:
			case db_Date:
			case db_Bytes:
				Collections.sort(list, new Comparator<IDataTableRow>() {
					@Override
					public int compare(IDataTableRow row1, IDataTableRow row2) {
						DataTableRow r1 = (DataTableRow) row1;
						DataTableRow r2 = (DataTableRow) row2;
						return r1.getValue(columnName).toString().compareTo(r2.getValue(columnName).toString());
					}
				});
				break;
			default:
				break;
			}
		}
		// 通过列名获取 列中的数据
		return this;

	}

	/**
	 * 更加列名降序排列
	 * 
	 * @param columnName
	 *            列名
	 * @return 排序后的结果
	 */
	public DataTable reverse(final String columnName) {
		// 判断列是否可以转为数字比较，否则转为字符串比较
		DataTableColumn column = this.columns.geDataTableColumn(columnName);
		List<IDataTableRow> list = this.rows;
		if (column != null) {
			switch (column.getColumnType()) {
			case db_Decimal:
			case db_Numeric:
				// 将列转换为数字比较 排序
				Collections.sort(list, new Comparator<IDataTableRow>() {
					@Override
					public int compare(IDataTableRow row1, IDataTableRow row2) {
						DataTableRow r1 = (DataTableRow) row1;
						DataTableRow r2 = (DataTableRow) row2;
						return ((Integer) r2.getValue(columnName)).intValue()
								- ((Integer) r1.getValue(columnName)).intValue();
					}
				});
				break;
			case db_Alphanumeric:
			case db_Memo:
			case db_Date:
			case db_Bytes:
				Collections.sort(list, new Comparator<IDataTableRow>() {
					@Override
					public int compare(IDataTableRow row1, IDataTableRow row2) {
						DataTableRow r1 = (DataTableRow) row1;
						DataTableRow r2 = (DataTableRow) row2;
						return r2.getValue(columnName).toString().compareTo(r1.getValue(columnName).toString());
					}
				});
				break;
			default:
				break;
			}

		}
		// 通过列名获取 列中的数据
		return this;
	}

}
