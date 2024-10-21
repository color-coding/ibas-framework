package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTableColumns", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlSeeAlso({ DataTableColumn.class })
public class DataTableColumns extends ArrayList<IDataTableColumn> implements IDataTableColumns {

	private static final long serialVersionUID = -592228599611799067L;

	public DataTableColumns(IDataTable table) {
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
	public IDataTableColumn create() {
		DataTableColumn column = new DataTableColumn();
		if (super.add(column)) {
			column.setName(String.format("col_%s", this.size()));
			return column;
		}
		return null;
	}

	@Override
	public IDataTableColumn create(String name, Class<?> type) {
		IDataTableColumn column = this.create();
		column.setName(name);
		column.setDataType(type);
		return column;
	}

	public IDataTableColumn get(String name) {
		for (int i = 0; i < super.size(); i++) {
			if (super.get(i).getName().equals(name)) {
				return super.get(i);
			}
		}
		return null;
	}

}
