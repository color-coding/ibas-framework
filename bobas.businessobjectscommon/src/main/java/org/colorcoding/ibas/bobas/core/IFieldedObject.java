package org.colorcoding.ibas.bobas.core;

import java.io.Serializable;
import java.util.function.Predicate;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

public interface IFieldedObject extends Serializable, IBindable, ITrackable {
	/**
	 * 对象的属性（全部）
	 *
	 * @return 属性列表
	 */
	List<IPropertyInfo<?>> properties();

	/**
	 * 对象的属性
	 *
	 * @param filter 过滤条件；null表示不过滤
	 * @return 符合条件的属性列表
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
	 * @param property 属性；不允许为null
	 * @return 属性值；存储null时返回默认值
	 */
	<P> P getProperty(IPropertyInfo<?> property);

	/**
	 * 设置属性值
	 *
	 * @param <P>      值类型
	 * @param property 属性；不允许为null
	 * @param value    新值
	 */
	<P> void setProperty(IPropertyInfo<?> property, P value);

	/**
	 * 属性是否改变
	 *
	 * @param propertyInfo 属性
	 * @return true表示已修改
	 */
	default boolean isModified(IPropertyInfo<?> propertyInfo) {
		return false;
	}
}
