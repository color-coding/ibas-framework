package org.colorcoding.ibas.bobas.ownership;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限分组
 * 
 * @author Niuren.Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PermissionGroup {
	/**
	 * 组名称
	 * 
	 * @return
	 */
	public String value();
}
