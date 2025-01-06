package org.colorcoding.ibas.bobas.serialization;

import java.io.InputStream;
import java.io.OutputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * 序列化者
 * 
 * @author Niuren.Zhu
 *
 * @param <S>
 *            schema类型
 */
public interface ISerializer<S> {

	/**
	 * 深度克隆对象
	 * 
	 * @param object
	 *            克隆对象
	 * @param types
	 *            已知类型
	 * @return 新对象实例
	 */
	<T> T clone(T object, Class<?>... types) throws SerializationException;

	/**
	 * 序列化
	 * 
	 * @param object
	 *            目标
	 * @param outputStream
	 *            输出流
	 * @param types
	 *            已知类型
	 * @throws SerializationException
	 */
	void serialize(Object object, OutputStream outputStream, Class<?>... types) throws SerializationException;

	/**
	 * 序列化
	 * 
	 * @param object
	 *            目标
	 * @param outputStream
	 *            输出流
	 * @param formated
	 *            是否格式化
	 * @param types
	 *            已知类型
	 * @throws SerializationException
	 */
	void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types)
			throws SerializationException;

	/**
	 * 获取schema
	 * 
	 * @param type
	 *            目标类型
	 * @return
	 * @throws SerializationException
	 */
	S getSchema(Class<?> type) throws SerializationException;

	/**
	 * 获取schema
	 * 
	 * @param type
	 *            目标类型
	 * @param outputStream
	 *            输出目标
	 * @throws SerializationException
	 */
	void getSchema(Class<?> type, OutputStream outputStream) throws SerializationException;

	/**
	 * 验证数据
	 * 
	 * @param schema
	 *            数据架构
	 * @param data
	 *            数据
	 * @throws SAXException
	 */
	void validate(S schema, InputStream data) throws ValidateException;

	/**
	 * 验证数据
	 * 
	 * @param schema
	 *            数据架构
	 * @param data
	 *            数据
	 * @throws SAXException
	 */
	void validate(S schema, String data) throws ValidateException;

	/**
	 * 验证数据
	 * 
	 * @param type
	 *            目标类型
	 * @param data
	 *            数据
	 * @throws SAXException
	 */
	void validate(Class<?> type, String data) throws ValidateException;

	/**
	 * 验证数据
	 * 
	 * @param type
	 *            目标类型
	 * @param data
	 *            数据
	 * @throws SAXException
	 */
	void validate(Class<?> type, InputStream data) throws ValidateException;

	/**
	 * 反序列化
	 * 
	 * @param data
	 *            数据
	 * @param types
	 *            其他已知类型
	 * @return 新对象实例
	 */
	Object deserialize(String data, Class<?>... types) throws SerializationException;

	/**
	 * 反序列化
	 * 
	 * @param inputStream
	 *            数据
	 * @param types
	 *            其他已知类型
	 * @return 新对象实例
	 */
	Object deserialize(InputStream inputStream, Class<?>... types) throws SerializationException;

	/**
	 * 反序列化
	 * 
	 * @param inputSource
	 *            数据
	 * @param types
	 *            其他已知类型
	 * @return 新对象实例
	 */
	Object deserialize(InputSource inputSource, Class<?>... types) throws SerializationException;
}
