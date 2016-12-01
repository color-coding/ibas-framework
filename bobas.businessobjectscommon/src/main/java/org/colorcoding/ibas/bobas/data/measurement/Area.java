package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 面积
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Area", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class Area extends MeasurementDecimal<emAreaUnit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7240444474472101668L;
	static KeyText[] unitCharacters = null;

	public Area() {

	}

	public Area(Decimal value, emAreaUnit unit) {
		super(value);
		this.setUnit(unit);
	}

	public Area(double value, emAreaUnit unit) {
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

	private emAreaUnit unit;

	@Override
	@XmlElement(name = "Unit")
	public emAreaUnit getUnit() {
		if (this.unit == null) {
			this.unit = emAreaUnit.square_metre;
		}
		return this.unit;
	}

	@Override
	public void setUnit(Object value) {
		this.setUnit((emAreaUnit) value);
	}

	public void setUnit(emAreaUnit value) {
		emAreaUnit oldValue = this.unit;
		this.unit = value;
		this.firePropertyChange("Unit", oldValue, this.unit);
	}

	private Decimal _long = Decimal.ZERO;

	public Decimal getLong() {
		return this._long;
	}

	public void setLong(Decimal value) {
		this._long = value;
		this.setValue(this.getLong().multiply(this.getWidth()));
	}

	private Decimal _width = Decimal.ZERO;

	public Decimal getWidth() {
		return this._width;
	}

	public void setWidth(Decimal value) {
		this._width = value;
		this.setValue(this.getLong().multiply(this.getWidth()));
	}

	@Override
	public int compareTo(IMeasurement<Decimal, emAreaUnit> o) {
		Decimal convertValue = emAreaUnit.convert(this.getUnit(), o.getValue(), o.getUnit());
		return this.getValue().compareTo(convertValue);
	}

}
