package org.colorcoding.ibas.bobas.repository;

import java.util.Iterator;
import java.util.LinkedList;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

class BOCacheContainers implements Iterable<IBOCacheContainer> {

	LinkedList<IBOCacheContainer> cacheList = new LinkedList<IBOCacheContainer>();

	/**
	 * 更新的数据加入缓存
	 * 
	 * @param container
	 *            数据
	 * @return true，加入缓存；false，未加入缓存
	 */
	public boolean add(IBOCacheContainer container) {
		for (int i = 0; i < this.cacheList.size(); i++) {
			IBOCacheContainer item = this.cacheList.get(i);
			if (item == null) {
				continue;
			}
			if (item.getDataKey().equals(container.getDataKey())) {
				if (item.getCacheTime() > container.getCacheTime()) {
					// 晚于已缓存的数据时间，退出
					return false;
				} else {
					// 新于已缓存的数据时间，替换
					this.cacheList.set(i, container);
					return true;
				}
			}
		}
		for (int i = 0; i < this.cacheList.size(); i++) {
			IBOCacheContainer item = this.cacheList.get(i);
			if (item == null) {
				this.cacheList.set(i, container);
				return true;
			}
		}
		this.cacheList.add(container);
		return true;
	}

	/**
	 * 遍历有效的缓存容器（未过期的）
	 */
	@Override
	public Iterator<IBOCacheContainer> iterator() {

		return new Iterator<IBOCacheContainer>() {
			int cursor = -1;
			int lastRet = 0;

			@Override
			public boolean hasNext() {
				for (int i = lastRet + 1; i < cacheList.size(); i++) {
					IBOCacheContainer item = cacheList.get(i);
					if (item != null) {
						if (!item.isExpired()) {
							cursor = i;
							return true;
						}
					}
				}
				return false;
			}

			@Override
			public IBOCacheContainer next() {
				if (cursor >= cacheList.size()) {
					return null;
				}
				lastRet = cursor;
				return cacheList.get(cursor);
			}

		};
	}

	public void remove(IBOCacheContainer container) {
		this.cacheList.remove(container);
	}

	public void remove(IBusinessObjectBase data) {
		for (IBOCacheContainer item : cacheList) {
			if (item == null) {
				continue;
			}
			if (item.getDataKey().equals(data.toString())) {
				this.remove(item);
				break;
			}
		}
	}

	/**
	 * 清理过期的数据
	 */
	public void clearExpired() {
		for (int i = 0; i < cacheList.size(); i++) {
			IBOCacheContainer boContainer = cacheList.get(i);
			if (boContainer == null) {
				continue;
			}
			cacheList.set(i, null);
		}
	}
}
