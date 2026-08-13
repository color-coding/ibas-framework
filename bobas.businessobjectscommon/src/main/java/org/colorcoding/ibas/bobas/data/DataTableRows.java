package org.colorcoding.ibas.bobas.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 行 集合
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ DataTableRow.class })
@XmlType(name = "DataTableRows", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class DataTableRows extends ArrayList<IDataTableRow> implements IDataTableRows {

	private static final long serialVersionUID = -6899298407933961527L;

	public DataTableRows(IDataTable table) {
		this.setTable(table);
	}

	protected DataTableRows() {
	}

	private IDataTable table;

	protected IDataTable getTable() {
		return table;
	}

	private void setTable(IDataTable table) {
		this.table = table;
	}

	/**
	 * 创建并添加新行，添加失败时返回null
	 */
	@Override
	public IDataTableRow create() {
		DataTableRow row = new DataTableRow();
		if (this.add(row)) {
			return row;
		}
		return null;
	}

	@Override
	public boolean add(IDataTableRow item) {
		boolean done = super.add(item);
		if (done && item instanceof DataTableRow) {
			DataTableRow row = (DataTableRow) item;
			row.setColumns(this.getTable().getColumns());
		}
		return done;
	}

	@Override
	public void ensureCapacity(int capacity) {
		((java.util.ArrayList<?>) this).ensureCapacity(capacity);
	}
}
