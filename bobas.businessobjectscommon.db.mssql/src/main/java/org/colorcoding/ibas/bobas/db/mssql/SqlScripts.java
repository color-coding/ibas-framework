package org.colorcoding.ibas.bobas.db.mssql;

import org.colorcoding.ibas.bobas.common.ConditionAliasDataType;

public class SqlScripts extends org.colorcoding.ibas.bobas.db.SqlScripts {

	@Override
	public String getCastTypeString(ConditionAliasDataType type) {
		if (type == ConditionAliasDataType.ALPHANUMERIC) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("CONVERT");
			stringBuilder.append("(");
			stringBuilder.append("NVARCHAR");
			stringBuilder.append(",");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			stringBuilder.append(",");
			stringBuilder.append(" ");
			stringBuilder.append("120");
			stringBuilder.append(")");
			return stringBuilder.toString();
		} else {
			return super.getCastTypeString(type);
		}
	}
}
