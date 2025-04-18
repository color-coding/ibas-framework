package org.colorcoding.ibas.bobas.i18n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class LanguageItemContent {
	public LanguageItemContent() {

	}

	public LanguageItemContent(String langCode, String content) {
		this.setLanguageCode(langCode);
		this.setContent(content);
	}

	private String languageCode;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
