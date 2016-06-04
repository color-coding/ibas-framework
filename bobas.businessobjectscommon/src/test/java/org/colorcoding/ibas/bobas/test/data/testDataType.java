package org.colorcoding.ibas.bobas.test.data;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.measurement.Currency;
import org.colorcoding.ibas.bobas.data.measurement.Length;
import org.colorcoding.ibas.bobas.data.measurement.emLengthUnit;

import junit.framework.TestCase;

public class testDataType extends TestCase {

	public void testDateTime() {

		DateTime min = DateTime.getMinValue();
		System.out.println(min.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		DateTime max = DateTime.getMaxValue();
		System.out.println(max.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		DateTime today = DateTime.getToday();
		System.out.println(today.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		DateTime now = DateTime.getNow();
		System.out.println(now.toString("yyyy-MM-dd'T'HH:mm:ss.SSS"));

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
}
