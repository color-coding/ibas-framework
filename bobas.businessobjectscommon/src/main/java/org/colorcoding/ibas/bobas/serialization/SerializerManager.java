package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class SerializerManager {

	/** 序列化类型：XML */
	public final static String TYPE_XML = SerializationFactory.TYPE_XML;
	/** 序列化类型：JSON */
	public final static String TYPE_JSON = SerializationFactory.TYPE_JSON;

	/**
	 * 创建序列化器，默认使用XML
	 *
	 * @return 序列化器实例
	 */
	public final ISerializer create() {
		return this.create(null);
	}

	/**
	 * 创建序列化器
	 *
	 * @param sign 类型标识，为空或"xml"时返回XML序列化器；其他类型抛出异常
	 * @return 序列化器实例
	 */
	public ISerializer create(String sign) {
		if (TYPE_XML.equalsIgnoreCase(sign) || Strings.isNullOrEmpty(sign)) {
			// 默认使用xml方式
			return new SerializerXml();
		}
		throw new SerializationException(I18N.prop("msg_bobas_not_support_serialize_type", sign));
	}

}
