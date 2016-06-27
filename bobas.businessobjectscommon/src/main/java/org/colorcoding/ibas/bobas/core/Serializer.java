package org.colorcoding.ibas.bobas.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 对象深度克隆
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
	public static Object Clone(Object object) {
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
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
			throw new ClonerException(e);
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
	public static String toString(String type, Object object, boolean formated) {
		if (OUT_TYPE_XML.equals(type)) {
			return toXmlString(object, false);
		} else if (OUT_TYPE_JSON.equals(type)) {
			return toJsonString(object, false);
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
	public static String toXmlString(Object object, boolean formated) {
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);// 是否格式化生成的xml串
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
			StringWriter writer = new StringWriter();
			marshaller.marshal(object, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new ClonerException(e);
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
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
			Unmarshaller unmarshaller = context.createUnmarshaller();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes());
			return unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new ClonerException(e);
		}
	}

	/**
	 * 格式化json字符串
	 * 
	 * @param object
	 * @param formated
	 * @return
	 */
	public static String toJsonString(Object object, boolean formated) {
		try {
			// 重置序列化工厂
			System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
			Map<String, Object> properties = new HashMap<String, Object>(2);
			// 指定格式为json，避免引用此处没有静态变量
			properties.put("eclipselink.media-type", "application/json");
			// json数组不要前缀类型
			properties.put("eclipselink.json.wrapper-as-array-name", true);
			JAXBContext context = JAXBContext.newInstance(new Class[] { object.getClass() }, properties);

			StringWriter writer = new StringWriter();
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);
			marshaller.marshal(object, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new ClonerException(e);
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
			// 重置序列化工厂
			System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
			Map<String, Object> properties = new HashMap<String, Object>(2);
			// 指定格式为json，避免引用此处没有静态变量
			properties.put("eclipselink.media-type", "application/json");
			// json数组不要前缀类型
			properties.put("eclipselink.json.wrapper-as-array-name", true);
			JAXBContext context = JAXBContext.newInstance(types, properties);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes());
			return unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new ClonerException(e);
		}
	}
}
