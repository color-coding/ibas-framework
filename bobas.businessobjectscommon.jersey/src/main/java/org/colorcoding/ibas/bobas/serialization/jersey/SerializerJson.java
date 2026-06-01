package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
import org.eclipse.persistence.oxm.json.JsonStructureSource;

public class SerializerJson extends Serializer {

	public SerializerJson() {
		this.setIncludeJsonRoot(false);
	}

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

	private Map<String, JAXBContext> contextCache;

	/**
	 * 创建JAXB上下文（首次创建后缓存复用）
	 *
	 * @param types 已知类型
	 * @return JAXB上下文
	 * @throws JAXBException 创建上下文失败时抛出
	 */
	protected JAXBContext createJAXBContextJson(Class<?>... types) throws JAXBException {
		String key = Arrays.stream(types).map(Class::getName).sorted().collect(Collectors.joining(","));
		if (this.contextCache == null) {
			this.contextCache = new HashMap<>();
		}
		JAXBContext ctx = this.contextCache.get(key);
		if (ctx == null) {
			ctx = JAXBContextFactory.createContext(types, null);
			this.contextCache.put(key, ctx);
		}
		return ctx;
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
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, this.isIncludeJsonRoot());
			marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			marshaller.setProperty(MarshallerProperties.JSON_TYPE_COMPATIBILITY, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, outputStream);
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	private Unmarshaller createUnmarshaller(Class<?>... types) throws JAXBException {
		// 反序列化不使用缓存，因为types可能不包含根类型，
		// 使用缓存的context会导致MOXy找不到根类型的descriptor
		JAXBContext context = JAXBContextFactory.createContext(types, null);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, this.isIncludeJsonRoot());
		unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		unmarshaller.setProperty(UnmarshallerProperties.JSON_TYPE_COMPATIBILITY, true);
		return unmarshaller;
	}

	/**
	 * 反序列化JSON流，不包含根元素时自动提取JAXBElement的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputStream ipnInputStream, Class<?>... types) throws SerializationException {
		try {
			Unmarshaller unmarshaller = this.createUnmarshaller(types);
			Object object = unmarshaller.unmarshal(ipnInputStream);
			if (object instanceof JAXBElement) {
				// 因为不包括头，此处返回的是这个玩意儿
				return ((JAXBElement<T>) object).getValue();
			} else {
				return (T) object;
			}
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * 反序列化JSON读取器，不包含根元素时自动提取JAXBElement的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(Reader reader, Class<?>... types) throws SerializationException {
		try {
			Unmarshaller unmarshaller = this.createUnmarshaller(types);
			Object object = unmarshaller.unmarshal(reader);
			if (object instanceof JAXBElement) {
				// 因为不包括头，此处返回的是这个玩意儿
				return ((JAXBElement<T>) object).getValue();
			} else {
				return (T) object;
			}
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * 反序列化JsonObject，不包含根元素时自动提取JAXBElement的值
	 */
	@SuppressWarnings("unchecked")
	public <T> T deserialize(JsonObject jsonObject, Class<?>... types) throws SerializationException {
		try {
			Unmarshaller unmarshaller = this.createUnmarshaller(types);
			Object object = unmarshaller.unmarshal(new JsonStructureSource(jsonObject));
			if (object instanceof JAXBElement) {
				// 因为不包括头，此处返回的是这个玩意儿
				return ((JAXBElement<T>) object).getValue();
			} else {
				return (T) object;
			}
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * 输出类型的JSON Schema（draft-07）
	 */
	@Override
	public void schema(Class<?> type, OutputStream outputStream) throws SerializationException {

		SchemaWriter schemaWriter = new SchemaWriter();
		schemaWriter.isIncludeJsonRoot = this.isIncludeJsonRoot();
		schemaWriter.jsonGenerator = Json.createObjectBuilder();
		schemaWriter.element = new Analyzer().analyse(type);
		schemaWriter.write();

		try (JsonWriter jsonWriter = Json.createWriter(outputStream)) {
			jsonWriter.write(schemaWriter.jsonGenerator.build());
		}
	}

	/**
	 * 基于结构分析的有限校验，校验JSON格式合法性、属性存在性及基本类型匹配。 不校验pattern、长度等细节约束。
	 */
	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
		try {
			JsonObject jsonData = Json.createReader(data).readObject();
			ElementRoot element = new Analyzer().analyse(type);
			SchemaValidator validator = new SchemaValidator();
			validator.validate(jsonData, element);
		} catch (ValidateException e) {
			throw e;
		} catch (Exception e) {
			throw new ValidateException(e);
		}
	}

	/**
	 * 结构校验器，遍历元素树对JSON数据进行有限校验
	 */
	class SchemaValidator {

		public SchemaValidator() {
			this.knownTypes = new HashMap<>();
			this.knownTypes.put("int", "integer");
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
			this.knownTypes.put(DateTime.class.getName(), "string");
		}

		protected Map<String, String> knownTypes;

		public void validate(JsonObject jsonData, ElementRoot element) throws ValidateException {
			if (SerializerJson.this.isIncludeJsonRoot()) {
				// 包含根元素时，先取内层对象
				String rootKey = element.getName();
				if (!jsonData.containsKey(rootKey)) {
					throw new ValidateException(String.format("missing root element [%s].", rootKey));
				}
				JsonValue rootValue = jsonData.get(rootKey);
				if (rootValue.getValueType() != JsonValue.ValueType.OBJECT) {
					throw new ValidateException(String.format("root element [%s] is not an object.", rootKey));
				}
				jsonData = rootValue.asJsonObject();
			}
			this.validateProperties(jsonData, element);
		}

		private void validateProperties(JsonObject jsonData, Element parent) throws ValidateException {
			for (Element child : parent.getChilds()) {
				String key = child.isCollection() ? child.getWrapper() : child.getName();
				if (!jsonData.containsKey(key)) {
					// 缺少属性，由于schema允许minOccurs=0，跳过
					continue;
				}
				JsonValue jsonValue = jsonData.get(key);
				if (jsonValue == JsonValue.NULL) {
					// null值，由于schema允许nillable，跳过
					continue;
				}
				if (child.isCollection()) {
					this.validateArray(jsonValue, child);
				} else {
					this.validateValue(jsonValue, child);
				}
			}
		}

		private void validateArray(JsonValue jsonValue, Element element) throws ValidateException {
			if (jsonValue.getValueType() != JsonValue.ValueType.ARRAY) {
				throw new ValidateException(String.format("property [%s] expected array but was [%s].",
						element.getWrapper(), jsonValue.getValueType()));
			}
			javax.json.JsonArray jsonArray = jsonValue.asJsonArray();
			for (JsonValue item : jsonArray) {
				if (item == JsonValue.NULL) {
					continue;
				}
				if (!element.getChilds().isEmpty()) {
					if (item.getValueType() != JsonValue.ValueType.OBJECT) {
						throw new ValidateException(String.format("array item of [%s] expected object but was [%s].",
								element.getWrapper(), item.getValueType()));
					}
					this.validateProperties(item.asJsonObject(), element);
				} else {
					String itemTypeName = this.knownTypes.get(element.getType().getName());
					if (itemTypeName != null) {
						this.validatePrimitiveType(item, element.getName(), itemTypeName);
					}
				}
			}
		}

		private void validateValue(JsonValue jsonValue, Element element) throws ValidateException {
			String typeName = this.knownTypes.get(element.getType().getName());
			if (typeName != null) {
				// 已知基本类型
				this.validatePrimitiveType(jsonValue, element.getName(), typeName);
			} else if (element.getType().isEnum()) {
				// 枚举类型
				this.validateEnum(jsonValue, element);
			} else if (element.getType() == DateTime.class) {
				// 日期类型，仅校验为字符串
				if (jsonValue.getValueType() != JsonValue.ValueType.STRING) {
					throw new ValidateException(String.format("property [%s] expected date string but was [%s].",
							element.getName(), jsonValue.getValueType()));
				}
			} else {
				// 对象类型
				if (jsonValue.getValueType() != JsonValue.ValueType.OBJECT) {
					throw new ValidateException(String.format("property [%s] expected object but was [%s].",
							element.getName(), jsonValue.getValueType()));
				}
				if (!element.getChilds().isEmpty()) {
					this.validateProperties(jsonValue.asJsonObject(), element);
				}
			}
		}

		private void validatePrimitiveType(JsonValue jsonValue, String propertyName, String expectedType)
				throws ValidateException {
			switch (expectedType) {
			case "integer":
				if (jsonValue.getValueType() != JsonValue.ValueType.NUMBER) {
					throw new ValidateException(String.format("property [%s] expected integer but was [%s].",
							propertyName, jsonValue.getValueType()));
				}
				try {
					jsonValue.toString();
					((javax.json.JsonNumber) jsonValue).intValueExact();
				} catch (ArithmeticException e) {
					throw new ValidateException(String
							.format("property [%s] expected integer but value is not exact integer.", propertyName));
				} catch (ClassCastException e) {
					throw new ValidateException(String.format("property [%s] expected integer but was [%s].",
							propertyName, jsonValue.getValueType()));
				}
				break;
			case "number":
				if (jsonValue.getValueType() != JsonValue.ValueType.NUMBER) {
					throw new ValidateException(String.format("property [%s] expected number but was [%s].",
							propertyName, jsonValue.getValueType()));
				}
				break;
			case "boolean":
				if (jsonValue.getValueType() != JsonValue.ValueType.TRUE
						&& jsonValue.getValueType() != JsonValue.ValueType.FALSE) {
					throw new ValidateException(String.format("property [%s] expected boolean but was [%s].",
							propertyName, jsonValue.getValueType()));
				}
				break;
			case "string":
				if (jsonValue.getValueType() != JsonValue.ValueType.STRING) {
					throw new ValidateException(String.format("property [%s] expected string but was [%s].",
							propertyName, jsonValue.getValueType()));
				}
				break;
			}
		}

		private void validateEnum(JsonValue jsonValue, Element element) throws ValidateException {
			if (jsonValue.getValueType() != JsonValue.ValueType.STRING) {
				throw new ValidateException(String.format("property [%s] expected enum string but was [%s].",
						element.getName(), jsonValue.getValueType()));
			}
			String strValue = ((javax.json.JsonString) jsonValue).getString();
			for (Object enumItem : element.getType().getEnumConstants()) {
				if (enumItem instanceof Enum<?>) {
					if (((Enum<?>) enumItem).name().equals(strValue)) {
						return;
					}
				}
			}
			throw new ValidateException(String.format("property [%s] value [%s] is not a valid enum of [%s].",
					element.getName(), strValue, element.getType().getSimpleName()));
		}
	}

	class SchemaWriter {

		public static final String SCHEMA_VERSION = "http://json-schema.org/draft-07/schema#";

		public SchemaWriter() {
			this.knownTypes = new HashMap<>();
			this.knownTypes.put("int", "integer");
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

		public JsonObjectBuilder jsonGenerator;
		public ElementRoot element;
		public boolean isIncludeJsonRoot;

		protected Map<String, String> knownTypes;

		public void write() {
			this.jsonGenerator.add("$schema", SCHEMA_VERSION);
			this.jsonGenerator.add("type", "object");
			JsonObjectBuilder objectBuilder = this.jsonGenerator;
			// 包含头
			if (this.isIncludeJsonRoot) {
				JsonObjectBuilder headerBuilder = Json.createObjectBuilder();
				headerBuilder.add("type", "string");
				headerBuilder.add("pattern", this.element.getType().getSimpleName());
				JsonObjectBuilder propertiesBuilder = Json.createObjectBuilder();
				propertiesBuilder.add("type", headerBuilder);
				objectBuilder = propertiesBuilder;
			}

			if (!this.element.getChilds().isEmpty()) {
				JsonObjectBuilder propertiesBuilder = Json.createObjectBuilder();
				for (Element item : this.element.getChilds()) {
					this.write(propertiesBuilder, item);
				}
				objectBuilder.add("properties", propertiesBuilder);
			}
			if (objectBuilder != this.jsonGenerator) {
				this.jsonGenerator.add("properties", objectBuilder);
			}

		}

		protected void write(JsonObjectBuilder objectBuilder, Element element) {
			if (element.isCollection()) {
				JsonObjectBuilder elementBuilder = Json.createObjectBuilder();
				elementBuilder.add("type", "array");
				JsonObjectBuilder itemsBuilder = Json.createObjectBuilder();
				itemsBuilder.add("type", "object");

				if (!element.getChilds().isEmpty()) {
					JsonObjectBuilder propertiesBuilder = Json.createObjectBuilder();
					for (Element item : element.getChilds()) {
						this.write(propertiesBuilder, item);
					}
					itemsBuilder.add("properties", propertiesBuilder);
				}

				elementBuilder.add("items", itemsBuilder);
				objectBuilder.add(element.getWrapper(), elementBuilder);
			} else {
				JsonObjectBuilder elementBuilder = Json.createObjectBuilder();
				String typeName = this.knownTypes.get(element.getType().getName());
				if (typeName != null) {
					// 已知类型
					elementBuilder.add("type", typeName);
				} else if (element.getType().isEnum()) {
					// 枚举类型
					elementBuilder.add("type", "string");
					JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
					for (Object enumItem : element.getType().getEnumConstants()) {
						if (enumItem instanceof Enum<?>) {
							// 枚举值（比对枚举索引）
							Enum<?> itemValue = (Enum<?>) enumItem;
							arrayBuilder.add(itemValue.name());
						}
					}
					elementBuilder.add("enum", arrayBuilder);
				} else if (element.getType().equals(DateTime.class)) {
					// 日期类型
					elementBuilder.add("type", "string");
					// 格式：2000-01-01 or 2000-01-01T00:00:00
					elementBuilder.add("pattern",
							"^|[0-9]{4}-[0-1][0-9]-[0-3][0-9]|[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-6][0-9]:[0-6][0-9]$");
				} else {
					elementBuilder.add("type", "object");
					if (!element.getChilds().isEmpty()) {
						JsonObjectBuilder propertiesBuilder = Json.createObjectBuilder();
						for (Element item : element.getChilds()) {
							this.write(propertiesBuilder, item);
						}
						elementBuilder.add("properties", propertiesBuilder);
					}
				}
				objectBuilder.add(element.getName(), elementBuilder);
			}
		}
	}

}