package org.colorcoding.ibas.bobas.core.fields;

import java.lang.reflect.Array;

/**
 * 关联字段，业务对象
 */
class AssociatedFieldDataBO extends AssociatedFieldDataBase<Object> {

	public AssociatedFieldDataBO(int assoCount) {
		super(assoCount);
	}

	private Object value = null;

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public boolean setValue(Object value) {
		if (this.value != null && this.value.equals(value)) {
			return false;
		}
		this.value = value;
		this.setDirty(true);
		return true;
	}
}

/**
 * 关联字段，业务数组
 */
class AssociatedFieldDataArray extends AssociatedFieldDataBase<Object> {

	public AssociatedFieldDataArray(int assoCount) {
		super(assoCount);
	}

	private Object value;

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public boolean setValue(Object value) {
		if (value == null) {
			// 置空
			this.value = null;
			this.setDirty(true);
			return true;
		}
		if (this.value != null && this.value.equals(value)) {
			// 无变化
			return false;
		}
		if (value.getClass().isArray()) {
			// 重新构建数组
			int length = Array.getLength(value);
			Object newValue = Array.newInstance(this.getValueType(), length);
			for (int i = 0; i < length; i++) {
				Array.set(newValue, i, Array.get(value, i));
			}
			this.value = newValue;
			this.setDirty(true);
			return true;
		}
		return false;
	}
}