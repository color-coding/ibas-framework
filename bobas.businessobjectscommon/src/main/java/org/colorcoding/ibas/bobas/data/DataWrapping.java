package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.serialization.Serializable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataWrapping", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "DataWrapping", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class DataWrapping extends Serializable {

	private static final long serialVersionUID = -3389334644344905791L;

	public DataWrapping() {
	}

	public DataWrapping(String content) {
		this();
		this.setContent(content);
	}

	@XmlElement(name = "Content")
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
