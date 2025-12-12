package org.colorcoding.ibas.bobas.core;

import java.io.Serializable;
import java.util.function.Predicate;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

public interface IFieldedObject extends Serializable, IBindable, ITrackable {
	/**
	 * 对象的属性
	 * 
	 * @return
	 */
	List<IPropertyInfo<?>> properties();

	/**
	 * 对象的属性
	 * 
	 * @param filter 过滤条件
	 * @return
	 */
	default List<IPropertyInfo<?>> properties(Predicate<IPropertyInfo<?>> filter) {
		List<IPropertyInfo<?>> properties = this.properties();
		if (properties == null) {
			return new ArrayList<>(0);
		}
		return properties.where(filter);
	}

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

	/**
	 * 属性是否改变
	 * 
	 * @param propertyInfo 属性
	 * @return
	 */
	default boolean isModified(IPropertyInfo<?> propertyInfo) {
		return false;
	}
}
