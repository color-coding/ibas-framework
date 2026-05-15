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
	 * 数据库字段名称
	 *
	 * @return 字段名
	 */
	public String name();

	/**
	 * 数据库字段类型
	 *
	 * @return 字段类型
	 */
	public DbFieldType type();

	/**
	 * 数据库表名称（支持${Variable}变量替换）
	 *
	 * @return 表名（默认空字符串）
	 */
	public String table() default Strings.VALUE_EMPTY;

	/**
	 * 是否主键
	 *
	 * @return 是否主键（默认false）
	 */
	public boolean primaryKey() default Booleans.VALUE_FALSE;

	/**
	 * 是否唯一键
	 *
	 * @return 是否唯一键（默认false）
	 */
	public boolean uniqueKey() default Booleans.VALUE_FALSE;

}
