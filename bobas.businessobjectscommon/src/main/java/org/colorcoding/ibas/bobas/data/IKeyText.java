package org.colorcoding.ibas.bobas.data;

/**
 * 键，值
 * 
 * @author Niuren.Zhu
 *
 */
public interface IKeyText {
	/**
	 * 获取-键
	 * 
	 * @return
	 */
	String getKey();

	/**
	 * 设置-键
	 * 
	 * @param value
	 */
	void setKey(String value);

	/**
	 * 获取-文本
	 * 
	 * @return
	 */
	String getText();

	/**
	 * 设置-文本
	 * 
	 * @param value
	 */
	void setText(String value);

	/**
	 * 输出字符串
	 * 
	 * @return
	 */
	String toString();
}
