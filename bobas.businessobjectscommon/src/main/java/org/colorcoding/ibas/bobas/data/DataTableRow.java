package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
		// 反序列化后：列定义已就绪，将字符串值转换为目标类型
		this.convertValues();
	}

	/**
	 * 将values中的字符串值按列类型转换为目标类型（修复反序列化类型丢失问题）
	 */
	private void convertValues() {
		if (this.values == null || this.columns == null) {
			return;
		}
		for (int i = 0; i < this.values.length && i < this.columns.size(); i++) {
			Object value = this.values[i];
			if (value == null) {
				continue;
			}
			IDataTableColumn column = this.columns.get(i);
			if (column == null || column.getDataType() == null) {
				continue;
			}
			// 字符串值且目标类型非String时，执行类型转换
			if (value.getClass() == String.class && column.getDataType() != String.class) {
				try {
					this.values[i] = DataConvert.convert(column.getDataType(), value);
				} catch (Exception e) {
					// 转换失败保留原值
				}
			}
			// 空字符串转数值类型时设为null
			if (value.getClass() == String.class && Strings.isNullOrEmpty((String) value)
					&& Number.class.isAssignableFrom(column.getDataType())) {
				this.values[i] = null;
			}
		}
	}

	@XmlElementWrapper(name = "Cells")
	@XmlElement(name = "Cell", type = String.class, required = true)
	private String[] getValueProxies() {
		String[] values = new String[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			Object value = this.values[i];
			if (value != null) {
				values[i] = Strings.valueOf(value);
			} else {
				values[i] = Strings.VALUE_EMPTY;
			}
		}
		return values;
	}

	@SuppressWarnings("unused")
	private void setValueProxies(String[] values) {
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
		if (column != null && column.getDataType() != null
				&& column.getDataType() != String.class && value instanceof String) {
			// 使用DataConvert进行类型转换（修复Class.cast不执行实际转换的Bug）
			try {
				value = DataConvert.convert(column.getDataType(), value);
			} catch (Exception e) {
				// 转换失败保留原值
			}
		}
		this.values[col] = value;
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
		// 引用比较，直接遍历（通常列数较少，无需额外优化）
		for (int i = 0; i < this.getColumns().size(); i++) {
			if (this.getColumns().get(i) == col) {
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
