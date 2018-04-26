package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.xml.sax.InputSource;

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
		Class<?>[] knownTypes = new Class[types.length + 1];
		knownTypes[0] = object.getClass();
		for (int i = 0; i < types.length; i++) {
			knownTypes[i + 1] = types[i];
		}
		return (T) this.deserialize(inputStream, knownTypes);
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
	public Object deserialize(InputStream inputStream, Class<?>... types) throws SerializationException {
		try {
			return this.deserialize(new InputSource(inputStream), types);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public abstract void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types);

	@Override
	public abstract Object deserialize(InputSource inputSource, Class<?>... types) throws SerializationException;

	@Override
	public abstract void validate(S schema, InputStream data) throws ValidateException;

	@Override
	public abstract S getSchema(Class<?> type) throws SerializationException;

}
