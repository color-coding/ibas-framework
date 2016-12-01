package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 体积
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Volume", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class Volume extends MeasurementDecimal<emVolumeUnit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4463068051027966746L;
	static KeyText[] unitCharacters = null;

	public Volume() {

	}

	public Volume(Decimal value, emVolumeUnit unit) {
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
			this.unit = emVolumeUnit.cubic_metre;
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

	private Decimal _long = Decimal.ZERO;

	public Decimal getLong() {
		return this._long;
	}

	public void setLong(Decimal value) {
		this._long = value;
		this.setValue(this.getLong().multiply(this.getWidth().multiply(this.getHight())));
	}

	private Decimal _width = Decimal.ZERO;

	public Decimal getWidth() {
		return this._width;
	}

	public void setWidth(Decimal value) {
		this._width = value;
		this.setValue(this.getLong().multiply(this.getWidth().multiply(this.getHight())));
	}

	private Decimal _hight = Decimal.ZERO;

	public Decimal getHight() {
		return this._hight;
	}

	public void setHight(Decimal value) {
		this._hight = value;
		this.setValue(this.getLong().multiply(this.getWidth().multiply(this.getHight())));
	}

	@Override
	public int compareTo(IMeasurement<Decimal, emVolumeUnit> o) {
		Decimal convertValue = emVolumeUnit.convert(this.getUnit(), o.getValue(), o.getUnit());
		return this.getValue().compareTo(convertValue);
	}

}
