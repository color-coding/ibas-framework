package org.colorcoding.ibas.bobas.core.fields;

/**
 * 字段数据
 */
public interface IFieldData {

	/**
	 * 名称
	 */
	String getName();

	/**
	 * 值
	 */
	Object getValue();

	/**
	 * 设置值
	 *
	 * @param value 新值
	 * @return true表示值已修改
	 */
	boolean setValue(Object value);

	/**
	 * 值的类型
	 */
	java.lang.Class<?> getValueType();

	/**
	 * 是否可保存（有数据库字段映射）
	 */
	boolean isSavable();

	/**
	 * 主键
	 */
	boolean isPrimaryKey();

	/**
	 * 索引键
	 */
	boolean isUniqueKey();

	/**
	 * 是否原始数据（无数据库字段映射）
	 */
	boolean isOriginal();

	/**
	 * 是否修改过
	 *
	 * @return true表示值已修改
	 */
	boolean isDirty();
}
