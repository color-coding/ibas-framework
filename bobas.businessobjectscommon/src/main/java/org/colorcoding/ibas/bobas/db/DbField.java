package org.colorcoding.ibas.bobas.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.colorcoding.ibas.bobas.common.Booleans;
import org.colorcoding.ibas.bobas.common.Strings;

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
	public String table() default Strings.VALUE_EMPTY;

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
