package org.colorcoding.ibas.btulz.data;

/**
 * 枚举，是否
 * 
 * @author Niuren.Zhu
 *
 */
public enum emYesNo {
	/**
	 * 否
	 */
	No,
	/**
	 * 是
	 */
	Yes;

	public int getValue() {
		return this.ordinal();
	}

	public static emYesNo forValue(int value) {
		return values()[value];
	}
}
