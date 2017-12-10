package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 行 集合
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTableRows", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlSeeAlso({ DataTableRow.class })
public class DataTableRows extends ArrayList<IDataTableRow> implements IDataTableRows {

	private static final long serialVersionUID = -6899298407933961527L;

	private IDataTable table;

	public IDataTable getTable() {
		return table;
	}

	public void setTable(IDataTable table) {
		this.table = table;
	}

	protected DataTableRows(IDataTable table) {
		this.setTable(table);
	}

	/**
	 * 创建行
	 * 
	 * @return
	 */
	public IDataTableRow create() {
		if (this.getTable().getColumns().isEmpty()) {
			throw new RuntimeException(I18N.prop("msg_bobas_data_table_no_columns_defined"));
		}
		DataTableRow row = new DataTableRow(this.getTable().getColumns());
		if (super.add(row)) {

		}
		return row;
	}

}
