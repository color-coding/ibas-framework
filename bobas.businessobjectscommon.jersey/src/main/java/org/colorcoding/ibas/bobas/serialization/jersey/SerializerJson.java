package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.Serializer;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.structure.Analyzer;
import org.colorcoding.ibas.bobas.serialization.structure.Element;
import org.colorcoding.ibas.bobas.serialization.structure.ElementRoot;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;

public class SerializerJson extends Serializer<JsonSchema> {

	private boolean includeJsonRoot;

	public final boolean isIncludeJsonRoot() {
		return includeJsonRoot;
	}

	public final void setIncludeJsonRoot(boolean includeJsonRoot) {
		this.includeJsonRoot = includeJsonRoot;
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types) {
		try {
			Objects.requireNonNull(object);
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[i];
			}
			ObjectMapper objectMapper = JsonMapper.builder().enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)
					// .enable(SerializationFeature.WRAP_ROOT_VALUE)
					// .enable(SerializationFeature.UNWRAP_ROOT_VALUE)
					.enable(formated ? SerializationFeature.INDENT_OUTPUT : null)
					.serializationInclusion(JsonInclude.Include.NON_NULL)
					.serializationInclusion(JsonInclude.Include.NON_EMPTY).build();

			objectMapper.registerModule(new JaxbAnnotationModule());
			objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

			objectMapper.writeValue(outputStream, object);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public Object deserialize(InputStream ipnInputStream, Class<?>... types) throws SerializationException {
		try {
			ObjectMapper objectMapper = JsonMapper.builder().enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)
					// .enable(SerializationFeature.WRAP_ROOT_VALUE)
					// .enable(SerializationFeature.UNWRAP_ROOT_VALUE)
					.serializationInclusion(JsonInclude.Include.NON_NULL)
					.serializationInclusion(JsonInclude.Include.NON_EMPTY).build();
			if (MyConfiguration.isDebugMode()) {
				// 报错提示jackson位置
				objectMapper.configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature(), true);
			}
			// objectMapper.enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE);
			objectMapper.registerModule(new JaxbAnnotationModule());
			objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

			return objectMapper.readValue(ipnInputStream, types[0]);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public Object deserialize(InputSource inputSource, Class<?>... types) throws SerializationException {
		return this.deserialize(inputSource.getByteStream(), types);
	}

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
		throw new UnsupportedOperationException();
	}

	@Override
	public void validate(JsonSchema schema, InputStream data) throws ValidateException {
		throw new UnsupportedOperationException();
	}

	public void validate(JsonSchema schema, JsonNode data) throws ValidateException {
		throw new UnsupportedOperationException();
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