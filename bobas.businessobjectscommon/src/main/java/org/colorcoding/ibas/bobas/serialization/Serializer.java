package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Writer;

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

}
