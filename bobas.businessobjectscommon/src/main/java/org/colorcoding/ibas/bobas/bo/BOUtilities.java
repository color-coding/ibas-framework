package org.colorcoding.ibas.bobas.bo;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.Objects;
import java.util.function.Consumer;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class BOUtilities {

	private BOUtilities() {
	}

	public static IBusinessObject EMPTY = new IBusinessObject() {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isSavable() {
			return false;
		}

		@Override
		public boolean isNew() {
			return false;
		}

		@Override
		public boolean isLoading() {
			return true;
		}

		@Override
		public boolean isDirty() {
			return false;
		}

		@Override
		public boolean isDeleted() {
			return false;
		}

		@Override
		public void removeListener(PropertyChangeListener listener) {
		}

		@Override
		public void registerListener(PropertyChangeListener listener) {
		}

		@Override
		public void undelete() {
		}

		@Override
		public void delete() {
		}

		@Override
		public String getIdentifiers() {
			return Strings.format("{EMPTY: %s}", this.hashCode());
		}

		@Override
		public ICriteria getCriteria() {
			return null;
		}
	};

	/**
	 * 是否为对象
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isBusinessObject(Object data) {
		if (data instanceof BusinessObject<?>) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为对象集合
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isBusinessObjects(Object data) {
		if (data instanceof BusinessObjects<?, ?>) {
			return true;
		}
		return false;
	}

	/**
	 * 是否可存储
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isSavable(Object data) {
		if (data instanceof IBusinessObject && data != EMPTY) {
			return ((IBusinessObject) data).isSavable();
		}
		return false;
	}

	/**
	 * 克隆实例
	 * 
	 * @param <T>
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T data) {
		if (isBusinessObject(data)) {
			return (T) ((BusinessObject<?>) data).clone();
		}
		throw new RuntimeException(I18N.prop("msg_bobas_invalid_data"));
	}

	/**
	 * 循环属性，值为IBusinessObject执行方法
	 * 
	 * @param action
	 */
	public static void traverse(BusinessObject<?> bo, Consumer<BusinessObject<?>> action) {
		if (action == null) {
			return;
		}
		Object data = null;
		for (IPropertyInfo<?> property : bo.properties()) {
			data = bo.getProperty(property);
			if (data == null) {
				continue;
			}
			if (isBusinessObject(data)) {
				// 值是业务对象
				action.accept((BusinessObject<?>) data);
			} else if (data instanceof Iterable<?>) {
				// 值是业务对象列表
				Iterable<?> datas = (Iterable<?>) data;
				for (Object item : datas) {
					if (isBusinessObject(item)) {
						action.accept((BusinessObject<?>) item);
					}
				}
			} else if (data.getClass().isArray()) {
				// 值是数组
				int length = Array.getLength(data);
				for (int i = 0; i < length; i++) {
					Object itemData = Array.get(data, i);
					if (isBusinessObject(itemData)) {
						action.accept((BusinessObject<?>) itemData);
					}
				}
			}
		}
	}

	/**
	 * 获取对象的属性
	 * 
	 * @param <P>          属性的值类型
	 * @param bo           对象
	 * @param propertyName 属性名称
	 * @return 找不到为空
	 */
	@SuppressWarnings("unchecked")
	public static <P> IPropertyInfo<P> propertyInfo(IBusinessObject bo, String propertyName) {
		if (!(bo instanceof FieldedObject)) {
			return null;
		}
		if (Strings.isNullOrEmpty(propertyName)) {
			return null;
		}
		FieldedObject fieldedObject = (FieldedObject) bo;
		for (IPropertyInfo<?> item : fieldedObject.properties()) {
			if (item.getName().equalsIgnoreCase(propertyName)) {
				return (IPropertyInfo<P>) item;
			}
		}
		return null;
	}

	/**
	 * 获取对象的属性值
	 * 
	 * @param <P>      值类型
	 * @param bo       对象
	 * @param property 属性
	 * @return
	 */
	public static <P> P propertyValue(IBusinessObject bo, IPropertyInfo<P> property) {
		if (!(bo instanceof FieldedObject)) {
			return null;
		}
		Objects.requireNonNull(property);
		return ((FieldedObject) bo).getProperty(property);
	}

	/**
	 * 获取对象的属性值
	 * 
	 * @param <P>          值类型
	 * @param bo           对象
	 * @param propertyName 属性名称
	 * @return
	 */
	public static <P> P propertyValue(IBusinessObject bo, String propertyName) {
		return propertyValue(bo, propertyInfo(bo, propertyName));
	}

	/**
	 * 版本实例a比b新
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isNewer(IBusinessObject a, IBusinessObject b) {
		if (!(a instanceof IBOStorageTag) || !(b instanceof IBOStorageTag)) {
			return false;
		}
		IBOStorageTag tagA = (IBOStorageTag) a;
		IBOStorageTag tagB = (IBOStorageTag) b;
		if (tagA.getLogInst() > tagB.getLogInst()) {
			return true;
		}
		return false;
	}
}
