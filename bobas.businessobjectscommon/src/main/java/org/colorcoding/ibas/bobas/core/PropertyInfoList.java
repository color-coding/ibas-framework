package org.colorcoding.ibas.bobas.core;

import java.util.ArrayList;

/**
 * 属性集合
 * 
 * @author Niuren.Zhu
 *
 */
public class PropertyInfoList extends ArrayList<IPropertyInfo<?>> {

	private static final long serialVersionUID = -6320914335175298868L;

	public PropertyInfoList() {
		super();
	}

	public PropertyInfoList(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public synchronized boolean add(IPropertyInfo<?> e) {
		for (IPropertyInfo<?> item : this) {
			if (item.getName().equals(e.getName())) {
				return false;
			}
		}
		return super.add(e);
	}

}
