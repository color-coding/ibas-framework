package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

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

	private IDataTable table;

	protected IDataTable getTable() {
		return table;
	}

	private void setTable(IDataTable table) {
		this.table = table;
	}

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
		if (done) {
			if (item instanceof DataTableRow) {
				DataTableRow row = (DataTableRow) item;
				row.setColumns(this.getTable().getColumns());
			}
		}
		return done;
	}
}
