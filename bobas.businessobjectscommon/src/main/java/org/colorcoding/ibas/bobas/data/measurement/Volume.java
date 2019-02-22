package org.colorcoding.ibas.bobas.data.measurement;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 体积
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Volume", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class Volume extends MeasurementDecimal<emVolumeUnit> {

	private static final long serialVersionUID = 4463068051027966746L;

	static KeyText[] unitCharacters = null;

	public Volume() {

	}

	public Volume(BigDecimal value, emVolumeUnit unit) {
		super(value);
		this.setUnit(unit);
	}

	public Volume(double value, emVolumeUnit unit) {
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

	private emVolumeUnit unit;

	@Override
	@XmlElement(name = "Unit")
	public emVolumeUnit getUnit() {
		if (this.unit == null) {
			this.unit = emVolumeUnit.CUBIC_METRE;
		}
		return this.unit;
	}

	@Override
	public void setUnit(Object value) {
		this.setUnit((emVolumeUnit) value);
	}

	public void setUnit(emVolumeUnit value) {
		emVolumeUnit oldValue = this.unit;
		this.unit = value;
		this.firePropertyChange("Unit", oldValue, this.unit);
	}

	private BigDecimal _long = Decimal.ZERO;

	public BigDecimal getLong() {
		return this._long;
	}

	public void setLong(BigDecimal value) {
		this._long = value;
		this.setValue(Decimal.multiply(this.getLong(), this.getWidth(), this.getHight()));
	}

	private BigDecimal _width = Decimal.ZERO;

	public BigDecimal getWidth() {
		return this._width;
	}

	public void setWidth(BigDecimal value) {
		this._width = value;
		this.setValue(Decimal.multiply(this.getLong(), this.getWidth(), this.getHight()));
	}

	private BigDecimal _hight = Decimal.ZERO;

	public BigDecimal getHight() {
		return this._hight;
	}

	public void setHight(BigDecimal value) {
		this._hight = value;
		this.setValue(Decimal.multiply(this.getLong(), this.getWidth(), this.getHight()));
	}

	@Override
	public int compareTo(IMeasurement<BigDecimal, emVolumeUnit> o) {
		BigDecimal convertValue = emVolumeUnit.convert(this.getUnit(), o.getValue(), o.getUnit());
		return this.getValue().compareTo(convertValue);
	}

}
