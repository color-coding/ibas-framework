package org.colorcoding.ibas.bobas.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.writer.CsvWriter;
import org.colorcoding.ibas.bobas.serialization.writer.JsonWriter;
import org.colorcoding.ibas.bobas.serialization.writer.Writer;
import org.colorcoding.ibas.bobas.serialization.writer.XmlWriter;

/**
 * DataTable专用JSON序列化器（仅支持序列化，不支持反序列化）
 */
class DataTableSerializerJson extends DataTableSerializer {

	public final static String TYPE_SIGN = SerializationFactory.TYPE_JSON;

	@Override
	protected Writer createWriter() {
		return new JsonWriter() {

			@Override
			public void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException {
				if (DataTable.class.isAssignableFrom(objectType) || IDataTable.class.isAssignableFrom(objectType)) {
					super.writeObjectStart(outputStream, objectType);
				} else {
					outputStream.write(CHAR_BYTES['{']);
				}
			}

			@Override
			public void writePropertyStart(OutputStream outputStream, String property, boolean isArray)
					throws IOException {
				if (!isArray && Strings.equals(property, "Cell")) {
					return;
				}
				super.writePropertyStart(outputStream, property, isArray);
			}

			@Override
			public void writePropertyEnd(OutputStream outputStream, String property, boolean isArray)
					throws IOException {
				if (!isArray && Strings.equals(property, "Cell")) {
					return;
				}
				super.writePropertyEnd(outputStream, property, isArray);
			}

		};
	}
}

/**
 * DataTable专用XML序列化器（仅支持序列化，不支持反序列化）
 */
class DataTableSerializerXml extends DataTableSerializer {

	public final static String TYPE_SIGN = SerializationFactory.TYPE_XML;

	@Override
	protected Writer createWriter() {
		return new XmlWriter() {

			@Override
			public void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException {
				if (DataTableColumn.class.isAssignableFrom(objectType)
						|| IDataTableColumn.class.isAssignableFrom(objectType)) {
					outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
					this.write(outputStream, "Column");
					outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
				} else if (DataTableRow.class.isAssignableFrom(objectType)
						|| IDataTableRow.class.isAssignableFrom(objectType)) {
					outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
					this.write(outputStream, "Row");
					outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
				} else {
					super.writeObjectStart(outputStream, objectType);
				}
			}

			@Override
			public void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException {
				if (DataTableColumn.class.isAssignableFrom(objectType)
						|| IDataTableColumn.class.isAssignableFrom(objectType)) {
					outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
					outputStream.write(CHAR_BYTES['/']);
					this.write(outputStream, "Column");
					outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
				} else if (DataTableRow.class.isAssignableFrom(objectType)
						|| IDataTableRow.class.isAssignableFrom(objectType)) {
					outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
					outputStream.write(CHAR_BYTES['/']);
					this.write(outputStream, "Row");
					outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
				} else {
					super.writeObjectEnd(outputStream, objectType);
				}
			}

		};
	}
}

/**
 * DataTable专用CSV序列化器（仅支持序列化，不支持反序列化）
 */
class DataTableSerializerCsv extends DataTableSerializer {

	public final static String TYPE_SIGN = "csv";

