package org.colorcoding.ibas.bobas.data;

/**
 * 枚举条目
 *
 * 
 */
public interface IEnumItem {
	/**
	 * 枚举名称
	 * 
	 * @return
	 */
	String name();

	/**
	 * 索引值
	 * 
	 * @return 数值
	 */
	int ordinal();

	/**
	 * 定义的值
	 * 
	 * @return 数值
	 */
	int getValue();
}
