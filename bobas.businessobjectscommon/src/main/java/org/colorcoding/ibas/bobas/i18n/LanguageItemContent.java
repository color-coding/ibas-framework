package org.colorcoding.ibas.bobas.i18n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "LanguageItemContent", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
@XmlRootElement(name = "LanguageItemContent", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
public class LanguageItemContent implements ILanguageItemContent {
	public LanguageItemContent() {

	}

	public LanguageItemContent(String langCode, String content) {
		this.setLanguageCode(langCode);
		this.setContent(content);
	}

	private String languageCode;

	@Override
	@XmlElement(name = "LanguageCode")
	public String getLanguageCode() {
		return languageCode;
	}

	@Override
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	private String content;

	@Override
	@XmlElement(name = "Content")
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

}
