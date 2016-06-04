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
	 * @return 是否修改
	 */
	boolean setValue(Object value);

	/**
	 * 值的类型
	 */
	java.lang.Class<?> getValueType();

	/**
	 * 是否保存
	 * 
	 */
	boolean isSavable();

	/**
	 * 主键
	 */
	boolean isPrimaryKey();

	/**
	 * 附件的数据，如定义字段
	 */
	boolean isOriginal();

	/**
	 * 是否联动，响应父项的变化
	 * 
	 */
	boolean isLinkage();

	/**
	 * 是否修改过
	 * 
	 */
	boolean isDirty();

	/**
	 * 置为未修改过
	 */
	void markOld();

}
