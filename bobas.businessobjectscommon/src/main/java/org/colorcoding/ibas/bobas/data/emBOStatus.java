package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 业务对象状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emBOStatus", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emBOStatus {

	/**
	 * 未清
	 */
	@Value("O")
	OPEN,

	/**
	 * 已清
	 */
	@Value("C")
	CLOSED;

	public static emBOStatus valueOf(int value) {
		return values()[value];
	}

	public static emBOStatus valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emBOStatus.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emBOStatus) item;
				}
			}
		}
		return emBOStatus.valueOf(value);
	}
}
