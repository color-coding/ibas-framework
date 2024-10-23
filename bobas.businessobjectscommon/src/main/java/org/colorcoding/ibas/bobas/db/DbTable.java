package org.colorcoding.ibas.bobas.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.colorcoding.ibas.bobas.common.Booleans;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbTable {
	/**
	 * 表名称
	 * 
	 * @return
	 */
	public String name();

	/**
	 * 实体表
	 * 
	 * @return
	 */
	public boolean entity() default Booleans.VALUE_TRUE;
}
