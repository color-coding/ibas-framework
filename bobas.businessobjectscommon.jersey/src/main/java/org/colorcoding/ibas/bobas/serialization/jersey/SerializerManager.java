package org.colorcoding.ibas.bobas.serialization.jersey;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.serialization.ISerializer;

public class SerializerManager extends org.colorcoding.ibas.bobas.serialization.SerializerManager {

	public static final String CONFIG_ITEM_SERIALIZATION_INCLUDE_JSON_ROOT = "IncludeJsonRoot";

	@Override
	public ISerializer<?> create(String sign) {
		if (sign != null && sign.equalsIgnoreCase(TYPE_JSON)) {
			// 使用json方式
			if (MyConfiguration.getConfigValue(CONFIG_ITEM_SERIALIZATION_INCLUDE_JSON_ROOT, false)) {
				// 包含对象ROOT
				return new SerializerJson();
			} else {
				// 不包含对象ROOT
				return new SerializerJsonNoRoot();
			}
		}
		return super.create(sign);
	}

}
