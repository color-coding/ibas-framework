package org.colorcoding.ibas.bobas.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关联的字段
 * 
 * @author niuren.zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AssociatedField {
	/**
	 * 关联的字段名称
	 * 
	 * @return
	 */
	public String field();

	/**
	 * 映射的字段名称（空则认为与name相同）
	 * 
	 * @return
	 */
	public String mapped() default "";

}
