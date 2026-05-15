package org.colorcoding.ibas.bobas.bo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 业务对象单元标识注解
 *
 * @author Niuren.Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BusinessObjectUnit {
	/**
	 * 业务对象编码（支持${Variable}变量替换）
	 *
	 * @return 编码值
	 */
	public String code();

	/**
	 * 业务对象名称
	 *
	 * @return 名称（默认空字符串）
	 */
	public String name() default Strings.VALUE_EMPTY;
}
