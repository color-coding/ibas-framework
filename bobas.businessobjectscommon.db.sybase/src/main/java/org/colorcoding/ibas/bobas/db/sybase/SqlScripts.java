package org.colorcoding.ibas.bobas.db.sybase;

import org.colorcoding.ibas.bobas.data.KeyValue;

public class SqlScripts extends org.colorcoding.ibas.bobas.db.SqlScripts {

	@Override
	public String getTransactionNotificationQuery(String boCode, String type, int keyCount, String keyNames,
			String keyValues) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("\"%s_SP_TRANSACTION_NOTIFICATION\"", this.getCompanyId()));
		stringBuilder.append("(");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(type));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append(keyCount);
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(keyNames));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(keyValues));
		stringBuilder.append("'");
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	@Override
	public String groupStoredProcedure(String spName, KeyValue[] parameters) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append("\"");
		stringBuilder.append(spName);
		stringBuilder.append("\"(");
		for (int i = 0; i < parameters.length; i++) {
			KeyValue keyValue = parameters[i];
			if (i > 0) {
				stringBuilder.append(", ");
			}
			if (keyValue.getValue() == null) {
				stringBuilder.append("''");
			} else if (keyValue.getValue().getClass().equals(Integer.class)) {
				stringBuilder.append(String.valueOf(keyValue.getValue()));
			} else if (keyValue.getValue().getClass().equals(Double.class)) {
				stringBuilder.append(String.valueOf(keyValue.getValue()));
			} else {
				stringBuilder.append("N'");
				stringBuilder.append(this.checkSecurity(String.valueOf(keyValue.getValue())));
				stringBuilder.append("'");
			}
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
