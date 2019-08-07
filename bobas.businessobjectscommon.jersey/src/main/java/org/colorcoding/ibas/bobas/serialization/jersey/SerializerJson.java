package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.Serializer;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.structure.Analyzer;
import org.colorcoding.ibas.bobas.serialization.structure.Element;
import org.colorcoding.ibas.bobas.serialization.structure.ElementRoot;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * JSON序列化，包含ROOT
 * 
 * @author Niuren.Zhu
 *
 */
public class SerializerJson extends Serializer<JsonSchema> {

	@Override
	public void getSchema(Class<?> type, OutputStream outputStream) throws SerializationException {
		try {
			JsonFactory jsonFactory = new JsonFactory();
			JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream);

			SchemaWriter schemaWriter = new SchemaWriter();
			schemaWriter.jsonGenerator = jsonGenerator;
			schemaWriter.element = new Analyzer().analyse(type);
			schemaWriter.write();

			jsonGenerator.flush();
			jsonGenerator.close();
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public JsonSchema getSchema(Class<?> type) throws SerializationException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			this.getSchema(type, outputStream);
			try (InputStream stream = new ByteArrayInputStream(outputStream.toByteArray())) {
				try (Reader reader = new InputStreamReader(stream)) {
					JsonNode jsonSchema = JsonLoader.fromReader(reader);
					return JsonSchemaFactory.byDefault().getJsonSchema(jsonSchema);
				}
			}
		} catch (IOException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	private JAXBContext context;

	/**
	 * 创建json序列化类
	 * 
	 * @param types 已知类型
	 * @return
	 * @throws JAXBException
	 */
	protected JAXBContext createJAXBContextJson(Class<?>... types) throws JAXBException {
		if (context == null) {
			context = JAXBContextFactory.createContext(types, null);
		}
		return context;
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types) {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[i];
			}
			JAXBContext context = createJAXBContextJson(knownTypes);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, outputStream);
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public Object deserialize(InputSource inputSource, Class<?>... types) throws SerializationException {
		try {
			JAXBContext context = createJAXBContextJson(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			return unmarshaller.unmarshal(inputSource);
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public void validate(JsonSchema schema, InputStream data) throws ValidateException {
		try (Reader reader = new InputStreamReader(data)) {
			JsonNode jsonData = JsonLoader.fromReader(reader);
			this.validate(schema, jsonData);
		} catch (IOException e) {
			throw new ValidateException(e);
		}
	}

	public void validate(JsonSchema schema, JsonNode data) throws ValidateException {
		try {
			ProcessingReport report = schema.validate(data);
			if (!report.isSuccess()) {
				throw new ValidateException(report.toString());
			}
		} catch (ValidateException e) {
			throw e;
		} catch (ProcessingException e) {
			throw new ValidateException(e);
		}
	}

}

class SchemaWriter {

	public static final String SCHEMA_VERSION = "http://json-schema.org/schema#";

	public SchemaWriter() {
		this.knownTypes = new HashMap<>();
		this.knownTypes.put("integer", "integer");
		this.knownTypes.put("long", "integer");
		this.knownTypes.put("short", "integer");
		this.knownTypes.put("float", "number");
		this.knownTypes.put("double", "number");
		this.knownTypes.put("boolean", "boolean");
		this.knownTypes.put("java.lang.Integer", "integer");
		this.knownTypes.put("java.lang.Long", "integer");
		this.knownTypes.put("java.lang.Short", "integer");
		this.knownTypes.put("java.math.BigInteger", "integer");
		this.knownTypes.put("java.lang.Float", "number");
		this.knownTypes.put("java.lang.Double", "number");
		this.knownTypes.put("java.math.BigDecimal", "number");
		this.knownTypes.put("java.lang.Boolean", "boolean");
		this.knownTypes.put("java.lang.String", "string");
		this.knownTypes.put("java.lang.Character", "string");
		this.knownTypes.put("java.util.Date", "string");
		this.knownTypes.put("org.colorcoding.ibas.bobas.data.Decimal", "number");
	}

	public JsonGenerator jsonGenerator;
	public ElementRoot element;
	protected Map<String, String> knownTypes;

	public void write() throws JsonGenerationException, IOException {
		this.jsonGenerator.writeStartObject();
		this.jsonGenerator.writeStringField("$schema", SCHEMA_VERSION);
		this.jsonGenerator.writeStringField("type", "object");
		this.jsonGenerator.writeFieldName("properties");
		this.jsonGenerator.writeStartObject();
		this.jsonGenerator.writeFieldName(this.element.getName());
		this.jsonGenerator.writeStartObject();
		this.jsonGenerator.writeStringField("type", "object");
		this.jsonGenerator.writeFieldName("properties");
		this.jsonGenerator.writeStartObject();
		for (Element item : this.element.getChilds()) {
			this.write(this.jsonGenerator, item);
		}
		this.jsonGenerator.writeEndObject();
		this.jsonGenerator.writeEndObject();
		this.jsonGenerator.writeEndObject();
		this.jsonGenerator.writeEndObject();
	}

	protected void write(JsonGenerator jsonGenerator, Element element) throws JsonGenerationException, IOException {
		if (element.isCollection()) {
			jsonGenerator.writeFieldName(element.getWrapper());
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("type", "array");
			jsonGenerator.writeFieldName("items");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("type", "object");
			jsonGenerator.writeFieldName("properties");
			jsonGenerator.writeStartObject();
			for (Element item : element.getChilds()) {
				this.write(jsonGenerator, item);
			}
			jsonGenerator.writeEndObject();
			jsonGenerator.writeEndObject();
			jsonGenerator.writeEndObject();
		} else {
			jsonGenerator.writeFieldName(element.getName());
			jsonGenerator.writeStartObject();
			String typeName = this.knownTypes.get(element.getType().getName());
			if (typeName != null) {
				// 已知类型
				jsonGenerator.writeStringField("type", typeName);
			} else if (element.getType().isEnum()) {
				// 枚举类型
				jsonGenerator.writeStringField("type", "string");
				jsonGenerator.writeArrayFieldStart("enum");
				for (Object enumItem : element.getType().getEnumConstants()) {
					if (enumItem instanceof Enum<?>) {
						// 枚举值（比对枚举索引）
						Enum<?> itemValue = (Enum<?>) enumItem;
						jsonGenerator.writeString(itemValue.name());
					}
				}
				jsonGenerator.writeEndArray();
			} else if (element.getType().equals(DateTime.class)) {
				// 日期类型
				jsonGenerator.writeStringField("type", "string");
				// 格式：2000-01-01 or 2000-01-01T00:00:00
				jsonGenerator.writeStringField("pattern",
						"^|[0-9]{4}-[0-1][0-9]-[0-3][0-9]|[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-6][0-9]:[0-6][0-9]$");
			} else {
				jsonGenerator.writeStringField("type", "object");
				jsonGenerator.writeFieldName("properties");
				jsonGenerator.writeStartObject();
				for (Element item : element.getChilds()) {
					this.write(jsonGenerator, item);
				}
				jsonGenerator.writeEndObject();
			}
			jsonGenerator.writeEndObject();
		}
	}
}