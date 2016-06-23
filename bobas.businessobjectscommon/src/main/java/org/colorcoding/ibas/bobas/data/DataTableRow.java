package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.i18n.i18n;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTableRow", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class DataTableRow implements IDataTableRow {
	public DataTableRow() {
	}

	public DataTableRow(IDataTableColumns columns) {
		this.setColumns(columns);
		this.values = new Object[this.getColumns().size()];
	}

	private IDataTableColumns columns;

	public IDataTableColumns getColumns() {
		return this.columns;
	}

	public void setColumns(IDataTableColumns columns) {
		this.columns = columns;
	}

	@XmlElement(name = "Cell", type = String.class, required = true)
	private String[] getValueProxys() {
		String[] values = new String[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			Object value = this.values[i];
			if (value != null) {
				values[i] = value.toString();
			}
		}
		return values;
	}

	@SuppressWarnings("unused")
	private void getValueProxys(String[] values) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				this.setValue(i, values[i]);
			}
		}
	}

	// @XmlElement(name = "Cell")
	private Object[] values;

	@Override
	public Object getValue(int col) {
		return this.values[col];
	}

	@Override
	public void setValue(int col, Object value) {
		IDataTableColumn column = this.getColumns().get(col);
		if (!column.getDataType().isInstance(value) && value != null) {
			// 值与列定义的类型不符
			throw new RuntimeException(i18n.prop("msg_bobas_data_table_data_type_was_not_column_definition"));
		}
		if (column.getDataType() != String.class && String.class.isInstance(value)) {
			this.values[col] = column.getDataType().cast(value);
		} else {
			this.values[col] = value;
		}
	}

	private int getColumnIndex(String col) {
		for (int i = 0; i < this.getColumns().size(); i++) {
			IDataTableColumn column = this.getColumns().get(i);
			if (column.getName().equals(col)) {
				return i;
			}
		}
		return -1;
	}

	private int getColumnIndex(IDataTableColumn col) {
		for (int i = 0; i < this.getColumns().size(); i++) {
			IDataTableColumn column = this.getColumns().get(i);
			if (column == col) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object getValue(String col) {
		return this.getValue(this.getColumnIndex(col));
	}

	@Override
	public void setValue(String col, Object value) {
		this.setValue(this.getColumnIndex(col), value);
	}

	@Override
	public Object getValue(IDataTableColumn col) {
		return this.getValue(this.getColumnIndex(col));
	}

	@Override
	public void setValue(IDataTableColumn col, Object value) {
		this.setValue(this.getColumnIndex(col), value);
	}

}
