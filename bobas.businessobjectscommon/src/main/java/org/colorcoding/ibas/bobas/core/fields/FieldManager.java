package org.colorcoding.ibas.bobas.core.fields;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfoList;
import org.colorcoding.ibas.bobas.core.PropertyInfoManager;

public class FieldManager implements Iterable<IFieldData> {

	/**
	 * 构造，根据类型获取依赖属性 生成，数据字段
	 */
	public FieldManager(Class<?> boType) {
		PropertyInfoList propertyInfoList = PropertyInfoManager.getPropertyInfoList(boType);
		this.fieldDatas = new IFieldData[propertyInfoList.size()];
		for (int i = 0; i < fieldDatas.length; i++) {
			this.fieldDatas[i] = FieldsFactory.create().create(propertyInfoList.get(i));
		}
	}

	private IFieldData[] fieldDatas;

	/**
	 * 根据属性的索引直接获取数据字段 坑
	 */
	public IFieldData getFieldData(IPropertyInfo<?> property) {
		return this.fieldDatas[property.getIndex()];
	}

	/**
	 * 迭代器
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IFieldData> iterator() {
		return new Iterator<IFieldData>() {
			private int nextSlot = 0;

			@Override
			public boolean hasNext() {
				if (fieldDatas == null || nextSlot >= fieldDatas.length)
					return false;
				return true;
			}

			@Override
			public IFieldData next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return fieldDatas[nextSlot++];
			}
		};
	}

	/**
	 * 返回数组
	 * 
	 * @return 字段数据数组
	 */
	public IFieldData[] toArray() {
		return this.fieldDatas;
	}
}
