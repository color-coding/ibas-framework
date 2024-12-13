package org.colorcoding.ibas.bobas.core;

import java.io.Serializable;

import org.colorcoding.ibas.bobas.data.IArrayList;

public interface IFieldedObject extends Cloneable, Serializable, IBindable, ITrackable {
	/**
	 * 对象的属性
	 * 
	 * @return
	 */
	IArrayList<IPropertyInfo<?>> properties();

	/**
	 * 获取属性值
	 * 
	 * @param <P>      值类型
	 * @param property 属性
	 * @return
	 */
	<P> P getProperty(IPropertyInfo<?> property);

	/**
	 * 设置属性值
	 * 
	 * @param <P>      值类型
	 * @param property 属性
	 * @param value    值
	 */
	<P> void setProperty(IPropertyInfo<?> property, P value);
}
