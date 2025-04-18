package org.colorcoding.ibas.bobas.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {
	/**
	 * 字段名称
	 * 
	 * @return
	 */
	public String name();

	/**
	 * 是否主键
	 * 
	 * @return
	 */
	public boolean primaryKey() default Booleans.VALUE_FALSE;

	/**
	 * 是否索引键
	 * 
	 * @return
	 */
	public boolean uniqueKey() default Booleans.VALUE_FALSE;

}
