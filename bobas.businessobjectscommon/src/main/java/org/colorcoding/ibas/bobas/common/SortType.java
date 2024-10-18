package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 排序
 */
@XmlType(name = "SortType", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public enum SortType {

	/** 降序 */
	@Value("D")
	DESCENDING,
	/** 升序 */
	@Value("A")
	ASCENDING;

	public static SortType valueOf(int value) {
		return values()[value];
	}

	public static SortType valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : SortType.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (SortType) item;
				}
			}
		}
		return SortType.valueOf(value);
	}
}
