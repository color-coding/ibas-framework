package org.colorcoding.ibas.bobas.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbTable {
	/**
	 * 表名称
	 * 
	 * @return
	 */
	public String name();
}
