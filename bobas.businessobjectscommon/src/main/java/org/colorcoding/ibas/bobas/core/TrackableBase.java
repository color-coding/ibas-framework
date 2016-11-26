package org.colorcoding.ibas.bobas.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "TrackableBase", namespace = MyConsts.NAMESPACE_BOBAS_CORE)
public abstract class TrackableBase extends BindableBase implements ITrackStatus, ITrackStatusOperator {

	private boolean isValid = true;

	@Override
	public final boolean isValid() {
		return this.isValid;
	}

	protected final void setValid(boolean value) {
		if (value == this.isValid) {
			return;
		}
		boolean oldValue = this.isValid;
		this.isValid = value;
		this.firePropertyChange("isValid", oldValue, this.isValid);
	}

	@XmlElement
	private boolean isDirty = true;

	@Override
	public final boolean isDirty() {
		return this.isDirty;
	}

	private final void setDirty(boolean value) {
		if (value == this.isDirty) {
			return;
		}
		boolean oldValue = this.isDirty;
		this.isDirty = value;
		this.firePropertyChange("isDirty", oldValue, this.isDirty);
	}

	@XmlElement
	private boolean isDeleted = false;

	@Override
	public final boolean isDeleted() {
		return this.isDeleted;
	}

	private final void setDeleted(boolean value) {
		if (value == this.isDeleted) {
			return;
		}
		if (this.isNew() && value) {
			// 新建状态，不允许删除
			return;
		}
		boolean oldValue = this.isDeleted;
		this.isDeleted = value;
		this.firePropertyChange("isDeleted", oldValue, this.isDeleted);
	}

	@XmlElement
	private boolean isNew = true;

	@Override
	public final boolean isNew() {
		return this.isNew;
	}

	private final void setNew(boolean value) {
		if (value == this.isNew) {
			return;
		}
		boolean oldValue = this.isNew;
		this.isNew = value;
		this.firePropertyChange("isNew", oldValue, this.isNew);
	}

	private boolean isSavable = true;

	@Override
	public final boolean isSavable() {
		return this.isSavable;
	}

	protected final void setSavable(boolean value) {
		if (value == this.isSavable) {
			return;
		}
		boolean oldValue = this.isSavable;
		this.isSavable = value;
		this.firePropertyChange("isSavable", oldValue, this.isSavable);
	}

	private boolean isBusy = false;

	@Override
	public final boolean isBusy() {
		return this.isBusy;
	}

	protected final void setBusy(boolean value) {
		if (value == this.isBusy) {
			return;
		}
		boolean oldValue = this.isBusy;
		this.isBusy = value;
		this.firePropertyChange("isBusy", oldValue, this.isBusy);
	}

	@Override
	public final void markOld() {
		this.setNew(false);
		this.setDirty(false);
		// this.setDeleted(false);// 删除标记不去除
	}

	@Override
	public final void markNew() {
		this.setNew(true);
		this.setDirty(true);
		this.setDeleted(false);
	}

	@Override
	public final void markDeleted() {
		this.setDirty(true);
		this.setDeleted(true);
	}

	@Override
	public final void markDirty() {
		this.setDirty(true);
	}

	@Override
	public final void markUnDeleted() {
		this.setDeleted(false);
	}

	private boolean isLoading;

	/**
	 * 数据加载中
	 * 
	 * 数据加载中 不触发属性改变事件
	 * 
	 * @return
	 */
	public boolean isLoading() {
		return isLoading;
	}

	/**
	 * 设置-数据加载中
	 * 
	 * @param value
	 */
	protected void setLoading(boolean value) {
		this.isLoading = value;
	}
}
