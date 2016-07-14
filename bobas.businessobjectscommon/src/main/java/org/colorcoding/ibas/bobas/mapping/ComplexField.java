package org.colorcoding.ibas.bobas.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 复合类型字段（多个DbField组成）
 * 
 * @author Niuren.Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ComplexField {
	/**
	 * 组字段名称
	 * 
	 * @return
	 */
	public String name();

	/**
	 * 数据类型
	 * 
	 * @return
	 */
	public ComplexFieldType type();

	/**
	 * 表名称
	 * 
	 * @return
	 */
	public String table() default "";

	/**
	 * 是否保存？
	 * 
	 * @return
	 */
	public boolean savable() default true;

	/**
	 * 索引 顺序
	 * 
	 * @return
	 */
	public int order() default -1;
}
