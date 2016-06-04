package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.common.Conditions;

/**
 * 查询条件为数据库的适配器
 */
public interface ICriteriaAdapter4Db {
	/**
	 * 获取sql脚本
	 */
	String getSqlScripts(Conditions conditions);
}
