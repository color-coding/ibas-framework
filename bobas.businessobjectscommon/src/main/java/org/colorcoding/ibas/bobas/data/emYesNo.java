package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 是否
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emYesNo", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emYesNo {
	/**
	 * 否
	 */
	@Value("N")
	NO,
	/**
	 * 是
	 */
	@Value("Y")
	YES;

	public static emYesNo valueOf(int value) {
		return values()[value];
	}

	public static emYesNo valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emYesNo.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emYesNo) item;
				}
			}
		}
		return emYesNo.valueOf(value);
	}
}
