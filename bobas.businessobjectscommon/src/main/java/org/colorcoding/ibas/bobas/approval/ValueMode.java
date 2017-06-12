package org.colorcoding.ibas.bobas.approval;

/**
 * 属性取值方式
 * 
 * @author Niuren.Zhu
 *
 */
public enum ValueMode {
	/**
	 * 属性取值
	 */
	PROPERTY,
	/**
	 * 数据库字段取值
	 */
	DB_FIELD,
	/**
	 * 输入值
	 */
	INPUT,
	/**
	 * sql脚本取值
	 */
	SQL_SCRIPT
}
