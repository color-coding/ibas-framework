package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务对象缓存容器
 */
class BOCacheContainer implements IBOCacheContainer {

	public BOCacheContainer() {

	}

	public BOCacheContainer(IBusinessObjectBase data) {
		this.setData(data);
	}

	@Override
	public String getDataKey() {
		if (this.data != null) {
			return this.data.toString();
		}
		return null;
	}

	private long cacheTime;

	@Override
	public long getCacheTime() {
		return this.cacheTime;
	}

	private void setCacheTime(long time) {
		this.cacheTime = time;
	}

	private IBusinessObjectBase data = null;

	@Override
	public IBusinessObjectBase getData() {
		return this.data;
	}

	@Override
	public void setData(IBusinessObjectBase data) {
		this.data = data;
		this.setCacheTime(System.currentTimeMillis());
	}

	@Override
	public boolean isExpired() {
		if ((this.getCacheTime() + this.getEffectiveTime()) < System.currentTimeMillis()) {
			// 超过有效时间
			return true;
		}
		return false;
	}

	private long effectiveTime = 5000;

	@Override
	public void setEffectiveTime(long minute) {
		this.effectiveTime = minute;
	}

	@Override
	public long getEffectiveTime() {
		return this.effectiveTime;
	}

}
