package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 十进制数序列化
 * 
 * @author Niuren.Zhu
 *
 */
class DecimalSerializer extends XmlAdapter<BigDecimal, Decimal> {

	@Override
	public BigDecimal marshal(Decimal value) throws Exception {
		return value;
	}

	@Override
	public Decimal unmarshal(BigDecimal value) throws Exception {
		return Decimal.valueOf(value);
	}

}
