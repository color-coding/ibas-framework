package org.colorcoding.ibas.bobas.serialization;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * 序列化者
 * 
 * @author Niuren.Zhu
 */
public interface ISerializer {

	/**
	 * 深度克隆对象
	 * 
	 * @param object 克隆对象
	 * @param types  已知类型
	 * @return 新对象实例
	 */
	<T> T clone(T object, Class<?>... types) throws SerializationException;

	/**
	 * 序列化
	 * 
	 * @param object       目标
	 * @param outputStream 输出流
	 * @param types        已知类型
	 * @throws SerializationException
	 */
	void serialize(Object object, OutputStream outputStream, Class<?>... types) throws SerializationException;

	/**
	 * 序列化
	 * 
	 * @param object       目标
	 * @param outputStream 输出流
	 * @param formated     是否格式化
	 * @param types        已知类型
	 * @throws SerializationException
	 */
	void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types)
			throws SerializationException;

	/**
	 * 获取schema
	 * 
	 * @param type         目标类型
	 * @param outputStream 输出目标
	 * @throws SerializationException
	 */
	void schema(Class<?> type, OutputStream outputStream) throws SerializationException;

	/**
	 * 验证数据
	 * 
	 * @param type 目标类型
	 * @param data 数据
	 * @throws SAXException
	 */
	void validate(Class<?> type, String data) throws ValidateException;

	/**
	 * 验证数据
	 * 
	 * @param type 目标类型
	 * @param data 数据
	 * @throws SAXException
	 */
	void validate(Class<?> type, InputStream data) throws ValidateException;

	/**
	 * 反序列化
	 * 
	 * @param data  数据
	 * @param types 其他已知类型
	 * @return 新对象实例
	 */
	<T> T deserialize(String data, Class<?>... types) throws SerializationException;

	/**
	 * 反序列化
	 * 
	 * @param inputStream 数据
	 * @param types       其他已知类型
	 * @return 新对象实例
	 */
	<T> T deserialize(InputStream inputStream, Class<?>... types) throws SerializationException;

	/**
	 * 反序列化
	 * 
	 * @param inputSource 数据
	 * @param types       其他已知类型
	 * @return 新对象实例
	 */
	<T> T deserialize(InputSource inputSource, Class<?>... types) throws SerializationException;

	/**
	 * 反序列化
	 * 
	 * @param reader 数据
	 * @param types  其他已知类型
	 * @return 新对象实例
	 */
	<T> T deserialize(Reader reader, Class<?>... types) throws SerializationException;
}
