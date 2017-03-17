package org.colorcoding.ibas.bobas.serialization;

/**
 * 序列化者管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISerializerManager {
	/**
	 * 输出字符串类型，XML
	 */
	public final static String TYPE_XML = "xml";
	/**
	 * 输出化字符串类型，JSON
	 */
	public final static String TYPE_JSON = "json";

	/**
	 * 创建序列化者
	 * 
	 * @return
	 */
	ISerializer<?> create();

	/**
	 * 创建序列化者
	 * 
	 * @param sign
	 *            类型
	 * @return
	 */
	ISerializer<?> create(String sign);
}
