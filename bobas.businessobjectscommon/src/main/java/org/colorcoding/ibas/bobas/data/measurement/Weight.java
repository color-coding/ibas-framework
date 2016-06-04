package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 重量
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Weight", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class Weight extends MeasurementDecimal<emWeightUnit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5827904441419366879L;
	static KeyText[] unitCharacters = null;

	public Weight() {

	}

	public Weight(Decimal value, emWeightUnit unit) {
		super(value);
		this.setUnit(unit);
	}

	public Weight(double value, emWeightUnit unit) {
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

	private emWeightUnit unit;

	@Override
	@XmlElement(name = "Unit")
	public emWeightUnit getUnit() {
		if (this.unit == null) {
			this.unit = emWeightUnit.kilogram;
		}
		return this.unit;
	}

	@Override
	public void setUnit(Object value) {
		this.setUnit((emWeightUnit) value);
	}

	public void setUnit(emWeightUnit value) {
		emWeightUnit oldValue = this.unit;
		this.unit = value;
		this.firePropertyChange("Unit", oldValue, this.unit);
	}

	@Override
	public int compareTo(IMeasurement<Decimal, emWeightUnit> o) {
		Decimal convertValue = emWeightUnit.convert(this.getUnit(), o.getValue(), o.getUnit());
		return this.getValue().compareTo(convertValue);
	}
}
