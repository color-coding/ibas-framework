package org.colorcoding.ibas.bobas.jersey;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.core.SerializationException;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 序列化，添加json处理
 * 
 * @author Niuren.Zhu
 *
 */
public class Serializer extends org.colorcoding.ibas.bobas.core.Serializer {

	@Override
	public String toString(String type, Object object, boolean formated, Class<?>... types) {
		if (type.equals(Serializer.DATA_TYPE_JSON)) {
			return this.toJsonString(object, formated, types);
		}
		return super.toString(type, object, formated, types);
	}

	@Override
	public Object fromString(String type, String value, Class<?>... types) {
		if (type.equals(Serializer.DATA_TYPE_JSON)) {
			return this.fromJsonString(value, types);
		}
		return super.fromString(type, value, types);
	}

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

	/**
	 * 格式化json字符串
	 * 
	 * @param object
	 *            数据
	 * @param formated
	 *            是否带格式
	 * @param types
	 *            已知的类型
	 * @return
	 */
	public String toJsonString(Object object, boolean formated, Class<?>... types) {
		StringWriter writer = null;
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[0];
			}
			JAXBContext context = createJAXBContextJson(knownTypes);
			writer = new StringWriter();
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, writer);
			return writer.toString();
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

	/**
	 * 从json字符形成对象
	 * 
	 * @param value
	 *            字符串
	 * @param types
	 *            相关对象
	 * @return 对象实例
	 */
	public Object fromJsonString(String value, Class<?>... types) {
		ByteArrayInputStream inputStream = null;
		try {
			JAXBContext context = createJAXBContextJson(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			inputStream = new ByteArrayInputStream(value.getBytes());
			return unmarshaller.unmarshal(inputStream);
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
}
