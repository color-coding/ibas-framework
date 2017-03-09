package org.colorcoding.ibas.bobas.serialization;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.colorcoding.ibas.bobas.MyConfiguration;

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
		boolean formatted = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FORMATTED_OUTPUT, false);
		this.serialize(object, writer, formatted, types);
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

	private class SchemaElements extends ArrayList<SchemaElement> {
		private static final long serialVersionUID = 5525571009797394981L;

		public void add(SchemaElement[] items) {
			for (SchemaElement schemaElement : items) {
				this.add(schemaElement);
			}
		}

		public void add(Collection<SchemaElement> items) {
			for (SchemaElement schemaElement : items) {
				this.add(schemaElement);
			}
		}

		@Override
		public boolean add(SchemaElement e) {
			for (int i = 0; i < this.size(); i++) {
				SchemaElement item = this.get(i);
				if (item.getName().equals(e.getName())) {
					this.remove(i);
				}
			}
			return super.add(e);
		}

		public SchemaElement[] toArray() {
			return this.toArray(new SchemaElement[] {});
		}
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
		public int compareTo(SchemaElement target) {
			String sName = this.getWrapper();
			if (sName == null || sName.isEmpty()) {
				sName = this.getName();
			}
			String tName = target.getWrapper();
			if (tName == null || tName.isEmpty()) {
				tName = target.getName();
			}
			if (Character.isUpperCase(tName.charAt(0)) == Character.isUpperCase(sName.charAt(0))) {
				return sName.compareTo(tName);
			} else {
				if (Character.isUpperCase(tName.charAt(0))) {
					return -1;
				}
				return 1;
			}
		}

	}

	@Override
	public void validate(Class<?> type, String data) throws ValidateException {
		this.validate(type, new BufferedReader(new StringReader(data)));
	}

	@Override
	public void validate(Class<?> type, File file) throws ValidateException {
		try {
			this.validate(type, new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new ValidateException(e);
		}
	}

	public abstract void validate(Class<?> type, Reader reader) throws ValidateException;
}
