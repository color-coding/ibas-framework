package org.colorcoding.ibas.bobas.bo;

public interface IUserField<P> {
	/**
	 * 获取-名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取-值类型
	 */
	Class<?> getValueType();

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
