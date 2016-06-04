package org.colorcoding.bobas.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.util.ArrayList;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObjectListBase", namespace = MyConsts.NAMESPACE_BOBAS_CORE)
public abstract class BusinessObjectListBase<E extends IBusinessObjectBase> extends ArrayList<E>
		implements IBusinessObjectListBase<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212226199781937273L;

	@Override
	public boolean add(E item) {
		boolean done = super.add(item);
		if (done) {
			if (item != null) {
				this.afterAddItem(item);
			}
		}
		return done;
	}

	/**
	 * 集合添加项目后
	 * 
	 * @param item
	 *            添加的项目
	 */
	protected void afterAddItem(E item) {

	}

	@Override
	public E remove(int index) {
		E item = super.remove(index);
		this.afterAddItem(item);
		return item;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object item) {
		boolean done = super.remove(item);
		if (done) {
			this.afterRemoveItem((E) item);
		}
		return done;
	}

	/**
	 * 集合移出项目后
	 * 
	 * @param item
	 *            项目
	 */
	protected void afterRemoveItem(E item) {

	}

}
