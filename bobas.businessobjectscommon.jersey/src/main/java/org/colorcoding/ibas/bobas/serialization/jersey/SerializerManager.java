package org.colorcoding.ibas.bobas.serialization.jersey;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.serialization.ISerializer;

public class SerializerManager extends org.colorcoding.ibas.bobas.serialization.SerializerManager {

	public static final String CONFIG_ITEM_SERIALIZATION_INCLUDE_JSON_ROOT = "IncludeJsonRoot";
	public static final String TYPE_JSON_NO_ROOT = TYPE_JSON + "_no_root";

	@Override
	public ISerializer<?> create(String sign) {
		if (TYPE_JSON_NO_ROOT.equalsIgnoreCase(sign)) {
			// 不包含对象ROOT
			return new SerializerJsonNoRoot();
		} else if (TYPE_JSON.equalsIgnoreCase(sign)) {
			// 使用json方式
			if (MyConfiguration.getConfigValue(CONFIG_ITEM_SERIALIZATION_INCLUDE_JSON_ROOT, true)) {
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
