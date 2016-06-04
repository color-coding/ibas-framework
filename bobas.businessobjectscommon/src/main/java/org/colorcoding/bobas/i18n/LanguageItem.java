package org.colorcoding.bobas.i18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConfiguration;
import org.colorcoding.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "LanguageItem", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
@XmlRootElement(name = "LanguageItem", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
public class LanguageItem implements ILanguageItem {
	/**
	 * 获取翻译文本的key 值
	 */
	private String key;

	@Override
	@XmlElement(name = "key")
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	private List<ILanguageItemContent> languageItemContents;

	@XmlElementWrapper(name = "LanguageItemContents")
	@XmlElement(name = "LanguageItemContent", type = LanguageItemContent.class)
	public List<ILanguageItemContent> getLanguageItemContents() {
		if (languageItemContents == null) {
			languageItemContents = new ArrayList<ILanguageItemContent>();
		}
		return languageItemContents;
	}

	@Override
	public ILanguageItemContent create() {
		ILanguageItemContent itemContent = new LanguageItemContent();
		getLanguageItemContents().add(itemContent);
		return itemContent;
	}

	/**
	 * 获取最后添加进来的翻译文本内容
	 * 
	 * @return 若存在返回结果，不存在返回空
	 */
	@Override
	public String getContent() {
		String langCode = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LANGUAGE_CODE);
		if (langCode == null || langCode.equals("")) {
			Locale locale = Locale.getDefault();
			langCode = String.format("%s-%s", locale.getLanguage(), locale.getCountry());
		}
		return this.getContent(langCode);
	}

	/**
	 * 根据语言类型 获取翻译文本内容
	 * 
	 * @param langcode
	 *            语言类型
	 * 
	 * @return 若存在返回结果，不存在返回空
	 */
	@Override
	public String getContent(String langcode) {
		String result = "";
		ILanguageItemContent itemContent = this.getItemContent(langcode);
		if (itemContent == null)
			return result;
		return itemContent.getContent();
	}

	/**
	 * 根据语言类型获取翻译文本内容实体
	 * 
	 * @param langcode
	 *            语言类型
	 * @return 翻译文本
	 */
	private ILanguageItemContent getItemContent(String langcode) {
		if (langcode == null || langcode.equals(""))
			return null;
		if (languageItemContents == null)
			return null;
		ILanguageItemContent itemContent = null;
		for (ILanguageItemContent languageItemContent : languageItemContents) {
			if (langcode.equals(languageItemContent.getLanguageCode())) {
				itemContent = languageItemContent;
				break;
			}
		}
		return itemContent;
	}

	/**
	 * 添加语言类型以及对应的翻译文本
	 * 
	 * @param langcode
	 * @param content
	 */
	@Override
	public void addContent(String langcode, String content) {
		if (langcode == null || langcode.equals(""))
			return;
		if (content == null || langcode.equals(""))
			return;
		// 添加或者更新值
		ILanguageItemContent itemContent = this.getItemContent(langcode);
		if (itemContent == null) {
			LanguageItemContent instance = new LanguageItemContent();
			instance.setContent(content);
			instance.setLanguageCode(langcode);
			this.getLanguageItemContents().add(instance);
		} else {
			itemContent.setContent(content);
		}
	}

	@Override
	public Iterator<ILanguageItemContent> iterator() {
		return this.getLanguageItemContents().iterator();
	}

}
