package org.colorcoding.ibas.bobas.test.core;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.IBOFactory;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

import junit.framework.TestCase;

public class testBOFactory extends TestCase {

	public void testBOCode() throws ClassNotFoundException {
		IBOFactory boFactory = BOFactory.create();
		boFactory.loadClasses("org.colorcoding.ibas.bobas.test");
		String boCode = boFactory.getCode(SalesOrder.class);
		assertEquals(String.format("not found [%s]'s bocode.", SalesOrder.class), boCode,
				SalesOrder.BUSINESS_OBJECT_CODE);
		System.out.println(String.format("using bocode [%s]", boCode));
		// 加载命名空间的类
		boFactory.register(SalesOrder.class);
		Class<?> boClass = boFactory.getClass(boCode);
		assertEquals(String.format("not found [%s]'s class.", boCode), boClass, SalesOrder.class);
	}
}
