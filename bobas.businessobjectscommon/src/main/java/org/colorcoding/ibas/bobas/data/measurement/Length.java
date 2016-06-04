package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 长度
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Length", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class Length extends MeasurementDecimal<emLengthUnit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -443387451382047557L;
	static KeyText[] unitCharacters = null;

	public Length() {

	}

	public Length(Decimal value, emLengthUnit unit) {
		super(value);
		this.setUnit(unit);
	}

	public Length(double value, emLengthUnit unit) {
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

	private emLengthUnit unit;

	@Override
	@XmlElement(name = "Unit")
	public emLengthUnit getUnit() {
		if (this.unit == null) {
			this.unit = emLengthUnit.meter;
		}
		return this.unit;
	}

	@Override
	public void setUnit(Object value) {
		this.setUnit((emLengthUnit) value);
	}

	public void setUnit(emLengthUnit value) {
		emLengthUnit oldValue = this.unit;
		this.unit = value;
		this.firePropertyChange("Unit", oldValue, this.unit);
	}

	@Override
	public int compareTo(IMeasurement<Decimal, emLengthUnit> o) {
		Decimal convertValue = emLengthUnit.convert(this.getUnit(), o.getValue(), o.getUnit());
		return this.getValue().compareTo(convertValue);
	}

}
