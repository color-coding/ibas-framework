package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 时间
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Time", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class Time extends MeasurementDecimal<emTimeUnit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6668026582997115744L;

	static KeyText[] unitCharacters = null;

	public Time() {

	}

	public Time(Decimal value, emTimeUnit unit) {
		super(value);
		this.setUnit(unit);
	}

	public Time(double value, emTimeUnit unit) {
		super(value);
		this.setUnit(unit);
	}

	@Override
	protected KeyText[] getUnitCharacters() {
		if (unitCharacters == null) {
			unitCharacters = parseUnitCharacters(this.getUnit().getClass());
		}
		return unitCharacters;
	}

	private emTimeUnit unit;

	@Override
	@XmlElement(name = "Unit")
	// @XmlAttribute(name = "Unit")
	public emTimeUnit getUnit() {
		if (this.unit == null) {
			this.unit = emTimeUnit.hour;
		}
		return this.unit;
	}

	@Override
	public void setUnit(Object value) {
		this.setUnit((emTimeUnit) value);
	}

	public void setUnit(emTimeUnit value) {
		emTimeUnit oldValue = this.unit;
		this.unit = value;
		this.firePropertyChange("Unit", oldValue, this.unit);
	}

	@Override
	public int compareTo(IMeasurement<Decimal, emTimeUnit> o) {
		Decimal convertValue = emTimeUnit.convert(this.getUnit(), o.getValue(), o.getUnit());
		return this.getValue().compareTo(convertValue);
	}

}
