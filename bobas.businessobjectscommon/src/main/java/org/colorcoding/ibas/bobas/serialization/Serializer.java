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
public abstract class Serializer implements ISerializer {

	@Override
	@SuppressWarnings("unchecked")
	public <T> T clone(T object, Class<?>... types) throws SerializationException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			this.serialize(object, outputStream, false, types);
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
				Class<?>[] knownTypes = new Class[types.length + 1];
				knownTypes[0] = object.getClass();
				for (int i = 0; i < types.length; i++) {
					knownTypes[i + 1] = types[i];
				}
				return (T) this.deserialize(inputStream, knownTypes);
			}
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, Class<?>... types) throws SerializationException {
		this.serialize(object, outputStream,
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FORMATTED_OUTPUT, false), types);
	}

	@Override
	public <T> T deserialize(String data, Class<?>... types) throws SerializationException {
		try (InputStream stream = new ByteArrayInputStream(data.getBytes())) {
			return this.deserialize(stream, types);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public <T> T deserialize(InputStream inputStream, Class<?>... types) throws SerializationException {
		return this.deserialize(new InputSource(inputStream), types);
	}

	@Override
	public void validate(Class<?> type, String data) throws ValidateException {
		try (InputStream stream = new ByteArrayInputStream(data.getBytes())) {
			this.validate(type, stream);
		} catch (IOException e) {
			throw new ValidateException(e);
		}

	}

	@Override
	public abstract void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types);

	@Override
	public abstract <T> T deserialize(InputSource inputSource, Class<?>... types) throws SerializationException;

}
