package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.i18n;

/**
 * 数量
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Quantity", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class Quantity extends MeasurementDecimal<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -125358402117609587L;

	public Quantity() {

	}

	public Quantity(Decimal value, String unit) {
		super(value);
		this.setUnit(unit);
	}

	public Quantity(double value, String unit) {
		super(value);
		this.setUnit(unit);
	}

	private String unit;

	@Override
	@XmlElement(name = "Unit")
	public String getUnit() {
		if (this.unit == null) {
			this.unit = "";
		}
		return this.unit;
	}

	@Override
	public void setUnit(Object value) {
		this.setUnit((String) value);
	}

	public void setUnit(String value) {
		String oldValue = this.unit;
		this.unit = value;
		this.firePropertyChange("Unit", oldValue, this.unit);
	}

	@Override
	public int compareTo(IMeasurement<Decimal, String> o) {
		if (!this.getUnit().equals(o.getUnit())) {
			throw new ClassCastException(i18n.prop("msg_bobas_measurement_unit_not_match"));
		}
		return this.getValue().compareTo(o.getValue());
	}

}
