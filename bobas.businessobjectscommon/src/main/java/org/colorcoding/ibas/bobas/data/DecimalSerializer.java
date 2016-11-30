package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

class DecimalSerializer extends XmlAdapter<BigDecimal, Decimal> {

    @Override
    public BigDecimal marshal(Decimal value) throws Exception {
        return value.round();// 截取小数位
    }

    @Override
    public Decimal unmarshal(BigDecimal value) throws Exception {
        return new Decimal(value);
    }

}
