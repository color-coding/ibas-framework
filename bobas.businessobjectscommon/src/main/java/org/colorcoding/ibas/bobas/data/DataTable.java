package org.colorcoding.ibas.bobas.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

	public DataTable() {
		this.columns = new DataTableColumns(this);
		this.rows = new DataTableRows(this);
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

	@XmlElementWrapper(name = "Columns")
	@XmlElement(name = "Column", type = DataTableColumn.class, required = true)
	private IDataTableColumns columns;

	@Override
	public IDataTableColumns getColumns() {
		if (this.columns == null) {
			this.columns = new DataTableColumns(this);
		}
		return this.columns;
	}

	@XmlElementWrapper(name = "Rows")
	@XmlElement(name = "Row", type = DataTableRow.class, required = true)
	private IDataTableRows rows;

	@Override
	public IDataTableRows getRows() {
		if (this.rows == null) {
			this.rows = new DataTableRows(this);
		}
		return this.rows;
	}

	@Override
	public String toString() {
		return String.format("{table: %s}", this.getName() == null ? "unknown" : this.getName());
	}

	@Override
	public String toString(String type) {
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			ISerializer<?> serializer = SerializerFactory.create().createManager().create(type);
			serializer.serialize(this, writer);
			return writer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
