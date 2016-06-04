package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

class DecimalSerializer extends XmlAdapter<BigDecimal, Decimal> {

	/**
	 * 除数，1
	 */
	public final static Decimal DIVISOR_ONE = new Decimal("1");

	@Override
	public BigDecimal marshal(Decimal v) throws Exception {
		return Decimal.round(v, Decimal.RESERVED_DECIMAL_PLACES_STORAGE);// 仅保留6位小数
	}

	@Override
	public Decimal unmarshal(BigDecimal v) throws Exception {
		return new Decimal(v);
	}

}
