package org.colorcoding.bobas.db;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.util.ArrayList;

/**
 * 行 集合
 */
@XmlType(name = "DataTableRows")
@XmlRootElement(name = "DataTableRows")
@XmlSeeAlso({ DataTableRow.class })
public class DataTableRows extends ArrayList<IDataTableRow> implements IDataTableRows {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6899298407933961527L;

	/**
	 * 创建行
	 * 
	 * @return
	 */
	public IDataTableRow create() {
		DataTableRow row = new DataTableRow();
		super.add(row);
		return row;
	}

	@Override
	public DataTableRow get(int index) {
		return (DataTableRow) super.get(index);
	}

	public void addColumn(IDataTableColumn column) {
		if (column == null)
			return;
		for (int i = 0; i < super.size(); i++) {
			this.get(i).addColumn((DataTableColumn) column);
		}
	}

	public void addColumns(IDataTableColumns columns) {
		if (columns == null)
			return;
		for (IDataTableColumn dataTableColumn : columns) {
			this.addColumn(dataTableColumn);
		}
	}

}
