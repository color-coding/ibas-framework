package org.colorcoding.ibas.bobas.serialization.jersey;

import org.colorcoding.ibas.bobas.serialization.ISerializer;

public class SerializerManager extends org.colorcoding.ibas.bobas.serialization.SerializerManager {

	@Override
	public ISerializer<?> create(String sign) {
		if (sign != null && sign.equalsIgnoreCase(TYPE_JSON)) {
			// 使用json方式
			return new SerializerJson();
		}
		return super.create(sign);
	}

}
