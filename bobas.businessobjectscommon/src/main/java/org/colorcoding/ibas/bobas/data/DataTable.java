package org.colorcoding.ibas.bobas.data;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.Serializable;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTable", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "DataTable", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class DataTable extends Serializable implements IDataTable {

	private static final long serialVersionUID = -5646933951739886585L;

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

	private IDataTableColumns columns;

	@XmlElementWrapper(name = "Columns")
	@XmlElement(name = "Column", type = DataTableColumn.class, required = true)
	@Override
	public IDataTableColumns getColumns() {
		if (this.columns == null) {
			this.columns = new DataTableColumns(this);
		}
		return this.columns;
	}

	@Override
	public void setColumns(IDataTableColumns columns) {
		this.columns = columns;
	}

	private IDataTableRows rows;

	@XmlElementWrapper(name = "Rows")
	@XmlElement(name = "Row", type = DataTableRow.class, required = true)
	@Override
	public IDataTableRows getRows() {
		if (this.rows == null) {
			this.rows = new DataTableRows(this);
		}
		return this.rows;
	}

	@Override
	public void setRows(IDataTableRows rows) {
		this.rows = rows;
	}

	@Override
	public String toString(String type) {
		ISerializer<?> serializer = SerializerFactory.create().createManager().create(type);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(this, writer);
		return writer.toString();
	}

}
