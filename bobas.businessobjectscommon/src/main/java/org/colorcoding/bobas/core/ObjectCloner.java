package org.colorcoding.bobas.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 对象深度克隆
 */
public class ObjectCloner {

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
}
