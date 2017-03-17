package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 序列化对象
 * 
 * 继承实现时，注意序列化和反序列化监听
 */
public abstract class Serializer<S> implements ISerializer<S> {

	@Override
	@SuppressWarnings("unchecked")
	public <T> T clone(T object, Class<?>... types) throws SerializationException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		this.serialize(object, outputStream, false, types);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		return (T) this.deserialize(inputStream, types);
	}

	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
		this.validate(this.getSchema(type), data);
	}

	@Override
	public void validate(S schema, String data) throws ValidateException {
		this.validate(schema, new ByteArrayInputStream(data.getBytes()));
	}

	@Override
	public void validate(Class<?> type, String data) throws ValidateException {
		this.validate(type, new ByteArrayInputStream(data.getBytes()));
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, Class<?>... types) throws SerializationException {
		this.serialize(object, outputStream,
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FORMATTED_OUTPUT, false), types);
	}

	@Override
	public Object deserialize(String data, Class<?>... types) throws SerializationException {
		return this.deserialize(new ByteArrayInputStream(data.getBytes()), types);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(String data, Class<T> type, Class<?>... types) throws SerializationException {
		Class<?>[] knwonTypes = new Class<?>[types.length + 1];
		knwonTypes[0] = type;
		System.arraycopy(types, 0, knwonTypes, 1, types.length);
		return (T) this.deserialize(data, knwonTypes);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputStream inputStream, Class<T> type, Class<?>... types) throws SerializationException {
		Class<?>[] knwonTypes = new Class<?>[types.length + 1];
		knwonTypes[0] = type;
		System.arraycopy(types, 0, knwonTypes, 1, types.length);
		return (T) this.deserialize(inputStream, knwonTypes);
	}

	/**
	 * 获取可能被序列化的元素
	 * 
	 * @param type
	 * @param recursion
	 * @return
	 */
	protected SchemaElement[] getSerializedElements(Class<?> type, boolean recursion) {
		SchemaElements elements = new SchemaElements();
		if (type.isPrimitive()) {
			// 基本类型不做处理
			return elements.toArray();
		} else if (type.isInterface()) {
			// 类型为接口时
			if (recursion) {
				// 递归，先获取基类
				for (Class<?> item : type.getInterfaces()) {
					if (item.getName().startsWith("java.")) {
						// 基础类型不做处理
						continue;
					}
					elements.add(this.getSerializedElements(item, recursion));
				}
			}
			for (Method method : type.getMethods()) {
				if (method.getParameterTypes().length != 1 || !method.getName().startsWith("set")) {
					continue;
				}
				String elementName = method.getName().replace("set", "");
				Class<?> elementType = method.getParameterTypes()[0];
				elements.add(new SchemaElement(elementName, elementType));
			}
		} else {
			// 类型是类
			if (recursion) {
				// 递归，先获取基类
				Class<?> superClass = type.getSuperclass();
				if (superClass != null && !superClass.equals(Object.class)) {
					elements.add(this.getSerializedElements(superClass, recursion));
				}
			}
			// 取被标记的字段
			elements.add(this.getSerializedElements(type.getDeclaredFields()));
			// 取被标记的属性
			List<SchemaElement> tmps = this.getSerializedElements(type.getDeclaredMethods());
			tmps.sort(null);// 排序
			elements.add(tmps);
		}
		return elements.toArray();
	}

	private List<SchemaElement> getSerializedElements(Field[] fields) {
		List<SchemaElement> elements = new ArrayList<>();
		for (Field field : fields) {
			Class<?> elementType = field.getType();
			String elementName = field.getName();
			String wrapperName = null;
			XmlElementWrapper xmlWrapper = field.getAnnotation(XmlElementWrapper.class);
			if (xmlWrapper != null) {
				// 首先判断是否为数组元素
				wrapperName = xmlWrapper.name();
			}
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			if (xmlElement != null) {
				if (!xmlElement.name().equals("##default")) {
					elementName = xmlElement.name();
				}
				if (xmlElement.type() != null && !xmlElement.type().getName().startsWith(XmlElement.class.getName())) {
					elementType = xmlElement.type();
				}
			} else {
				continue;
			}
			if (elementName == null) {
				continue;
			}
			if (elementType == null) {
				continue;
			}
			elements.add(new SchemaElement(elementName, wrapperName, elementType));
		}
		return elements;
	}

	private List<SchemaElement> getSerializedElements(Method[] methods) {
		List<SchemaElement> elements = new ArrayList<>();
		for (Method method : methods) {
			Class<?> elementType = method.getReturnType();
			if (elementType == null && method.getParameterTypes().length == 1) {
				// 没有返回类型时，取一个参数的设置类型
				elementType = method.getParameterTypes()[0];
			}
			String elementName = null;
			String wrapperName = null;
			XmlElementWrapper xmlWrapper = method.getAnnotation(XmlElementWrapper.class);
			if (xmlWrapper != null) {
				// 首先判断是否为数组元素
				wrapperName = xmlWrapper.name();
			}
			XmlElement xmlElement = method.getAnnotation(XmlElement.class);
			if (xmlElement != null) {
				if (elementName == null) {
					elementName = xmlElement.name();
				}
				if (xmlElement.type() != null && !xmlElement.type().getName().startsWith(XmlElement.class.getName())) {
					elementType = xmlElement.type();
				}
			}
			if (elementName == null) {
				continue;
			}
			if (elementType == null) {
				continue;
			}
			elements.add(new SchemaElement(elementName, wrapperName, elementType));
		}
		return elements;
	}

	@Override
	public abstract void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types);

	@Override
	public abstract Object deserialize(InputStream inputStream, Class<?>... types) throws SerializationException;

	@Override
	public abstract void validate(S schema, InputStream data) throws ValidateException;

	@Override
	public abstract S getSchema(Class<?> type) throws SerializationException;

}
