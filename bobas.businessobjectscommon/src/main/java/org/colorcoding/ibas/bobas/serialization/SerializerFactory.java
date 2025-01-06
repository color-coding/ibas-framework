package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 序列化者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class SerializerFactory {

	private SerializerFactory() {
	}

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
	public static final ISerializer<?> create() {
		return create(null);
	}

	/**
	 * 创建序列化者
	 * 
	 * @param type 类型
	 * @return
	 */
	public static ISerializer<?> create(String type) {
		if (TYPE_XML.equalsIgnoreCase(type) || Strings.isNullOrEmpty(type)) {
			// 默认使用xml方式
			return new SerializerXml();
		}
		throw new SerializationException(I18N.prop("msg_bobas_not_support_serialize_type", type));
	}
}
