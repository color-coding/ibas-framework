package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.serialization.SchemaElement;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.Serializer;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;

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
 * JSON序列化，不包含ROOT
 * 
 * 注意： 无ROOT反序列化时需要type属性，没有基类的对象序列化时没有此属性 以上情况可继承Serializable解决
 * 
 * @author Niuren.Zhu
 *
 */
public class SerializerJsonNoRoot extends Serializer<JsonSchema> {
	public static final String SCHEMA_VERSION = "http://json-schema.org/schema#";

	@Override
	public void getSchema(Class<?> type, OutputStream outputStream) throws SerializationException {
		JsonFactory jsonFactory = new JsonFactory();
		try {
			JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("$schema", SCHEMA_VERSION);
			jsonGenerator.writeStringField("type", "object");
			jsonGenerator.writeFieldName("properties");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeFieldName("type");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("type", "string");
			jsonGenerator.writeStringField("pattern", type.getSimpleName());
			jsonGenerator.writeEndObject();
			this.createSchemaElement(jsonGenerator, type);
			jsonGenerator.writeEndObject();
			jsonGenerator.writeEndObject();
			jsonGenerator.flush();
			jsonGenerator.close();
		} catch (IOException e) {
			throw new SerializationException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	protected void createSchemaElement(JsonGenerator jsonGenerator, Class<?> type)
			throws JsonGenerationException, IOException {
		for (SchemaElement item : this.getSerializedElements(type, true)) {
			if (type.equals(item.getType())) {
				// 子项是自身，不做处理
				continue;
			}
			jsonGenerator.writeFieldName(
					item.getWrapper() != null && !item.getWrapper().isEmpty() ? item.getWrapper() : item.getName());
			jsonGenerator.writeStartObject();
			if (this.getKnownTyps().containsKey(item.getType().getName())) {
				// 已知类型
				jsonGenerator.writeStringField("type", this.getKnownTyps().get(item.getType().getName()));
			} else if (item.getType().isEnum()) {
				// 枚举类型
				jsonGenerator.writeStringField("type", "string");
				jsonGenerator.writeArrayFieldStart("enum");
				for (Object enumItem : item.getType().getEnumConstants()) {
					if (enumItem instanceof Enum<?>) {
						// 枚举值（比对枚举索引）
						Enum<?> itemValue = (Enum<?>) enumItem;
						jsonGenerator.writeString(itemValue.name());
					}
				}
				jsonGenerator.writeEndArray();
			} else if (item.getType().equals(DateTime.class)) {
				// 日期类型
				jsonGenerator.writeStringField("type", "string");
				// 格式：2000-01-01 or 2000-01-01T00:00:00
				jsonGenerator.writeStringField("pattern",
						"^|[0-9]{4}-[0-1][0-9]-[0-3][0-9]|[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-6][0-9]:[0-6][0-9]$");
			} else if (item.getWrapper() != null && !item.getWrapper().isEmpty()) {
				jsonGenerator.writeStringField("type", "array");
				jsonGenerator.writeFieldName("items");
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("type", "object");
				jsonGenerator.writeFieldName("properties");
				jsonGenerator.writeStartObject();
				this.createSchemaElement(jsonGenerator, item.getType());
				jsonGenerator.writeEndObject();
				jsonGenerator.writeEndObject();
			} else {
				jsonGenerator.writeStringField("type", "object");
				jsonGenerator.writeFieldName("properties");
				jsonGenerator.writeStartObject();
				this.createSchemaElement(jsonGenerator, item.getType());
				jsonGenerator.writeEndObject();
			}
			jsonGenerator.writeEndObject();
		}
	}

	private Map<String, String> knownTypes;

	public Map<String, String> getKnownTyps() {
		if (this.knownTypes == null) {
			this.knownTypes = new HashMap<>();
			this.knownTypes.put("integer", "integer");
			this.knownTypes.put("short", "integer");
			this.knownTypes.put("boolean", "boolean");
			this.knownTypes.put("float", "number");
			this.knownTypes.put("double", "number");
			this.knownTypes.put("java.lang.Integer", "integer");
			this.knownTypes.put("java.lang.String", "string");
			this.knownTypes.put("java.lang.Short", "integer");
			this.knownTypes.put("java.lang.Boolean", "boolean");
			this.knownTypes.put("java.lang.Float", "number");
			this.knownTypes.put("java.lang.Double", "number");
			this.knownTypes.put("java.lang.Character", "string");
			this.knownTypes.put("java.math.BigDecimal", "number");
			this.knownTypes.put("java.util.Date", "string");
			this.knownTypes.put("org.colorcoding.ibas.bobas.data.Decimal", "number");
			// this.knownTypes.put("org.colorcoding.ibas.bobas.data.DateTime",
			// "string");
		}
		return this.knownTypes;
	}

	@Override
	public JsonSchema getSchema(Class<?> type) throws SerializationException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			this.getSchema(type, outputStream);
			JsonNode jsonSchema = JsonLoader
					.fromReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())));
			return JsonSchemaFactory.byDefault().getJsonSchema(jsonSchema);
		} catch (IOException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	private JAXBContext context;

	/**
	 * 创建json序列化类
	 * 
	 * @param types
	 *            已知类型
	 * @return
	 * @throws JAXBException
	 */
	protected JAXBContext createJAXBContextJson(Class<?>... types) throws JAXBException {
		if (context != null) {
			return context;
		}
		String factoryKey = "javax.xml.bind.context.factory";
		String factoryValue = System.getProperty(factoryKey);
		try {
			// 重置序列化工厂
			System.setProperty(factoryKey, "org.eclipse.persistence.jaxb.JAXBContextFactory");
			context = JAXBContext.newInstance(types);
			return context;
		} finally {
			// 还原工厂参数
			if (factoryValue == null) {
				System.clearProperty(factoryKey);
			} else {
				System.setProperty(factoryKey, factoryValue);
			}
		}
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
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			marshaller.setProperty(MarshallerProperties.JSON_TYPE_COMPATIBILITY, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, outputStream);
		} catch (JAXBException e) {
			throw new SerializationException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public Object deserialize(InputStream inputStream, Class<?>... types) throws SerializationException {
		try {
			JAXBContext context = createJAXBContextJson(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_TYPE_COMPATIBILITY, true);
			Object object = unmarshaller.unmarshal(inputStream);
			if (object instanceof JAXBElement) {
				// 因为不包括头，此处返回的是这个玩意儿
				return ((JAXBElement<?>) object).getValue();
			} else {
				return object;
			}
		} catch (JAXBException e) {
			throw new SerializationException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void validate(JsonSchema schema, InputStream data) throws ValidateException {
		try {
			JsonNode jsonData = JsonLoader.fromReader(new InputStreamReader(data));
			this.validate(schema, jsonData);
		} catch (IOException e) {
			throw new ValidateException(e);
		} finally {
			if (data != null) {
				try {
					data.close();
				} catch (IOException e) {
				}
			}
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
