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
	 * @return 字段名
	 */
	public String name();

	/**
	 * 是否主键
	 *
	 * @return true表示主键
	 */
	public boolean primaryKey() default Booleans.VALUE_FALSE;

	/**
	 * 是否唯一键
	 *
	 * @return true表示唯一键
	 */
	public boolean uniqueKey() default Booleans.VALUE_FALSE;

}
