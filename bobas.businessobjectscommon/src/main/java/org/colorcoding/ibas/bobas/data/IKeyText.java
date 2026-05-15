package org.colorcoding.ibas.bobas.data;

/**
 * 键-文本对
 *
 * @author Niuren.Zhu
 *
 */
public interface IKeyText {
	/**
	 * 获取键
	 *
	 * @return 键值
	 */
	String getKey();

	/**
	 * 设置键
	 *
	 * @param value 键值
	 */
	void setKey(String value);

	/**
	 * 获取文本
	 *
	 * @return 文本内容
	 */
	String getText();

	/**
	 * 设置文本
	 *
	 * @param value 文本内容
	 */
	void setText(String value);

	/**
	 * 输出字符串
	 *
	 * @return 字符串表示
	 */
	String toString();
}
