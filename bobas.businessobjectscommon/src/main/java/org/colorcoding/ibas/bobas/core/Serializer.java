package org.colorcoding.ibas.bobas.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 序列化对象
 */
public class Serializer {
	/**
	 * 输出字符串类型，XML
	 */
	public final static String OUT_TYPE_XML = "xml";
	/**
	 * 输出化字符串类型，JSON
	 */
	public final static String OUT_TYPE_JSON = "json";

	/**
	 * 通过序列化与反序列化克隆对象
	 * 
	 * @param object
	 *            被克隆对象
	 * 
	 * @return 克隆的对象实例
	 */
	public static Object Clone(Object object, Class<?>... types) {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[0];
			}
			JAXBContext context = JAXBContext.newInstance(knownTypes);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);// 是否格式化生成的xml串
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			marshaller.marshal(object, outputStream);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			return unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new SerializeException(e);
		}
	}

	/**
	 * 格式化字符串输出
	 * 
	 * @param type
	 *            类型
	 * @param object
	 *            对象
	 * @param formated
	 *            是否缩进
	 * @return
	 */
	public static String toString(String type, Object object, boolean formated, Class<?>... types) {
		if (OUT_TYPE_XML.equals(type)) {
			return toXmlString(object, formated, types);
		} else if (OUT_TYPE_JSON.equals(type)) {
			return toJsonString(object, formated, types);
		}
		return object.toString();
	}

	/**
	 * 输出字符串
	 * 
	 * @param object
	 *            对象
	 * @param formated
	 *            是否格式化
	 * @return 对象的字符串
	 */
	public static String toXmlString(Object object, boolean formated, Class<?>... types) {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[0];
			}
			JAXBContext context = JAXBContext.newInstance(knownTypes);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);// 是否格式化生成的xml串
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
			StringWriter writer = new StringWriter();
			marshaller.marshal(object, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new SerializeException(e);
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
	public static Object fromXmlString(String value, Class<?>... types) {
		try {
			JAXBContext context = JAXBContext.newInstance(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes());
			return unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new SerializeException(e);
		}
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
	public static String toJsonString(Object object, boolean formated, Class<?>... types) {
		try {
			Class<?>[] knownTypes = new Class[types.length + 1];
			knownTypes[0] = object.getClass();
			for (int i = 0; i < types.length; i++) {
				knownTypes[i + 1] = types[0];
			}
			JAXBContext context = createJAXBContextJson(knownTypes);

			StringWriter writer = new StringWriter();
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new SerializeException(e);
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
	public static Object fromJsonString(String value, Class<?>... types) {
		try {
			JAXBContext context = createJAXBContextJson(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes());
			return unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new SerializeException(e);
		}
	}
}
