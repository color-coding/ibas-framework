package org.colorcoding.ibas.bobas.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 序列化对象
 * 
 * 继承实现时，注意序列化和反序列化监听
 */
public class Serializer {
	/**
	 * 输出字符串类型，XML
	 */
	public final static String DATA_TYPE_XML = "xml";
	/**
	 * 输出化字符串类型，JSON
	 */
	public final static String DATA_TYPE_JSON = "json";

	private static Serializer instance;

	private static Serializer create() {
		if (instance == null) {
			synchronized (Serializer.class) {
				if (instance == null) {
					String className = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_SERIALIZER);
					if (className != null && !className.isEmpty()) {
						if (className.indexOf(".") < 0) {
							// 没有命名空间，补齐命名空间
							className = String.format("org.colorcoding.ibas.bobas.%s.Serializer", className);
						}
						try {
							Object object = BOFactory.create().createInstance(className);
							if (object instanceof Serializer) {
								instance = (Serializer) object;
							} else {
								throw new InvalidClassException(
										i18n.prop("msg_bobas_required_class", Serializer.class.getName()));
							}
						} catch (Exception e) {
							RuntimeLog.log(e);
						}
					}
					if (instance == null) {
						instance = new Serializer();
					}
				}
			}
		}
		return instance;
	}

	/**
	 * 复制对象
	 * 
	 * @param object
	 *            被复制的对象
	 * @param types
	 *            已知类型
	 * @return 新的对象实例
	 */
	public static Object clone(Object object, Class<?>... types) {
		ByteArrayInputStream inputStream = null;
		ByteArrayOutputStream outputStream = null;
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
			outputStream = new ByteArrayOutputStream();
			marshaller.marshal(object, outputStream);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			return unmarshaller.unmarshal(inputStream);
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
	 * 序列化为字符串
	 * 
	 * @param type
	 *            字符串格式
	 * @param object
	 *            数据
	 * @param formated
	 *            是否格式化
	 * @param types
	 *            已知类型
	 * @return 数据字符串
	 */
	public static String serializeString(String type, Object object, boolean formated, Class<?>... types) {
		return create().toString(type, object, formated, types);
	}

	/**
	 * 序列化为字符串（XML）
	 * 
	 * @param object
	 *            数据
	 * @param formated
	 *            是否格式化
	 * @param types
	 *            已知类型
	 * @return 数据字符串
	 */
	public static String serializeString(Object object, boolean formated, Class<?>... types) {
		return serializeString(DATA_TYPE_XML, object, formated, types);
	}

	/**
	 * 序列化为字符串（XML）
	 * 
	 * @param object
	 *            数据
	 * @param types
	 *            已知类型
	 * @return 数据字符串
	 */
	public static String serializeString(Object object, Class<?>... types) {
		return serializeString(object, false, types);
	}

	/**
	 * 反序列化对象
	 * 
	 * @param type
	 *            输入格式
	 * @param value
	 *            值
	 * @param types
	 *            已知类型
	 * @return 对象实例
	 */
	public static Object deserializeString(String type, String value, Class<?>... types) {
		return create().fromString(type, value, types);
	}

	/**
	 * 反序列化对象
	 * 
	 * @param value
	 *            值（XML）
	 * @param types
	 *            已知类型
	 * @return 对象实例
	 */
	public static Object deserializeString(String value, Class<?>... types) {
		return deserializeString(DATA_TYPE_XML, value, types);
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
	public String toString(String type, Object object, boolean formated, Class<?>... types) {
		if (DATA_TYPE_XML.equals(type)) {
			return toXmlString(object, formated, types);
		}
		throw new SerializationException(i18n.prop("msg_bobas_not_support_serialize_type", type));
	}

	/**
	 * 获取对象
	 * 
	 * @param type
	 *            输入类型
	 * @param value
	 *            数据
	 * @param types
	 *            已知类型
	 * @return 对象实例
	 */
	public Object fromString(String type, String value, Class<?>... types) {
		if (DATA_TYPE_XML.equals(type)) {
			return fromXmlString(value, types);
		}
		throw new SerializationException(i18n.prop("msg_bobas_not_support_serialize_type", type));
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
	public String toXmlString(Object object, boolean formated, Class<?>... types) {
		StringWriter writer = null;
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
			writer = new StringWriter();
			marshaller.marshal(object, writer);
			return writer.toString();
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

	/**
	 * 从xml字符形成对象
	 * 
	 * @param value
	 *            字符串
	 * @param types
	 *            相关对象
	 * @return 对象实例
	 */
	public Object fromXmlString(String value, Class<?>... types) {
		ByteArrayInputStream inputStream = null;
		try {
			JAXBContext context = JAXBContext.newInstance(types);
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
