package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.i18n.I18N;

public class SerializerManager {

	/**
	 * 输出字符串类型，XML
	 */
	public final static String TYPE_XML = SerializerFactory.TYPE_XML;
	/**
	 * 输出化字符串类型，JSON
	 */
	public final static String TYPE_JSON = SerializerFactory.TYPE_JSON;

	public final ISerializer create() {
		return this.create(null);
	}

	public ISerializer create(String sign) {
		if (TYPE_XML.equalsIgnoreCase(sign) || sign == null || sign.isEmpty()) {
			// 默认使用xml方式
			return new SerializerXml();
		}
		throw new SerializationException(I18N.prop("msg_bobas_not_support_serialize_type", sign));
	}

}
