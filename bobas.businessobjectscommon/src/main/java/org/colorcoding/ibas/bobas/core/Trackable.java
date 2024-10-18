package org.colorcoding.ibas.bobas.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class Trackable extends Bindable implements ITrackable {

	@XmlElement
	private volatile boolean isDirty = true;


	public final boolean isDirty() {
		return this.isDirty;
	}

	final void setDirty(boolean value) {
		if (value == this.isDirty) {
			return;
		}
		boolean oldValue = this.isDirty;
		this.isDirty = value;
		this.firePropertyChange("isDirty", oldValue, this.isDirty);
	}

	@XmlElement
	private volatile boolean isDeleted = false;


	public final boolean isDeleted() {
		return this.isDeleted;
	}

	final void setDeleted(boolean value) {
		if (value == this.isDeleted) {
			return;
		}
		boolean oldValue = this.isDeleted;
		this.isDeleted = value;
		this.firePropertyChange("isDeleted", oldValue, this.isDeleted);
	}

	@XmlElement
	private volatile boolean isNew = true;


	public final boolean isNew() {
		return this.isNew;
	}

	final void setNew(boolean value) {
		if (value == this.isNew) {
			return;
		}
		boolean oldValue = this.isNew;
		this.isNew = value;
		this.firePropertyChange("isNew", oldValue, this.isNew);
	}

	@XmlElement
	private volatile boolean isSavable = true;


	public final boolean isSavable() {
		return this.isSavable;
	}

	final void setSavable(boolean value) {
		this.isSavable = value;
	}

	private volatile boolean isLoading;

	/**
	 * 数据加载中
	 * 
	 * 数据加载中 不触发属性改变事件
	 * 
	 * @return
	 */
	public final boolean isLoading() {
		return isLoading;
	}

	/**
	 * 设置-数据加载中
	 * 
	 * @param value
	 */
	final void setLoading(boolean value) {
		this.isLoading = value;
	}

	public final void markOld() {
		this.setNew(false);
		this.setDirty(false);
		// this.setDeleted(false);// 删除标记不去除
	}

	public final void markNew() {
		this.setNew(true);
		this.setDirty(true);
		this.setDeleted(false);
	}

	public final void markDeleted() {
		this.setDirty(true);
		this.setDeleted(true);
	}

	public final void clearDeleted() {
		if (!this.isDirty()) {
			this.markDirty();
		}
		this.setDeleted(false);
	}

	public final void markDirty() {
		this.setDirty(true);
	}
}
