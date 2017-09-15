package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.i18n.I18N;

public class SerializerManager implements ISerializerManager {

	@Override
	public final ISerializer<?> create() {
		return this.create(null);
	}

	@Override
	public ISerializer<?> create(String sign) {
		if (sign == null || sign.isEmpty() || sign.equalsIgnoreCase(TYPE_XML)) {
			// 默认使用xml方式
			return new SerializerXml();
		}
		throw new SerializationException(I18N.prop("msg_bobas_not_support_serialize_type", sign));
	}

}
