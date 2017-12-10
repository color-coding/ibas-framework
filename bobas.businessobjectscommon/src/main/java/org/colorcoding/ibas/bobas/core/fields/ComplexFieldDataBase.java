package org.colorcoding.ibas.bobas.core.fields;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.mapping.ComplexField;

/**
 * 复合字段基类
 * 
 * @author Niuren.Zhu
 *
 * @param <T>
 */
public abstract class ComplexFieldDataBase<T> extends FieldDataBase<T> implements IFieldDataDbs {

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
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
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

	public abstract Object getData();

	public abstract void setData(Object value);
}
