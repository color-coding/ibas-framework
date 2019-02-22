package org.colorcoding.ibas.bobas.data.measurement;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 重量
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Weight", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class Weight extends MeasurementDecimal<emWeightUnit> {

	private static final long serialVersionUID = 5827904441419366879L;

	static KeyText[] unitCharacters = null;

	public Weight() {

	}

	public Weight(BigDecimal value, emWeightUnit unit) {
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
			this.unit = emWeightUnit.KILOGRAM;
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
	public int compareTo(IMeasurement<BigDecimal, emWeightUnit> o) {
		BigDecimal convertValue = emWeightUnit.convert(this.getUnit(), o.getValue(), o.getUnit());
		return this.getValue().compareTo(convertValue);
	}
}
