package org.colorcoding.ibas.bobas.db;

import java.util.regex.Pattern;

import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.i18n.i18n;

public class SqlScriptInspector implements ISqlScriptInspector {

	@Override
	public void check(ISqlQuery sql) throws SecurityException, SqlScriptsException {
		if (sql == null) {
			return;
		}
		if (sql.getQueryString() == null || sql.getQueryString().isEmpty()) {
			throw new SqlScriptsException(i18n.prop("msg_bobas_invalid_sql_query"));
		}
		if (!sql.isAllowWrite()) {
			String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
					+ "(\\b(update|delete|insert|trancate|into|ascii|drop)\\b)";
			Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(sql.getQueryString()).find()) {
				throw new SecurityException(i18n.prop("msg_bobas_not_authorized_sql", sql.getQueryString()));
			}
		}
	}

}
