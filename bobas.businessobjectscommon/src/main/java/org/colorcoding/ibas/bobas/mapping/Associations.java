package org.colorcoding.ibas.bobas.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 关联字段集合
 * 
 * @author niuren.zhu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Associations {
	/**
	 * 关联内容
	 * 
	 * @return
	 */
	public AssociatedField[] value();

	/**
	 * 关联类型
	 * 
	 * @return
	 */
	public Class<?> type();

	/**
	 * 关联方式
	 * 
	 * @return
	 */
	public AssociationMode mode() default AssociationMode.OneToZero;

	/**
	 * 可否保存
	 * 
	 * @return
	 */
	public boolean isSavable() default false;

	/**
	 * 自动加载
	 * 
	 * @return
	 */
	public boolean autoLoading() default true;
}
