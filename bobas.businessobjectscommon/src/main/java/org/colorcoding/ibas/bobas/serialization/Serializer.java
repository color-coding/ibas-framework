package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Bytes;

/**
 * 序列化对象
 * 
 * 继承实现时，注意序列化和反序列化监听
 */
public abstract class Serializer implements ISerializer {

	protected final boolean isCollectionType(Class<?> type) {
		return type != null && Collection.class.isAssignableFrom(type);
	}

	/** 解析集合元素类型；没有明确类型或唯一的 @XmlSeeAlso 时拒绝继续。 */
	protected final Class<?> resolveCollectionItemType(Class<?>... types) {
		if (types == null || types.length == 0 || types[0] == null) {
			throw new SerializationException("Root collection type is required.");
		}
		if (!this.isCollectionType(types[0])) {
			return types[0];
		}
		if (types.length > 1 && types[1] != null) {
			return types[1];
		}
		Class<?> type = types[0];
		while (type != null && type != Object.class) {
			XmlSeeAlso seeAlso = type.getAnnotation(XmlSeeAlso.class);
			if (seeAlso != null) {
				if (seeAlso.value().length != 1) {
					throw new SerializationException("Root collection requires exactly one element type: "
							+ types[0].getName());
				}
				return seeAlso.value()[0];
			}
			type = type.getSuperclass();
		}
		throw new SerializationException("Root collection requires an element type or @XmlSeeAlso: "
				+ types[0].getName());
	}

	@SuppressWarnings("unchecked")
	protected final Collection<Object> newCollection(Class<?> collectionType) {
		try {
			java.lang.reflect.Constructor<?> constructor = collectionType.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object instance = constructor.newInstance();
			if (instance instanceof Collection<?>) {
				return (Collection<Object>) instance;
			}
		} catch (Exception e) {
			throw new SerializationException("Cannot create collection: " + collectionType.getName(), e);
		}
		throw new SerializationException("Type is not a collection: " + collectionType.getName());
	}

	/** 返回 JAXB root/type 的 namespace 和名称，供 XML 和 JSON 根处理共用。 */
	protected final String[] resolveXmlInfo(Class<?> type) {
		XmlRootElement root = type.getAnnotation(XmlRootElement.class);
		if (root != null) {
			String name = "##default".equals(root.name()) ? type.getSimpleName() : root.name();
			String namespace = "##default".equals(root.namespace()) ? "" : root.namespace();
			return new String[] { namespace, name };
		}
		XmlType xmlType = type.getAnnotation(XmlType.class);
		String name = xmlType != null && !"##default".equals(xmlType.name())
				? xmlType.name() : type.getSimpleName();
		String namespace = xmlType != null && !"##default".equals(xmlType.namespace())
				? xmlType.namespace() : "";
		return new String[] { namespace, name };
	}

	/**
	 * 构建已知类型数组，将对象类型与额外类型合并
	 *
	 * @param object 目标对象
	 * @param types  额外已知类型
	 * @return 合并后的已知类型数组
	 */
	protected Class<?>[] buildKnownTypes(Object object, Class<?>... types) {
		Class<?>[] knownTypes = new Class[types.length + 1];
		knownTypes[0] = object.getClass();
		for (int i = 0; i < types.length; i++) {
			knownTypes[i + 1] = types[i];
		}
		return knownTypes;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T clone(T object, Class<?>... types) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(512)) {
			this.serialize(object, outputStream, false, types);
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
				return (T) this.deserialize(inputStream, this.buildKnownTypes(object, types));
			}
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, Class<?>... types) {
		this.serialize(object, outputStream,
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FORMATTED_OUTPUT, false), types);
	}

	@Override
	public <T> T deserialize(String data, Class<?>... types) {
		try (InputStream stream = new ByteArrayInputStream(Bytes.valueOf(data))) {
			return this.deserialize(stream, types);
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	public void validate(Class<?> type, String data) throws ValidateException {
		try (InputStream stream = new ByteArrayInputStream(Bytes.valueOf(data))) {
			this.validate(type, stream);
		} catch (IOException e) {
			throw new ValidateException(e.getMessage(), e);
		}

	}

}
