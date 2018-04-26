package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.Serializer;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.structure.Analyzer;
import org.colorcoding.ibas.bobas.serialization.structure.Element;
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

			SchemaWriter schemaWriter = new SchemaWriterNoRoot();
			schemaWriter.jsonGenerator = jsonGenerator;
			schemaWriter.element = new Analyzer().analyse(type);
			schemaWriter.write();

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
	public Object deserialize(InputSource inputSource, Class<?>... types) throws SerializationException {
		try {
			JAXBContext context = createJAXBContextJson(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
			unmarshaller.setProperty(UnmarshallerProperties.JSON_TYPE_COMPATIBILITY, true);
			Object object = unmarshaller.unmarshal(inputSource);
			if (object instanceof JAXBElement) {
				// 因为不包括头，此处返回的是这个玩意儿
				return ((JAXBElement<?>) object).getValue();
			} else {
				return object;
			}
		} catch (JAXBException e) {
			throw new SerializationException(e);
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

class SchemaWriterNoRoot extends SchemaWriter {

	@Override
	public void write() throws JsonGenerationException, IOException {
		this.jsonGenerator.writeStartObject();
		this.jsonGenerator.writeStringField("$schema", SCHEMA_VERSION);
		this.jsonGenerator.writeStringField("type", "object");
		this.jsonGenerator.writeFieldName("properties");
		this.jsonGenerator.writeStartObject();
		this.jsonGenerator.writeFieldName("type");
		this.jsonGenerator.writeStartObject();
		this.jsonGenerator.writeStringField("type", "string");
		this.jsonGenerator.writeStringField("pattern", this.element.getType().getSimpleName());
		this.jsonGenerator.writeEndObject();
		for (Element item : this.element.getChilds()) {
			this.write(this.jsonGenerator, item);
		}
		this.jsonGenerator.writeEndObject();
		this.jsonGenerator.writeEndObject();
	}

}