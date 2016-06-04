package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.DataConvert;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "MeasurementDouble", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public abstract class MeasurementDouble<U> extends Measurement<Double, U> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3691255331802819111L;

	public MeasurementDouble() {

	}

	public MeasurementDouble(Double value) {
		this();
		this.setValue(value);
	}

	@Override
	public double doubleValue() {
		return this.getValue();
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

	private double _value;

	@Override
	@XmlElement(name = "Value")
	public Double getValue() {
		return this._value;
	}

	@Override
	public void setValue(double value) {
		Double oldValue = this._value;
		this._value = value;
		this.firePropertyChange("Value", oldValue, this._value);
	}

	@Override
	public void setValue(Object value) {
		this.setValue(value);
	}

	@Override
	public void setValue(String value) {
		this.setValue(Double.parseDouble(value));
	}

	@Override
	public void setValue(int value) {
		this.setValue((double) value);
	}

	@Override
	public void setValue(long value) {
		this.setValue((double) value);
	}
}
