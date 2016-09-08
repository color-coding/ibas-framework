package org.colorcoding.ibas.bobas.i18n;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.util.ArrayList;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "LanguageItem", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
@XmlRootElement(name = "LanguageItem", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
public class LanguageItem implements ILanguageItem {

	private String key;

	@Override
	@XmlElement(name = "Key")
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	private ArrayList<ILanguageItemContent> languageItemContents;

	@XmlElementWrapper(name = "LanguageItemContents")
	@XmlElement(name = "LanguageItemContent", type = LanguageItemContent.class)
	protected ArrayList<ILanguageItemContent> getLanguageItemContents() {
		if (languageItemContents == null) {
			languageItemContents = new ArrayList<ILanguageItemContent>();
		}
		return languageItemContents;
	}

	@Override
	public String getContent(String langcode) {
		if (langcode != null && !langcode.isEmpty()) {
			for (ILanguageItemContent item : this.getLanguageItemContents()) {
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
		ILanguageItemContent content = this.getLanguageItemContents().firstOrDefault();
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
	@Override
	public void addContent(String langcode, String content) {
		if (langcode == null || langcode.isEmpty())
			return;
		if (content == null || langcode.isEmpty())
			return;
		// 添加或者更新值
		ILanguageItemContent itemContent = null;
		for (ILanguageItemContent item : this.getLanguageItemContents()) {
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

	@Override
	public Iterator<ILanguageItemContent> iterator() {
		return this.getLanguageItemContents().iterator();
	}

}
