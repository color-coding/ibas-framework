package org.colorcoding.ibas.bobas.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class Trackable extends Bindable implements ITrackable {

	private static final long serialVersionUID = 1L;

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

	public final void setSavable(boolean value) {
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
	public final void setLoading(boolean value) {
		this.isLoading = value;
	}

	public void markOld() {
		this.setNew(false);
		this.setDirty(false);
		/*
		 * 删除标记不去除??
		 * 
		 * this.setDeleted(false);
		 */
	}

	public void markNew() {
		this.setNew(true);
		this.setDirty(true);
		this.setDeleted(false);
	}

	public void markDeleted() {
		this.setDirty(true);
		this.setDeleted(true);
	}

	public void clearDeleted() {
		if (!this.isDirty()) {
			this.markDirty();
		}
		this.setDeleted(false);
	}

	public void markDirty() {
		this.setDirty(true);
	}

	/**
	 * 反序列化之前调用
	 * 
	 * @param parent 所属父项
	 */
	protected void beforeUnmarshal(Object parent) {
		super.beforeUnmarshal(parent);
		this.setLoading(true);
	}

	/**
	 * 反序列化之后调用
	 * 
	 * @param parent 所属父项
	 */
	protected void afterUnmarshal(Object parent) {
		super.afterUnmarshal(parent);
		this.setLoading(false);
	}

	/**
	 * 序列化之前调用
	 */
	protected void beforeMarshal() {
		super.beforeMarshal();
		this.setLoading(true);
	}

	/**
	 * 序列化之后调用
	 */
	protected void afterMarshal() {
		super.afterMarshal();
		this.setLoading(false);
	}
}
