package org.colorcoding.ibas.bobas.test.data;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.measurement.Currency;
import org.colorcoding.ibas.bobas.data.measurement.Length;
import org.colorcoding.ibas.bobas.data.measurement.emLengthUnit;

import junit.framework.TestCase;

public class TestDataType extends TestCase {

	public void testDateTime() {

		DateTime min = DateTime.MIN_VALUE;
		System.out.println(min.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		DateTime max = DateTime.MAX_VALUE;
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
		length.setUnit(emLengthUnit.KILOMETER);
		length.setValue(10000.00);
		System.out.println(String.format("Length: %s", length.toString())); // 长度
	}

	public void testDecimal() {
		System.out.println(Decimal.valueOf());
		System.out.println(String.format("%09d", 0));
		BigDecimal zero = Decimal.ZERO;
		BigDecimal zero2 = new BigDecimal("777777777777777770.00099999999999999999999999999");
		System.err.println(zero.equals(zero2));
		System.out.println(zero.toString());
		System.out.println(Decimal.round(zero2));

		BigDecimal value = new BigDecimal("1.000000001");
		System.out.println(value.toString());
		value = Decimal.round(value);
		System.out.println(value.toString());
		value = new BigDecimal("1.000001401");
		System.out.println(value.toString());
		value = Decimal.round(value);
		System.out.println(value.toString());
		value = new BigDecimal("1.0000019001");
		System.out.println(value.toString());
		value = Decimal.round(value);
		System.out.println(value.toString());

		System.out.println("************");
		System.out.println(Decimal.valueOf(".000000000"));
		System.out.println(Decimal.valueOf("1.000000000"));
		System.out.println(Decimal.valueOf("-1.000000000"));
		System.out.println(Decimal.valueOf("0.000000000"));
		System.out.println(Decimal.valueOf("1"));
		System.out.println(Decimal.valueOf("-1"));
	}
}
