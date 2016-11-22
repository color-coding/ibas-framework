package org.colorcoding.ibas.bobas.core;

/**
 * 序列化监听
 * 
 * @author Niuren.Zhu
 *
 */
public interface SerializationListener {

	/**
	 * 反序列之前
	 * 
	 * @param target
	 *            目标对象
	 * @param parent
	 *            目标对象所属对象
	 */
	void beforeUnmarshal(Object parent);

	/**
	 * 反序列之后
	 * 
	 * @param target
	 *            目标对象
	 * @param parent
	 *            目标对象所属对象
	 */
	void afterUnmarshal(Object parent);

	/**
	 * 序列之前
	 * 
	 * @param target
	 *            目标对象
	 */
	void beforeMarshal();

	/**
	 * 序列之后
	 * 
	 * @param target
	 *            目标对象
	 */
	void afterMarshal();

}
