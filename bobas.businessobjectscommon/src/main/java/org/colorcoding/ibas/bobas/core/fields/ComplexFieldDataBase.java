package org.colorcoding.ibas.bobas.core.fields;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.mapping.ComplexField;

/**
 * 复合字段基类
 * 
 * @author Niuren.Zhu
 *
 * @param <T>
 */
public abstract class ComplexFieldDataBase<T> extends FieldDataBase<T>
		implements IFieldDataDbs, PropertyChangeListener {

	private FieldDataDb4Complex<?>[] dbFields;

	public final FieldDataDb4Complex<?>[] getDbFields() {
		return dbFields;
	}

	public final void setDbFields(FieldDataDb4Complex<?>[] dbFields) {
		this.dbFields = dbFields;
	}

	@Override
	public Iterator<IFieldDataDb> iterator() {
		return new Iterator<IFieldDataDb>() {
			private int nextSlot = 0;

			@Override
			public boolean hasNext() {
				if (dbFields == null || nextSlot >= dbFields.length)
					return false;
				return true;
			}

			@Override
			public IFieldDataDb next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return dbFields[nextSlot++];
			}
		};
	}

	@Override
	protected void setDirty(boolean value) {
		super.setDirty(value);
		if (this.getDbFields() != null) {
			for (FieldDataDb4Complex<?> item : this.getDbFields()) {
				item.setDirty(value);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// 度量属性发生改变，改变映射的数据库字段状态
		this.setDirty(true);
	}

	@Override
	public void mapping(IPropertyInfo<?> mapping) {
		super.mapping(mapping);
		super.setValueType(mapping.getValueType());
	}

	private String field;

	public final String getField() {
		return field;
	}

	public final void setField(String field) {
		this.field = field;
	}

	private String table;

	public final String getTable() {
		return table;
	}

	public final void setTable(String table) {
		this.table = table;
	}

	public void mapping(ComplexField mapping) {
		this.setSavable(mapping.savable());
		this.setField(mapping.name());
		this.setTable(mapping.table());
		try {
			this.setValue(BOFactory.create().createInstance(this.getValueType()));
		} catch (BOFactoryException e) {
			e.printStackTrace();
		}
	}

	protected void mapping() {
		if (this.isSavable()) {
			this.setDbFields(this.createDbFields(this.getField(), this.getTable()));
		}
	}

	protected abstract FieldDataDb4Complex<?>[] createDbFields(String name, String table);
}

abstract class FieldDataDb4Complex<T> extends FieldDataDbBase<T> {

	@Override
	public void setDirty(boolean value) {
		super.setDirty(value);
	}

	public abstract Object getData();

	public abstract void setData(Object value);
}
