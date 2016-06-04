package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

/**
 * 值及描述
 */
@XmlType(name = "KeyText", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "KeyText", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class KeyText {
	/**
	 * 值
	 */
	@XmlElement(name = "Key")
	public String key = "";
	/**
	 * 描述
	 */
	@XmlElement(name = "Text")
	public String text = "";
}
