package org.colorcoding.ibas.bobas.test.data;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.measurement.Currency;
import org.colorcoding.ibas.bobas.data.measurement.Length;
import org.colorcoding.ibas.bobas.data.measurement.emLengthUnit;

import junit.framework.TestCase;

public class testDataType extends TestCase {

    public void testDateTime() {

        DateTime min = DateTime.minValue;
        System.out.println(min.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        DateTime max = DateTime.getMaxValue();
        System.out.println(max.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        DateTime today = DateTime.getToday();
        System.out.println(today.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        DateTime now = DateTime.getNow();
        System.out.println(now.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        DateTime dateTime = DateTime.valueOf("2017-01-30");
        System.out.println(dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    public void testMeasurement() {
        Currency currency = new Currency();
        currency.setUnit("RMB");
        currency.setValue(100.00);
        System.out.println(String.format("Currency: %s", currency.toString())); // 货币
        Length length = new Length();
        length.setUnit(emLengthUnit.kilometer);
        length.setValue(10000.00);
        System.out.println(String.format("Length: %s", length.toString())); // 长度
    }

    public void testDecimal() {
        System.out.println(String.format("%09d", 0));
        Decimal zero = Decimal.ZERO;
        Decimal zero2 = new Decimal("777777777777777770.00099999999999999999999999999");
        System.err.println(zero.equals(zero2));
        System.out.println(zero.toString());
        System.out.println(zero2.round());
    }
}
