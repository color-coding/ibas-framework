package org.colorcoding.bobas.db;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DataTableColumns")
@XmlRootElement(name = "DataTableColumns")
@XmlSeeAlso({ DataTableColumn.class })
public class DataTableColumns extends ArrayList<IDataTableColumn> implements IDataTableColumns {

	/**
	 * 
	 */
	private static final long serialVersionUID = -592228599611799067L;

	/**
	 * 通过 列名查找列
	 * 
	 * @param name
	 *            列名
	 * @return 返回查找的列
	 */
	public DataTableColumn geDataTableColumn(String name) {
		DataTableColumn column = null;
		for (int i = 0; i < super.size(); i++) {
			DataTableColumn dataTableColumn = (DataTableColumn) super.get(i);
			if (dataTableColumn.getColumnName().equals(name)) {
				column = dataTableColumn;
			}
		}
		return column;

	}

	/**
	 * 创建一列
	 * 
	 * @return
	 */
	public IDataTableColumn create() {
		DataTableColumn column = new DataTableColumn();
		super.add(column);
		return column;
	}

}
