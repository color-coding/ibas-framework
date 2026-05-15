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

	/**
	 * 获取指定语言的文本
	 *
	 * @param langcode 语言编码；null或未找到时返回默认语言内容
	 * @return 文本内容
	 */
	public String getContent(String langcode) {
		if (!Strings.isNullOrEmpty(langcode)) {
			for (KeyText item : this.getContents()) {
				if (langcode.equalsIgnoreCase(item.getKey())) {
					return item.getText();
				}
			}
		}
		// 未找到指定语言，回退到空编码的默认内容
		for (KeyText item : this.getContents()) {
			if (Strings.isNullOrEmpty(item.getKey())) {
				return item.getText();
			}
		}
		return this.getContent();
	}

	/**
	 * 获取默认语言的文本
	 *
	 * @return 默认语言的文本；无内容时返回[key]格式
	 */
	public String getContent() {
		// 优先返回默认语言内容（空编码）
		for (KeyText item : this.getContents()) {
			if (Strings.isNullOrEmpty(item.getKey())) {
				return item.getText();
			}
		}
		// 回退到第一个内容
		for (KeyText item : this.getContents()) {
			return item.getText();
		}
		return Strings.format("[%s]", this.getKey());
	}

	/**
	 * 添加或更新语言内容
	 *
	 * @param langcode 语言编码；空字符串表示默认语言，null忽略
	 * @param content  文本内容；null忽略
	 */
	public void addContent(String langcode, String content) {
		if (langcode == null)
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