	@Override
	protected Writer createWriter() {
		return new CsvWriter();
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types)
			 {
		if (!(object instanceof IDataTable)) {
			throw new SerializationException(I18N.prop("msg_bobas_data_type_not_support",
					object != null ? object.getClass().getSimpleName() : "NULL"));
		}
		try {
			this.serialize((IDataTable) object, outputStream, formated);
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	protected void serialize(IDataTable dataTable, OutputStream outputStream, boolean formated) throws Exception {
		if (dataTable.getColumns().isEmpty()) {
			return;
		}
		Writer writer = this.createWriter();

		IDataTableColumn column;
		for (int i = 0; i < dataTable.getColumns().size(); i++) {
			column = dataTable.getColumns().get(i);
			if (i > 0) {
				writer.writeDelimiter(outputStream);
			}
			writer.write(outputStream, column.getName());
		}
		writer.writeNewLine(outputStream);

		IDataTableRow row;
		for (int i = 0; i < dataTable.getRows().size(); i++) {
			row = dataTable.getRows().get(i);
			if (i > 0) {
				writer.writeNewLine(outputStream);
			}
			for (int j = 0; j < dataTable.getColumns().size(); j++) {
				if (j > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.write(outputStream, Strings.valueOf(row.getValue(j)));
			}
		}
	}
}

/**
 * DataTable序列化器基类（仅支持序列化，clone/deserialize/schema/validate均未实现）
 */
abstract class DataTableSerializer implements ISerializer {

	protected abstract Writer createWriter();

	private Writer writer;

	public Writer getWriter() {
		if (this.writer == null) {
			this.writer = this.createWriter();
		}
		return writer;
	}

	@Override
	public <T> T clone(T object, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "clone"));
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, Class<?>... types)  {
		this.serialize(object, outputStream, false, types);
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types)
			 {
		if (!(object instanceof IDataTable)) {
			throw new SerializationException(I18N.prop("msg_bobas_data_type_not_support",
					object != null ? object.getClass().getSimpleName() : "NULL"));
		}
		try {
			this.getWriter().writeHeader(outputStream);
			this.serialize((IDataTable) object, outputStream, formated);
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	public void schema(Class<?> type, OutputStream outputStream)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "schema"));
	}

	@Override
	public void validate(Class<?> type, String data) throws ValidateException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "validate(String)"));
	}

	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "validate(InputStream)"));
	}

	@Override
	public <T> T deserialize(String data, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "deserialize(String)"));
	}

	@Override
	public <T> T deserialize(InputStream inputStream, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "deserialize(InputStream)"));
	}

	@Override
	public <T> T deserialize(Reader reader, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "deserialize(Reader)"));
	}

	protected void serialize(IDataTable dataTable, OutputStream outputStream, boolean formated) throws Exception {
		if (dataTable.getColumns().isEmpty()) {
			return;
		}
		Writer writer = this.createWriter();
		writer.writeObjectStart(outputStream, dataTable.getClass());

		IDataTableColumn column;
		writer.writePropertyStart(outputStream, "Columns", true);
		for (int i = 0; i < dataTable.getColumns().size(); i++) {
			column = dataTable.getColumns().get(i);
			if (i > 0) {
				writer.writeDelimiter(outputStream);
			}
			writer.writeObjectStart(outputStream, column.getClass());
			writer.writePropertyStart(outputStream, "Name", false);
			writer.writePropertyValue(outputStream, column.getName());
			writer.writePropertyEnd(outputStream, "Name", false);
			writer.writeDelimiter(outputStream);
			writer.writePropertyStart(outputStream, "DataType", false);
			writer.writePropertyValue(outputStream, column.getDataType().getName());
			writer.writePropertyEnd(outputStream, "DataType", false);
			writer.writeObjectEnd(outputStream, column.getClass());
		}
		writer.writePropertyEnd(outputStream, "Columns", true);

		IDataTableRow row;
		writer.writeDelimiter(outputStream);
		writer.writePropertyStart(outputStream, "Rows", true);
		for (int i = 0; i < dataTable.getRows().size(); i++) {
			row = dataTable.getRows().get(i);
			if (i > 0) {
				writer.writeDelimiter(outputStream);
			}
			writer.writeObjectStart(outputStream, row.getClass());
			writer.writePropertyStart(outputStream, "Cells", true);
			for (int j = 0; j < dataTable.getColumns().size(); j++) {
				if (j > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.writePropertyStart(outputStream, "Cell", false);
				writer.writePropertyValue(outputStream, Strings.valueOf(row.getValue(j)));
				writer.writePropertyEnd(outputStream, "Cell", false);
			}
			writer.writePropertyEnd(outputStream, "Cells", true);
			writer.writeObjectEnd(outputStream, row.getClass());
		}
		writer.writePropertyEnd(outputStream, "Rows", true);

		writer.writeObjectEnd(outputStream, dataTable.getClass());
	}

}
