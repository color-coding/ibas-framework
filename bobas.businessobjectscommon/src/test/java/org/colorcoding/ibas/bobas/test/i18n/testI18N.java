package org.colorcoding.ibas.bobas.test.i18n;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.i18n.I18N;

import junit.framework.TestCase;

public class testI18N extends TestCase {

	public void testMessage() throws Exception {
		System.out.println(I18N.prop("msg_bobas_create_db_adapter_falid"));
		System.out.println(I18N.prop("msg_bobas_data_ownership_fetch_count", 2));

	}

	public void testMessageEN() throws Exception {
		MyConfiguration.addConfigValue(MyConfiguration.CONFIG_ITEM_LANGUAGE_CODE, "en-US");
		System.out.println(I18N.prop("msg_bobas_create_db_adapter_falid"));
		System.out.println(I18N.prop("msg_bobas_data_ownership_fetch_count", 2));

	}
}
