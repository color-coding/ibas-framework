package org.colorcoding.ibas.bobas.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbField {

	/**
	 * 字段名称
	 * 
	 * @return
	 */
	public String name();

	/**
	 * 数据类型
	 * 
	 * @return
	 */
	public DbFieldType type();

	/**
	 * 表名称
	 * 
	 * @return
	 */
	public String table() default "";

	/**
	 * 是否主键？
	 * 
	 * @return
	 */
	public boolean primaryKey() default false;

	/**
	 * 是否保存？
	 * 
	 * @return
	 */
	public boolean savable() default true;

}
