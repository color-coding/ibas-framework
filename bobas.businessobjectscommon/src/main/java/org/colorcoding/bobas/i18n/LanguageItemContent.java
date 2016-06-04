package org.colorcoding.bobas.i18n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "LanguageItemContent", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
@XmlRootElement(name = "LanguageItemContent", namespace = MyConsts.NAMESPACE_BOBAS_I18N)
public class LanguageItemContent implements ILanguageItemContent {

	/**
	 * 存储语言类型
	 */
	private String languageCode;

	@Override
	@XmlElement(name = "languageCode")
	public String getLanguageCode() {
		return languageCode;
	}

	@Override
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * 存储相应语言对应的相应文本
	 */
	private String content;

	@Override
	@XmlElement(name = "content")
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

}
