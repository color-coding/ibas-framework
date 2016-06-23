package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTableColumn", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class DataTableColumn implements IDataTableColumn {
	public DataTableColumn() {
		
	}
	
	private String name;

	@XmlElement(name = "Name")
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private Class<?> dataType;

	@XmlElement(name = "DataType")
	@Override
	public Class<?> getDataType() {
		return this.dataType;
	}

	@Override
	public void setDataType(Class<?> type) {
		this.dataType = type;
	}
	private String description;

	@XmlElement(name = "Description")
	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
}
