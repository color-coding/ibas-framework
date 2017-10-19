package org.colorcoding.ibas.bobas.test.data;

import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DataConvert;

import junit.framework.TestCase;

public class testDataConvert extends TestCase {

	public void testEnumConvert() {
		emYesNo emValue = emYesNo.YES;
		String dbValue = DataConvert.toDbValue(emValue);
		assertEquals("Y", dbValue);
		emValue = (emYesNo) DataConvert.toEnumValue(emYesNo.class, "N");
		assertEquals(emYesNo.NO, emValue);
		emValue = (emYesNo) DataConvert.toEnumValue(emYesNo.class, "No");
		assertEquals(emYesNo.NO, emValue);
		// 索引测试
		emValue = (emYesNo) DataConvert.toEnumValue(emYesNo.class, 1);
		assertEquals(emYesNo.YES, emValue);
		// 值测试
		emConditionOperation emcpValue = (emConditionOperation) DataConvert.toEnumValue(emConditionOperation.class,
				emConditionOperation.CONTAIN.getValue());
		assertEquals(emConditionOperation.CONTAIN, emcpValue);

		for (KeyValue item : DataConvert.toKeyValues(emYesNo.class)) {
			System.out.println(item.toString());
		}
	}
}
