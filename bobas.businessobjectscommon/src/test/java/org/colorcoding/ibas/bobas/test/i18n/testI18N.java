package org.colorcoding.ibas.bobas.test.i18n;

import org.colorcoding.ibas.bobas.core.ObjectCloner;
import org.colorcoding.ibas.bobas.i18n.ILanguageItem;
import org.colorcoding.ibas.bobas.i18n.ILanguageItemContent;
import org.colorcoding.ibas.bobas.i18n.ILanguageItems;
import org.colorcoding.ibas.bobas.i18n.LanguageItems;
import org.colorcoding.ibas.bobas.i18n.i18n;

import junit.framework.TestCase;

public class testI18N extends TestCase {

	public testI18N() {
		super();
	}

	public void testMessage() {

		System.out.println(i18n.prop("msg_bobas_create_db_adapter_falid"));
		System.out.println(i18n.prop("msg_bobas_data_ownership_fetch_count", 2));

		i18n.readResources(
				"E:\\WorkTemp\\ibcp.trainingtesting\\ibcp.trainingtesting.service\\src\\main\\resources\\i18n");
		System.out.println(i18n.prop("msg_bobas_create_db_adapter_falid"));
		System.out.println(i18n.prop("msg_bobas_data_ownership_fetch_count", 2));
		System.out.println(i18n.prop("msg_tt_func_name_materials"));
		System.out.println(i18n.prop("msg_tt_app_ls_name_materials", 2));
		i18n.getInstance().setLanguageCode("en-US");
		System.out.println(i18n.prop("msg_tt_func_name_materials"));
		System.out.println(i18n.prop("msg_tt_app_ls_name_materials", 2));

	}

	public void testForSerialize() {

		ILanguageItems items = new LanguageItems();
		ILanguageItem item = items.create();
		item.setKey("MSG_LIHAO");
		ILanguageItemContent itemContent = item.create();
		itemContent.setLanguageCode("zh-CN");
		itemContent.setContent("你好");
		item.addContent("en-US", "Hello");

		ILanguageItem item1 = items.create();
		item1.setKey("MSG_LIHAO");
		ILanguageItemContent itemContent1 = item1.create();
		itemContent1.setLanguageCode("zh-CN");
		itemContent1.setContent("你好");
		item1.addContent("en-US", "Hello");

		System.out.println(ObjectCloner.toXmlString(items, true));

	}

}
