package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.common.ISqlQuery;

/**
 * sql语句检查员
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISqlScriptInspector {
	/**
	 * 检查语句
	 * 
	 * @throws SecurityException
	 * @throws SqlScriptException
	 */
	void check(ISqlQuery sql) throws SecurityException, SqlScriptException;
}
