package org.colorcoding.ibas.bobas.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbProcedure {

	/**
	 * 名称
	 * 
	 * @return
	 */
	public String name();

}
