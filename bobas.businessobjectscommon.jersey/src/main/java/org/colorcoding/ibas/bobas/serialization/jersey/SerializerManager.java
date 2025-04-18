package org.colorcoding.ibas.bobas.serialization.jersey;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.serialization.ISerializer;

public class SerializerManager extends org.colorcoding.ibas.bobas.serialization.SerializerManager {

	public static final String CONFIG_ITEM_SERIALIZATION_INCLUDE_JSON_ROOT = "IncludeJsonRoot";

	@Override
	public ISerializer create(String sign) {
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
