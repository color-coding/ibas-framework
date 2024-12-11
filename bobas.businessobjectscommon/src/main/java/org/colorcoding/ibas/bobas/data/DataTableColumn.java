package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.Serializable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTableColumn", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class DataTableColumn extends Serializable implements IDataTableColumn {

	private static final long serialVersionUID = 1631814339522850980L;

	public DataTableColumn() {
	}

	@XmlElement(name = "Name")
	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "DataType")
	private String getDataTypeProxy() {
		if (this.getDataType() == null) {
			return Object.class.getName();
		}
		return this.getDataType().getName();
	}

	@SuppressWarnings("unused")
	private void setDataTypeProxy(String dataType) {
		try {
			this.setDataType(Class.forName(dataType));
		} catch (Exception e) {
		}
	}

	private Class<?> dataType;

	@Override
	public Class<?> getDataType() {
		return this.dataType;
	}

	@Override
	public void setDataType(Class<?> type) {
		this.dataType = type;
	}

	@XmlElement(name = "Description")
	private String description;

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return String.format("{column: %s}", this.getName() == null ? "unknown" : this.getName());
	}
}
