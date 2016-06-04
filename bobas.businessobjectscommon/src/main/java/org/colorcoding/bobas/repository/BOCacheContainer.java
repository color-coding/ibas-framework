package org.colorcoding.bobas.repository;

import org.colorcoding.bobas.core.IBusinessObjectBase;
import org.colorcoding.bobas.data.ComputeException;
import org.colorcoding.bobas.data.DateTime;
import org.colorcoding.bobas.data.measurement.emTimeUnit;

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

	private DateTime cacheTime = DateTime.getMaxValue();

	@Override
	public DateTime getCacheTime() {
		return this.cacheTime;
	}

	@Override
	public void setCacheTime(DateTime time) {
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
		this.setCacheTime(DateTime.getNow());
	}

	@Override
	public boolean isExpired() {
		// 获取与现在的间隔时间
		long time;
		try {
			time = DateTime.interval(this.getCacheTime(), emTimeUnit.minute);
			if (time >= this.getEffectiveTime()) {
				// 超过有效时间
				return true;
			}
		} catch (ComputeException e) {
			e.printStackTrace();
		}
		return false;
	}

	private long effectiveTime = 10;

	@Override
	public void setEffectiveTime(long minute) {
		this.effectiveTime = minute;
	}

	@Override
	public long getEffectiveTime() {
		return this.effectiveTime;
	}

}
