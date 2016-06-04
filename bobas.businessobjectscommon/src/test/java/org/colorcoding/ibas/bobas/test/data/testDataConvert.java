package org.colorcoding.ibas.bobas.test.data;

import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DataConvert;

import junit.framework.TestCase;

public class testDataConvert extends TestCase {

	public void testEnumConvert() {
		emYesNo emValue = emYesNo.Yes;
		String dbValue = DataConvert.toDbValue(emValue);
		assertEquals("Y", dbValue);
		emValue = (emYesNo) DataConvert.toEnumValue(emYesNo.class, "N");
		assertEquals(emYesNo.No, emValue);
		emValue = (emYesNo) DataConvert.toEnumValue(emYesNo.class, "No");
		assertEquals(emYesNo.No, emValue);
		// 索引测试
		emValue = (emYesNo) DataConvert.toEnumValue(emYesNo.class, 1);
		assertEquals(emYesNo.Yes, emValue);
		// 值测试
		emConditionOperation emcpValue = (emConditionOperation) DataConvert.toEnumValue(emConditionOperation.class,
				emConditionOperation.CONTAIN.getValue());
		assertEquals(emConditionOperation.CONTAIN, emcpValue);
	}
}
