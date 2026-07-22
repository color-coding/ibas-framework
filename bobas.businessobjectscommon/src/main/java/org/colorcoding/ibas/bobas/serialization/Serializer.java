package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Bytes;

/**
 * 序列化对象
 * 
 * 继承实现时，注意序列化和反序列化监听
 */
public abstract class Serializer implements ISerializer {

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
