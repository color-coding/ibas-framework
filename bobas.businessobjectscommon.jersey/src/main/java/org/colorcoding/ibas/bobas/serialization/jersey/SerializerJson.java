package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.Serializer;
import org.colorcoding.ibas.bobas.serialization.ValidateException;

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
 * 序列化，添加json处理
 * 
 * @author Niuren.Zhu
 *
 */
public class SerializerJson extends Serializer {

	/**
	 * 创建json序列化类
	 * 
	 * @param types
	 *            已知类型
	 * @return
	 * @throws JAXBException
	 */
	private static JAXBContext createJAXBContextJson(Class<?>... types) throws JAXBException {
		String factoryKey = "javax.xml.bind.context.factory";
		String factoryValue = System.getProperty(factoryKey);
		try {
			// 重置序列化工厂
			System.setProperty(factoryKey, "org.eclipse.persistence.jaxb.JAXBContextFactory");
			Map<String, Object> properties = new HashMap<String, Object>(2);
			// 指定格式为json，避免引用此处没有静态变量
			properties.put("eclipselink.media-type", "application/json");
			// json数组不要前缀类型
			properties.put("eclipselink.json.wrapper-as-array-name", true);
			JAXBContext context = JAXBContext.newInstance(types, properties);
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T clone(T object, Class<?>... types) throws SerializationException {
		ByteArrayInputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[i];
			}
			JAXBContext context = createJAXBContextJson(knownTypes);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);// 是否格式化生成的xml串
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
			outputStream = new ByteArrayOutputStream();
			marshaller.marshal(object, outputStream);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			return (T) unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new SerializationException(e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				RuntimeLog.log(e);
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				RuntimeLog.log(e);
			}
		}
	}

	@Override
	public void serialize(Object object, Writer writer, boolean formated, Class<?>... types)
			throws SerializationException {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[i];
			}
			JAXBContext context = createJAXBContextJson(knownTypes);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, writer);
		} catch (Exception e) {
			throw new SerializationException(e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				RuntimeLog.log(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> type, Class<?>... types) {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = type;
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[i];
			}
			JAXBContext context = createJAXBContextJson(knownTypes);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new SerializationException(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				RuntimeLog.log(e);
			}
		}
	}

	@Override
	public void validate(Class<?> type, Reader reader) throws ValidateException {
		try {
			StringWriter writer = new StringWriter();
			this.getSchema(type, writer);
			JsonNode jsonSchema = JsonLoader.fromString(writer.toString());
			JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(jsonSchema);
			this.validate(schema, reader);
		} catch (IOException e) {
			throw new ValidateException(e);
		} catch (ProcessingException e) {
			throw new ValidateException(e);
		}
	}

	public void validate(JsonSchema schema, Reader reader) throws ValidateException {
		try {
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

	public static final String SCHEMA_VERSION = "http://json-schema.org/draft-04/schema#";

	@Override
	public void getSchema(Class<?> type, Writer writer) throws SerializationException {
		JsonFactory jsonFactory = new JsonFactory();
		try {
			JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("$schema", SCHEMA_VERSION);
			jsonGenerator.writeStringField("type", "object");
			jsonGenerator.writeFieldName("properties");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeFieldName(type.getSimpleName());
			this.createSchemaElement(jsonGenerator, type);
			jsonGenerator.writeEndObject();
			jsonGenerator.writeEndObject();
			jsonGenerator.flush();
			jsonGenerator.close();
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	protected void createSchemaElement(JsonGenerator jsonGenerator, Class<?> type)
			throws JsonGenerationException, IOException {
		jsonGenerator.writeStartObject();
		if (this.getKnownTyps().containsKey(type.getName())) {
			// 已知类型
			jsonGenerator.writeStringField("type", this.getKnownTyps().get(type.getName()));
		} else if (type.isEnum()) {
			// 枚举类型
			jsonGenerator.writeStringField("type", "string");
			jsonGenerator.writeArrayFieldStart("enum");
			for (Object enumItem : type.getEnumConstants()) {
				if (enumItem instanceof Enum<?>) {
					// 枚举值（比对枚举索引）
					Enum<?> itemValue = (Enum<?>) enumItem;
					jsonGenerator.writeString(itemValue.name());
				}
			}
			jsonGenerator.writeEndArray();
		} else if (type.equals(DateTime.class)) {
			// 日期类型
			jsonGenerator.writeStringField("type", "string");
			// 格式：2000-01-01 or 2000-01-01T00:00:00
			jsonGenerator.writeStringField("pattern",
					"^|[0-9]{4}-[0-1][0-9]-[0-3][0-9]|[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-6][0-9]:[0-6][0-9]$");
		} else {
			jsonGenerator.writeStringField("type", "object");
			jsonGenerator.writeFieldName("properties");
			jsonGenerator.writeStartObject();
			for (SchemaElement item : this.getSerializedElements(type, true)) {
				if (item.getWrapper() != null && !item.getWrapper().isEmpty()) {
					jsonGenerator.writeFieldName(item.getWrapper());
					jsonGenerator.writeStartObject();
					jsonGenerator.writeStringField("type", "array");
					jsonGenerator.writeFieldName("items");
					this.createSchemaElement(jsonGenerator, item.getType());
					jsonGenerator.writeEndObject();
				} else {
					jsonGenerator.writeFieldName(item.getName());
					this.createSchemaElement(jsonGenerator, item.getType());
				}
			}
			jsonGenerator.writeEndObject();
		}
		jsonGenerator.writeEndObject();
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
}
