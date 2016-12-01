package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.Decimal;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "MeasurementDecimal", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public abstract class MeasurementDecimal<U> extends Measurement<Decimal, U> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6473479770614930990L;

	public MeasurementDecimal() {

	}

	public MeasurementDecimal(Decimal value) {
		this();
		this.setValue(value);
	}

	public MeasurementDecimal(double value) {
		this();
		this.setValue(value);
	}

	@Override
	public double doubleValue() {
		return DataConvert.toDouble(this.getValue());
	}

	@Override
	public float floatValue() {
		return DataConvert.toFloat(this.getValue());
	}

	@Override
	public int intValue() {
		return DataConvert.toInt(this.getValue());
	}

	@Override
	public long longValue() {
		return DataConvert.toLong(this.getValue());
	}

	private Decimal _value;

	@Override
	@XmlElement(name = "Value")
	public Decimal getValue() {
		if (this._value == null) {
			this._value = Decimal.ZERO;
		}
		return this._value;
	}

	@Override
	public void setValue(Object value) {
		this.setValue((Decimal) value);
	}

	public void setValue(Decimal value) {
		Decimal oldValue = this._value;
		this._value = value;
		this.firePropertyChange("Value", oldValue, this._value);
	}

	@Override
	public void setValue(String value) {
		this.setValue(new Decimal(value));
	}

	@Override
	public void setValue(int value) {
		this.setValue(new Decimal(value));
	}

	@Override
	public void setValue(double value) {
		this.setValue(new Decimal(value));
	}

	@Override
	public void setValue(long value) {
		this.setValue(new Decimal(value));
	}

}
