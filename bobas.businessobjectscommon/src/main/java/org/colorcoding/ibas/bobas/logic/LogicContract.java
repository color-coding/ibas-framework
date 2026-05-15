package org.colorcoding.ibas.bobas.logic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 逻辑契约注解
 *
 * @author Niuren.Zhu
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicContract {

	/**
	 * 契约类型
	 *
	 * @return 契约接口类
	 */
	public Class<?> value();
}