package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 方向
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emDirection", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emDirection {
	/**
	 * 入
	 */
	@Value("I")
	IN,
	/**
	 * 出
	 */
	@Value("O")
	OUT;

	public static emDirection valueOf(int value) {
		return values()[value];
	}

	public static emDirection valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emDirection.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emDirection) item;
				}
			}
		}
		return emDirection.valueOf(value);
	}
}
