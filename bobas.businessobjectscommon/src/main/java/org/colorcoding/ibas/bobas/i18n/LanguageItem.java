package org.colorcoding.ibas.bobas.i18n;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

@XmlAccessorType(XmlAccessType.NONE)
public class LanguageItem {

	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private List<LanguageItemContent> languageItemContents;

	protected List<LanguageItemContent> getLanguageItemContents() {
		if (languageItemContents == null) {
			languageItemContents = new ArrayList<LanguageItemContent>();
		}
		return languageItemContents;
	}

	public String getContent(String langcode) {
		if (langcode != null && !langcode.isEmpty()) {
			for (LanguageItemContent item : this.getLanguageItemContents()) {
				if (langcode.equals(item.getLanguageCode())) {
					return item.getContent();
				}
			}
		}
		return this.getContent();
	}

	/**
	 * 默认值
	 * 
	 * @return
	 */
	public String getContent() {
		LanguageItemContent content = this.getLanguageItemContents().firstOrDefault();
		if (content != null) {
			return content.getContent();
		}
		return String.format("[%s]", this.getKey());
	}

	/**
	 * 添加语言类型以及对应的翻译文本
	 * 
	 * @param langcode
	 * @param content
	 */
	public void addContent(String langcode, String content) {
		if (langcode == null || langcode.isEmpty())
			return;
		if (content == null || langcode.isEmpty())
			return;
		// 添加或者更新值
		LanguageItemContent itemContent = null;
		for (LanguageItemContent item : this.getLanguageItemContents()) {
			if (langcode.equals(item.getLanguageCode())) {
				itemContent = item;
				break;
			}
		}
		if (itemContent == null) {
			this.getLanguageItemContents().add(new LanguageItemContent(langcode, content));
		} else {
			itemContent.setContent(content);
		}
	}

	public Iterator<LanguageItemContent> iterator() {
		return this.getLanguageItemContents().iterator();
	}

}
