package org.colorcoding.ibas.bobas.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 逻辑契约
 * @author Niuren.Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LogicContract {

	/**
	 * 契约类型
	 * 
	 * @return
	 */
	public Class<?> value();
}
