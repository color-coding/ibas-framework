package org.colorcoding.ibas.bobas.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.i18n.LanguageItem;
import org.colorcoding.ibas.bobas.i18n.LanguageItemManager;

import junit.framework.TestCase;

/**
 * 多语言（i18n）功能测试
 *
 * 测试范围：
 * 1. classpath 资源加载（src/main/resources/i18n 已存在的 locale.bobas.properties）
 * 2. 同时加载文件夹与 jar 包资源（依赖运行时类路径的常规行为）
 * 3. 语言切换（默认/英文）
 * 4. 未知 key 返回 [key]
 * 5. 带参数格式化
 * 6. 同 key 多语言合并
 * 7. extractLanguageCode 文件名解析
 * 8. 工作目录补充加载外部 properties
 */
public class TestI18N extends TestCase {

	/**
	 * 验证 classpath 上 i18n 目录中的资源已被加载，
	 * 并且 ContextClassLoader.getResources("i18n") 同时能找到文件夹与 jar 中的资源。
	 */
	public void testLoadFromClasspath() {
		LanguageItemManager manager = I18N.getInstance();
		assertNotNull(manager);
		// 框架内置的 key 必须能取到（来自 locale.bobas.properties）
		String value = I18N.prop("msg_bobas_business_rule_required");
		assertNotNull(value);
		assertFalse("classpath 内置 i18n 资源未正确加载", value.startsWith("["));
	}

	/**
	 * 切换语言到 en-US，验证英文文本加载（来自 locale.bobas_en-US.properties）。
	 */
	public void testSwitchLanguage() {
		LanguageItemManager manager = I18N.getInstance();
		String previous = manager.getLanguageCode();
		try {
			manager.setLanguageCode("en-US");
			String en = I18N.prop("msg_bobas_business_rule_required");
			assertNotNull(en);
			assertFalse(en.startsWith("["));
			// 英文版固定内容
			assertEquals("Required value validation", en);

			manager.setLanguageCode("zh-CN");
			String def = I18N.prop("msg_bobas_business_rule_required");
			// 默认（无语言后缀）文件应能作为回退命中
			assertNotNull(def);
			assertFalse(def.startsWith("["));
		} finally {
			manager.setLanguageCode(previous);
		}
	}

	/**
	 * 未知 key 应返回 [key]。
	 */
	public void testUnknownKey() {
		String value = I18N.prop("no_such_key_xxx_yyy_zzz");
		assertEquals("[no_such_key_xxx_yyy_zzz]", value);
	}

	/**
	 * 验证带参数格式化。
	 */
	public void testFormatArguments() {
		// msg_bobas_business_rule_required_error = [%s]没有输入值
		String value = I18N.prop("msg_bobas_business_rule_required_error", "Name");
		assertNotNull(value);
		assertTrue("格式化参数未替换：" + value, value.contains("Name"));
		assertFalse(value.contains("%s"));
	}

	/**
	 * 验证同 key 在不同语言文件中合并（默认 + en-US 两种语言均能取到）。
	 */
	public void testMergeMultiLanguage() {
		LanguageItemManager manager = I18N.getInstance();
		String previous = manager.getLanguageCode();
		try {
			manager.setLanguageCode("en-US");
			String en = manager.getContent("msg_bobas_business_rule_required");
			manager.setLanguageCode("zh-CN");
			String def = manager.getContent("msg_bobas_business_rule_required");
			assertNotNull(en);
			assertNotNull(def);
			assertFalse(en.startsWith("["));
			assertFalse(def.startsWith("["));
			assertFalse("默认语言与英文内容应不同", en.equals(def));
		} finally {
			manager.setLanguageCode(previous);
		}
	}

	/**
	 * 验证从工作目录加载额外的 properties 资源。
	 */
	public void testLoadFromWorkFolder() throws IOException {
		// 写入一个临时文件到 workFolder/i18n，验证能加载
		String workFolder = MyConfiguration.getWorkFolder();
		File i18nDir = new File(workFolder, "i18n");
		if (!i18nDir.exists()) {
			i18nDir.mkdirs();
		}
		File tmpDefault = new File(i18nDir, "locale.test_i18n.properties");
		File tmpEn = new File(i18nDir, "locale.test_i18n_en-US.properties");
		try {
			try (Writer w = new OutputStreamWriter(new FileOutputStream(tmpDefault), StandardCharsets.UTF_8)) {
				w.write("test_i18n_key_for_unit = 单元测试值\n");
			}
			try (Writer w = new OutputStreamWriter(new FileOutputStream(tmpEn), StandardCharsets.UTF_8)) {
				w.write("test_i18n_key_for_unit = unit-test-value\n");
			}
			// 触发重新加载
			LanguageItemManager manager = I18N.getInstance();
			manager.readResources(i18nDir.getPath());

			String previous = manager.getLanguageCode();
			try {
				manager.setLanguageCode("en-US");
				assertEquals("unit-test-value", I18N.prop("test_i18n_key_for_unit"));
				manager.setLanguageCode("zh-CN");
				assertEquals("单元测试值", I18N.prop("test_i18n_key_for_unit"));
			} finally {
				manager.setLanguageCode(previous);
			}
		} finally {
			if (tmpDefault.exists()) {
				tmpDefault.delete();
			}
			if (tmpEn.exists()) {
				tmpEn.delete();
			}
		}
	}

	/**
	 * 验证文件名解析语言编码规则。
	 */
	public void testExtractLanguageCode() {
		ExposedManager manager = new ExposedManager();
		assertEquals("", manager.callExtractLanguageCode("locale.bobas.properties"));
		assertEquals("en-US", manager.callExtractLanguageCode("locale.bobas_en-US.properties"));
		assertEquals("zh-CN", manager.callExtractLanguageCode("my_module_zh-CN.properties"));
		assertEquals("", manager.callExtractLanguageCode(""));
		assertEquals("", manager.callExtractLanguageCode(null));
	}

	/**
	 * 验证 LanguageItem.getContent(langcode) 未匹配时回退到默认。
	 */
	public void testLanguageItemFallback() {
		LanguageItem item = new LanguageItem();
		item.setKey("k1");
		item.addContent("", "默认");
		item.addContent("en-US", "english");

		assertEquals("english", item.getContent("en-US"));
		assertEquals("默认", item.getContent("zh-CN"));
		assertEquals("默认", item.getContent(null));
		assertEquals("默认", item.getContent(""));

		// 仅有英文，请求中文回退到英文
		LanguageItem item2 = new LanguageItem();
		item2.setKey("k2");
		item2.addContent("en-US", "english-only");
		assertEquals("english-only", item2.getContent("zh-CN"));

		// 无任何内容
		LanguageItem item3 = new LanguageItem();
		item3.setKey("k3");
		String value = item3.getContent("en-US");
		assertTrue(Strings.isNullOrEmpty(value) || value.startsWith("["));
	}

	/**
	 * 暴露 protected 方法以便测试。
	 */
	private static class ExposedManager extends LanguageItemManager {
		String callExtractLanguageCode(String fileName) {
			return this.extractLanguageCode(fileName);
		}
	}

}
