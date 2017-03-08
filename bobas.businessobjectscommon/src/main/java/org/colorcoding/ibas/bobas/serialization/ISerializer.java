package org.colorcoding.ibas.bobas.serialization;

import java.io.File;
import java.io.Writer;

/**
 * 序列化者
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISerializer {

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
	 * 反序列化
	 * 
	 * @param data
	 *            数据
	 * @param type
	 *            目标类型
	 * @param types
	 *            已知类型
	 * @return 新对象实例
	 */
	<T> T deserialize(String data, Class<T> type, Class<?>... types) throws SerializationException;

	/**
	 * 反序列化
	 * 
	 * @param file
	 *            文件
	 * @param type
	 *            目标类型
	 * @param types
	 *            已知类型
	 * @return 新对象实例
	 */
	<T> T deserialize(File file, Class<T> type, Class<?>... types) throws SerializationException;

	/**
	 * 序列化
	 * 
	 * @param object
	 *            目标数据
	 * @param writer
	 *            输入目标
	 * @param formated
	 *            是否格式化
	 * @param types
	 *            已知类型
	 */
	void serialize(Object object, Writer writer, boolean formated, Class<?>... types) throws SerializationException;

	/**
	 * 序列化
	 * 
	 * @param object
	 *            目标数据
	 * @param writer
	 *            输入目标
	 * @param formated
	 *            是否格式化
	 * @param types
	 *            已知类型
	 */
	void serialize(Object object, Writer writer, Class<?>... types) throws SerializationException;
}
