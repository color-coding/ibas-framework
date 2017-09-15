package org.colorcoding.ibas.bobas.db;

import java.util.regex.Pattern;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class SqlScriptInspector implements ISqlScriptInspector {

	@Override
	public void check(ISqlQuery sql) throws SecurityException, SqlScriptsException {
		if (sql == null) {
			return;
		}
		if (sql.getQueryString() == null || sql.getQueryString().isEmpty()) {
			throw new SqlScriptsException(I18N.prop("msg_bobas_invalid_sql_query"));
		}
		if (!sql.isAllowWrite()) {
			String regex = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_INSPECTOR_SQL_WRITE_REGEX);
			if (regex == null || regex.isEmpty()) {
				regex = "(\\b(update|delete|insert|trancate|into|ascii|drop)\\b)";
			}
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(sql.getQueryString()).find()) {
				throw new SecurityException(I18N.prop("msg_bobas_not_authorized_sql", sql.getQueryString()));
			}
		}
	}

}
