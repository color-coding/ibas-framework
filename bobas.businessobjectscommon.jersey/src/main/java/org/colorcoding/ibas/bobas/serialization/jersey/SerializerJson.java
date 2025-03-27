package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

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
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, this.isIncludeJsonRoot());
			marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			marshaller.setProperty(MarshallerProperties.JSON_TYPE_COMPATIBILITY, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, outputStream);
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputStream ipnInputStream, Class<?>... types) throws SerializationException {
		try {
			JAXBContext context = createJAXBContextJson(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, this.isIncludeJsonRoot());
			unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_TYPE_COMPATIBILITY, true);
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

	@Override
	public <T> T deserialize(InputSource inputSource, Class<?>... types) throws SerializationException {
		return this.deserialize(inputSource.getByteStream(), types);
	}

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

	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
		throw new UnsupportedOperationException();
	}

	class SchemaWriter {

		public static final String SCHEMA_VERSION = "http://json-schema.org/schema#";

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