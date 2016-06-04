package org.colorcoding.ibas.bobas.repository;

import java.util.Iterator;
import java.util.LinkedList;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

public class BOCacheContainers implements Iterable<IBOCacheContainer> {

	LinkedList<IBOCacheContainer> cacheList = new LinkedList<IBOCacheContainer>();

	/**
	 * 更新的数据加入缓存
	 * 
	 * @param container
	 *            数据
	 * @return true，加入缓存；false，未加入缓存
	 */
	public boolean add(IBOCacheContainer container) {
		for (IBOCacheContainer item : cacheList) {
			if (item.getDataKey().equals(container.getDataKey())) {
				if (item.getCacheTime().after(container.getCacheTime())) {
					return false;
				}
			}
		}
		this.cacheList.add(container);
		return true;
	}

	@Override
	public Iterator<IBOCacheContainer> iterator() {
		return this.cacheList.iterator();
	}

	public void remove(IBOCacheContainer container) {
		this.cacheList.remove(container);
	}

	public void remove(IBusinessObjectBase data) {
		for (IBOCacheContainer item : cacheList) {
			if (item.getDataKey().equals(data.toString())) {
				this.remove(item);
			}
		}
	}
}
