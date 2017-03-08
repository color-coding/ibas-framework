package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 序列化对象
 * 
 * 继承实现时，注意序列化和反序列化监听
 */
public class SerializerXml extends Serializer {

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
			JAXBContext context = JAXBContext.newInstance(knownTypes);
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

	/**
	 * 从xml字符形成对象
	 * 
	 * @param value
	 *            字符串
	 * @param types
	 *            相关对象
	 * @return 对象实例
	 */
	@SuppressWarnings("unchecked")
	public <T> T deserialize(java.io.InputStream inputStream, Class<T> type, Class<?>... types) {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = type;
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[i];
			}
			JAXBContext context = JAXBContext.newInstance(knownTypes);
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
	public void serialize(Object object, Writer writer, boolean formated, Class<?>... types)
			throws SerializationException {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[i];
			}
			JAXBContext context = JAXBContext.newInstance(knownTypes);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);// 是否格式化生成的xml串
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
			marshaller.marshal(object, writer);
		} catch (Exception e) {
			throw new SerializationException(e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				RuntimeLog.log(e);
			}
		}

	}

}
