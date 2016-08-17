package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.measurement.IMeasurement;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

class ComplexFieldDataMeasurement extends ComplexFieldDataBase<IMeasurement<?, ?>> {

	private IMeasurement<?, ?> value = null;

	@Override
	public IMeasurement<?, ?> getValue() {
		if (this.value == null) {
			// 如果未初始化，则尝试自动初始化
			try {
				this.setValue(BOFactory.create().createInstance(this.getValueType()));
			} catch (BOFactoryException e) {
				e.printStackTrace();
			}
		}
		return this.value;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((IMeasurement<?, ?>) value);
	}

	public boolean setValue(IMeasurement<?, ?> value) {
		if (this.value != null && this.value.equals(value)) {
			return false;
		}
		if (this.value != null) {
			// 移出事件监听
			this.value.removePropertyChangeListener(this);
		}
		this.value = value;
		if (this.getDbFields() != null) {
			for (FieldDataDb4Complex<?> item : this.getDbFields()) {
				item.setData(this.value);
			}
		} else {
			this.mapping();
		}
		if (this.value != null) {
			// 添加事件监听
			this.value.addPropertyChangeListener(this);
		}
		this.setDirty(true);
		return true;
	}

	@Override
	protected FieldDataDb4Complex<?>[] createDbFields(String name, String table) {
		if (this.value == null) {
			return null;
		}
		FieldDataDb4Complex<?>[] fieldDataDbs = new FieldDataDb4Complex<?>[2];
		FieldData4Value fdValue = new FieldData4Value(this.value);
		String fieldName = name;
		fdValue.setName(fieldName);
		fdValue.setPrimaryKey(false);
		fdValue.setSavable(true);
		fdValue.setOriginal(true);
		fdValue.setDbField(fieldName);
		fdValue.setDbTable(table);
		fdValue.setFieldType(DbFieldType.db_Decimal);
		fdValue.setValueType(Decimal.class);
		fieldDataDbs[0] = fdValue;
		FieldData4Unit fdUnit = new FieldData4Unit(this.getValue());
		fieldName = String.format("%sUnit", name);
		fdUnit.setName(fieldName);
		fdUnit.setPrimaryKey(false);
		fdUnit.setSavable(true);
		fdUnit.setOriginal(true);
		fdUnit.setDbField(fieldName);
		fdUnit.setDbTable(table);
		fdUnit.setFieldType(DbFieldType.db_Alphanumeric);
		if (this.value.getUnit() != null) {
			fdUnit.setValueType(this.value.getUnit().getClass());
		}
		fieldDataDbs[1] = fdUnit;
		return fieldDataDbs;
	}

	/**
	 * 度量-值-字段
	 * 
	 * @author Niuren.Zhu
	 *
	 */
	private class FieldData4Value extends FieldDataDb4Complex<Decimal> {

		public FieldData4Value(IMeasurement<?, ?> value) {
			this.setMeasurement(value);
		}

		private IMeasurement<?, ?> measurement = null;

		public final IMeasurement<?, ?> getMeasurement() {
			return measurement;
		}

		public final void setMeasurement(IMeasurement<?, ?> measurement) {
			this.measurement = measurement;
		}

		@Override
		public Object getData() {
			return this.getMeasurement();
		}

		@Override
		public void setData(Object value) {
			this.setMeasurement((IMeasurement<?, ?>) value);
		}

		@Override
		public boolean setValue(Object value) {
			if (this.getMeasurement() == null) {
				return false;
			}
			this.getMeasurement().setValue(value);
			return true;
		}

		@Override
		public Decimal getValue() {
			if (this.getMeasurement() == null) {
				return null;
			}
			return (Decimal) this.getMeasurement().getValue();
		}

	}

	/**
	 * 度量-单位-字段
	 * 
	 * @author Niuren.Zhu
	 *
	 */
	private class FieldData4Unit extends FieldDataDb4Complex<Object> {

		public FieldData4Unit(IMeasurement<?, ?> value) {
			this.setMeasurement(value);
		}

		private IMeasurement<?, ?> measurement = null;

		public final IMeasurement<?, ?> getMeasurement() {
			return measurement;
		}

		public final void setMeasurement(IMeasurement<?, ?> measurement) {
			this.measurement = measurement;
		}

		@Override
		public Object getData() {
			return this.getMeasurement();
		}

		@Override
		public void setData(Object value) {
			this.setMeasurement((IMeasurement<?, ?>) value);
		}

		@Override
		public boolean setValue(Object value) {
			if (this.getMeasurement() == null) {
				return false;
			}
			this.getMeasurement().setUnit(value);
			return true;
		}

		@Override
		public Object getValue() {
			if (this.getMeasurement() == null) {
				return null;
			}
			return this.getMeasurement().getUnit();
		}

	}

}
