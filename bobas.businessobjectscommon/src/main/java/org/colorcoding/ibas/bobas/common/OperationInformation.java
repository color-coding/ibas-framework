package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 操作信息
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "OperationInformation", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationInformation", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class OperationInformation implements IOperationInformation {
	public OperationInformation() {

	}

	public OperationInformation(String name) {
		this.setName(name);
	}

	public OperationInformation(String name, String content) {
		this(name);
		this.setContent(content);
	}

	public OperationInformation(String name, String content, String tag) {
		this(name, content);
		this.setTag(tag);
	}

	// 名称
	@XmlElement(name = "Name")
	private String name;

	@Override
	public final String getName() {
		return this.name;
	}

	public final void setName(String value) {
		this.name = value;
	}

	// 内容
	@XmlElement(name = "Content")
	private String content;

	@Override
	public final String getContent() {
		return this.content;
	}

	public final void setContent(String value) {
		this.content = value;
	}

	// 标签
	@XmlElement(name = "Tag")
	private String tag;

	@Override
	public final String getTag() {
		return this.tag;
	}

	public final void setTag(String value) {
		this.tag = value;
	}

	@Override
	public String toString() {
		return String.format("{operation information: %s - %s}", this.getName(), this.getContent());
	}

}
