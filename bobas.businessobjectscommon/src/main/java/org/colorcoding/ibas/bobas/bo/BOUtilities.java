package org.colorcoding.ibas.bobas.bo;

import java.lang.reflect.Array;
import java.util.function.Consumer;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;

public class BOUtilities {

	private BOUtilities() {
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
			if (data instanceof BusinessObject<?>) {
				// 值是业务对象
				action.accept((BusinessObject<?>) data);
			} else if (data instanceof Iterable<?>) {
				// 值是业务对象列表
				Iterable<?> datas = (Iterable<?>) data;
				for (Object item : datas) {
					if (item instanceof BusinessObject<?>) {
						action.accept((BusinessObject<?>) item);
					}
				}
			} else if (data.getClass().isArray()) {
				// 值是数组
				int length = Array.getLength(data);
				for (int i = 0; i < length; i++) {
					Object itemData = Array.get(data, i);
					if (itemData instanceof BusinessObject<?>) {
						action.accept((BusinessObject<?>) itemData);
					}
				}
			}
		}
	}

}
