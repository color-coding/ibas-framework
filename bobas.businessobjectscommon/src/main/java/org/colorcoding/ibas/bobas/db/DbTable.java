package org.colorcoding.ibas.bobas.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.colorcoding.ibas.bobas.common.Booleans;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbTable {
	/**
	 * 数据库表名称（支持${Variable}变量替换）
	 *
	 * @return 表名
	 */
	public String name();

	/**
	 * 是否实体表（实体表参与数据删除和更新操作）
	 *
	 * @return 是否实体表（默认true）
	 */
	public boolean entity() default Booleans.VALUE_TRUE;
}
