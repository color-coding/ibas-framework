package org.colorcoding.ibas.bobas.serialization.jersey;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.serialization.ISerializer;

public class SerializerManager extends org.colorcoding.ibas.bobas.serialization.SerializerManager {

	public static final String CONFIG_ITEM_SERIALIZATION_INCLUDE_JSON_ROOT = "IncludeJsonRoot";
	public static final String TYPE_JSON_NO_ROOT = TYPE_JSON + "_no_root";
	public static final String TYPE_JSON_HAS_ROOT = TYPE_JSON + "_has_root";

	@Override
	public ISerializer<?> create(String sign) {
		if (Strings.isWith(sign, TYPE_JSON, null)) {
			SerializerJson serializerJson = new SerializerJson();
			serializerJson.setIncludeJsonRoot(
					MyConfiguration.getConfigValue(CONFIG_ITEM_SERIALIZATION_INCLUDE_JSON_ROOT, false));
			return serializerJson;
		} else {
			return super.create(sign);
		}
	}

}
