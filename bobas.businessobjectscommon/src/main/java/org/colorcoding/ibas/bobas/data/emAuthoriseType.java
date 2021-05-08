package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 权限
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emAuthoriseType", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emAuthoriseType {
	/**
	 * 完全
	 */
	@Value("A")
	ALL,
	/**
	 * 只读
	 */
	@Value("R")
	READ,
	/**
	 * 没有
	 */
	@Value("N")
	NONE;

	public static emAuthoriseType valueOf(int value) {
		return values()[value];
	}

	public static emAuthoriseType valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emAuthoriseType.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emAuthoriseType) item;
				}
			}
		}
		return emAuthoriseType.valueOf(value);
	}
}
