package org.colorcoding.ibas.bobas.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务对象单元
 * 
 * @author Niuren.Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BusinessObjectUnit {
	/**
	 * 编码
	 * 
	 * @return
	 */
	public String code();

	/**
	 * 名称
	 * 
	 * @return
	 */
	public String name() default "";
}
