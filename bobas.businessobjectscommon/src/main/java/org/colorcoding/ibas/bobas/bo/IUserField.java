package org.colorcoding.ibas.bobas.bo;

public interface IUserField<P> {
	/**
	 * 获取-名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 值的类型
	 */
	java.lang.Class<P> getValueType();

	/**
	 * 获取-值
	 * 
	 * @return
	 */
	P getValue();

	/**
	 * 设置-值
	 * 
	 */
	void setValue(P value);

}
