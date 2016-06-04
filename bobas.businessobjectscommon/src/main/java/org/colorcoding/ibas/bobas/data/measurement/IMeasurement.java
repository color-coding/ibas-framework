package org.colorcoding.ibas.bobas.data.measurement;

import org.colorcoding.ibas.bobas.core.IBindableBase;

/**
 * 度量
 */
public interface IMeasurement<V, U> extends IBindableBase {
	/**
	 * 显示的
	 */
	@Override
	String toString();

	/**
	 * 获取值
	 */
	V getValue();

	/**
	 * 设置值
	 */
	void setValue(Object value);

	/**
	 * 设置值
	 */
	void setValue(String value);

	/**
	 * 设置值
	 */
	void setValue(int value);

	/**
	 * 设置值
	 */
	void setValue(double value);

	/**
	 * 设置值
	 */
	void setValue(long value);

	/**
	 * 计算值
	 */
	V toValue();

	/**
	 * 获取单位
	 */
	U getUnit();

	/**
	 * 设置单位
	 */
	void setUnit(Object value);
}
