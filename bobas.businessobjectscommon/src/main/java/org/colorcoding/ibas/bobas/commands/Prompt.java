package org.colorcoding.ibas.bobas.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 命令的提示符
 * 
 * @author Niuren.Zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Prompt {
	/**
	 * 提示符
	 * 
	 * @return
	 */
	public String value();

}
