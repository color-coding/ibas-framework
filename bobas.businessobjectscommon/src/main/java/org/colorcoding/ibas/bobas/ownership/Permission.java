package org.colorcoding.ibas.bobas.ownership;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限标记
 * 
 * @author Niuren.Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
	/**
	 * 默认值
	 * 
	 * @return
	 */
	public PermissionValue defaultValue() default PermissionValue.available;

	/**
	 * 分组（默认类标记）
	 * 
	 * @return
	 */
	public String group() default "";

	/**
	 * 名称（默认方法名）
	 * 
	 * @return
	 */
	public String name() default "";
}
