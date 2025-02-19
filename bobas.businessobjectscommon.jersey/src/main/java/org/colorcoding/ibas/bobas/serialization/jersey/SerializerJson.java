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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class SerializerJson extends Serializer {

	private boolean includeJsonRoot;

	public final boolean isIncludeJsonRoot() {
		return includeJsonRoot;
	}

	public final void setIncludeJsonRoot(boolean includeJsonRoot) {
		this.includeJsonRoot = includeJsonRoot;
	}

	private boolean orderProperties;

	protected final boolean isOrderProperties() {
		return orderProperties;
	}

	protected final void setOrderProperties(boolean orderProperties) {
		this.orderProperties = orderProperties;
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
			ObjectMapper objectMapper = JsonMapper.builder()
					// 数组外属性名称
					.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)
					// 含头对象名称
					.configure(SerializationFeature.WRAP_ROOT_VALUE, this.isIncludeJsonRoot())
					// 属性排序
					.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, this.isOrderProperties())
					// 格式化输出
					.configure(SerializationFeature.INDENT_OUTPUT, formated)
					// 忽略空值
					.serializationInclusion(JsonInclude.Include.NON_NULL)
					.serializationInclusion(JsonInclude.Include.NON_EMPTY)
					// 构建
					.build();
			// 注册自定义内容
			objectMapper.registerModule(new SimpleModule().setSerializerModifier(new AllTypesSerializerModifier()));
			// 注册对JAXB注解支持
			objectMapper.registerModule(new JaxbAnnotationModule());

			objectMapper.writeValue(outputStream, object);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputStream ipnInputStream, Class<?>... types) throws SerializationException {
		try {
			ObjectMapper objectMapper = JsonMapper.builder()
					// 数组外属性名称
					.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)
					// 含头对象名称
					.configure(SerializationFeature.WRAP_ROOT_VALUE, this.isIncludeJsonRoot())
					// 属性排序
					.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, this.isOrderProperties())
					// 日期判断时区
					.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
					// 不存在属性报错
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					// 构建
					.build();

			if (MyConfiguration.isDebugMode()) {
				// 报错提示jackson位置
				objectMapper.configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature(), true);
			}

			// 注册对JAXB注解支持
			objectMapper.registerModule(new JaxbAnnotationModule());

			return (T) objectMapper.readValue(ipnInputStream, types[0]);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public <T> T deserialize(InputSource inputSource, Class<?>... types) throws SerializationException {
		return this.deserialize(inputSource.getByteStream(), types);
	}

	@Override
	public void getSchema(Class<?> type, OutputStream outputStream) throws SerializationException {
		JsonFactory jsonFactory = new JsonFactory();
		try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {
			SchemaWriter schemaWriter = new SchemaWriter();
			schemaWriter.jsonGenerator = jsonGenerator;
			schemaWriter.element = new Analyzer().analyse(type);
			schemaWriter.write();

			jsonGenerator.flush();
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
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
		for (Element item : this.element.getChilds()) {
			this.write(this.jsonGenerator, item);
		}
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