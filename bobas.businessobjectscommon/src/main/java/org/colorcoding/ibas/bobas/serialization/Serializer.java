package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * 序列化对象
 * 
 * 继承实现时，注意序列化和反序列化监听
 */
public abstract class Serializer implements ISerializer {

	/**
	 * 从xml字符形成对象
	 * 
	 * @param value
	 *            字符串
	 * @param types
	 *            相关对象
	 * @return 对象实例
	 */
	public abstract <T> T deserialize(java.io.InputStream inputStream, Class<T> type, Class<?>... types);

	@Override
	public <T> T deserialize(String data, Class<T> type, Class<?>... types) throws SerializationException {
		return this.deserialize(new ByteArrayInputStream(data.getBytes()), type, types);
	}

	@Override
	public <T> T deserialize(File file, Class<T> type, Class<?>... types) throws SerializationException {
		try {
			return this.deserialize(new FileInputStream(file), type, types);
		} catch (FileNotFoundException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public void serialize(Object object, Writer writer, Class<?>... types) throws SerializationException {
		this.serialize(object, writer, false, types);
	}

	/**
	 * 获取可能被序列化的元素
	 * 
	 * @param type
	 * @param recursion
	 * @return
	 */
	protected SchemaElement[] getSerializedElements(Class<?> type, boolean recursion) {
		Map<String, SchemaElement> elements = new HashMap<>();
		if (type.isPrimitive()) {
			// 基本类型不做处理
			return elements.values().toArray(new SchemaElement[] {});
		} else if (type.isInterface()) {
			// 类型为接口时
			if (recursion) {
				// 递归，先获取基类
				for (Class<?> item : type.getInterfaces()) {
					if (item.getName().startsWith("java.")) {
						// 基础类型不做处理
						continue;
					}
					for (SchemaElement element : this.getSerializedElements(item, recursion)) {
						elements.put(element.getName(), element);
					}
				}
			}
			for (Method method : type.getMethods()) {
				if (method.getParameterTypes().length != 1 || !method.getName().startsWith("set")) {
					continue;
				}
				String elementName = method.getName().replace("set", "");
				Class<?> elementType = method.getParameterTypes()[0];
				elements.put(elementName, new SchemaElement(elementName, elementType));
			}
		} else {
			// 类型是类
			if (recursion) {
				// 递归，先获取基类
				Class<?> superClass = type.getSuperclass();
				if (superClass != null && !superClass.equals(Object.class)) {
					for (SchemaElement element : this.getSerializedElements(superClass, recursion)) {
						elements.put(element.getName(), element);
					}
				}
			}
			// 取被标记的属性
			for (Method method : type.getMethods()) {
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
					if (xmlElement.type() != null
							&& !xmlElement.type().getName().startsWith(XmlElement.class.getName())) {
						elementType = xmlElement.type();
					}
				}
				if (elementName == null) {
					continue;
				}
				if (elementType == null) {
					continue;
				}
				elements.put(elementName, new SchemaElement(elementName, wrapperName, elementType));
			}
		}
		List<SchemaElement> values = new ArrayList<>(elements.values());
		Collections.sort(values);
		return values.toArray(new SchemaElement[] {});
	}

	protected class SchemaElement implements Comparable<SchemaElement> {

		public SchemaElement() {

		}

		public SchemaElement(String name, Class<?> type) {
			this.name = name;
			this.type = type;
		}

		public SchemaElement(String name, String wrapper, Class<?> type) {
			this(name, type);
			this.wrapper = wrapper;
		}

		private String name;

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		private String wrapper;

		public final String getWrapper() {
			return wrapper;
		}

		public final void setWrapper(String wrapper) {
			this.wrapper = wrapper;
		}

		private Class<?> type;

		public final Class<?> getType() {
			return type;
		}

		public final void setType(Class<?> type) {
			this.type = type;
		}

		@Override
		public int compareTo(SchemaElement o) {
			return this.getName().compareTo(o.getName());
		}

	}
}
