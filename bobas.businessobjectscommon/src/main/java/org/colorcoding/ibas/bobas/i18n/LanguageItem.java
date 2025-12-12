package org.colorcoding.ibas.bobas.i18n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.KeyText;
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

	private List<KeyText> contents;

	protected List<KeyText> getContents() {
		if (contents == null) {
			contents = new ArrayList<>(4);
		}
		return contents;
	}

	public String getContent(String langcode) {
		if (!Strings.isNullOrEmpty(langcode)) {
			for (KeyText item : this.getContents()) {
				if (langcode.equalsIgnoreCase(item.getKey())) {
					return item.getText();
				}
			}
		}
		return this.getContent();
	}

	public String getContent() {
		for (KeyText item : this.getContents()) {
			return item.getText();
		}
		return Strings.format("[%s]", this.getKey());
	}

	public void addContent(String langcode, String content) {
		if (Strings.isNullOrEmpty(langcode))
			return;
		if (content == null)
			return;
		// 添加或者更新值
		KeyText itemContent = null;
		for (KeyText item : this.getContents()) {
			if (langcode.equalsIgnoreCase(item.getKey())) {
				itemContent = item;
				break;
			}
		}
		if (itemContent == null) {
			this.getContents().add(new KeyText(langcode, content));
		} else {
			itemContent.setText(content);
		}
	}

}
