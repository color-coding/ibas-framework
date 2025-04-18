package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTableRow", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class DataTableRow extends Serializable implements IDataTableRow {

	private static final long serialVersionUID = -1613267620773820698L;

	public DataTableRow() {
	}

	private IDataTableColumns columns;

	protected IDataTableColumns getColumns() {
		return this.columns;
	}

	void setColumns(IDataTableColumns columns) {
		this.columns = columns;
		if (this.values == null && this.columns != null) {
			this.values = new Object[this.getColumns().size()];
		}
	}

	@XmlElement(name = "Cells", type = String.class, required = true)
	private String[] getValueProxys() {
		String[] values = new String[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			Object value = this.values[i];
			if (value != null) {
				values[i] = Strings.valueOf(value);
			} else {
				values[i] = new String();
			}
		}
		return values;
	}

	@SuppressWarnings("unused")
	private void setValueProxys(String[] values) {
		if (values != null) {
			this.values = new Object[values.length];
			for (int i = 0; i < this.values.length; i++) {
				this.values[i] = values[i];
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

	@Override
	public String toString() {
		return String.format("{row: %s}", this.getColumns().size());
	}
}
