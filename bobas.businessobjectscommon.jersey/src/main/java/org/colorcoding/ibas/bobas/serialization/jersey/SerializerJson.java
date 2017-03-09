package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.Serializer;
import org.colorcoding.ibas.bobas.serialization.ValidateException;

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
	public void getSchema(Class<?> type, Writer writer) throws SerializationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void validate(Class<?> type, Reader reader) throws ValidateException {
		throw new UnsupportedOperationException();
	}

}
