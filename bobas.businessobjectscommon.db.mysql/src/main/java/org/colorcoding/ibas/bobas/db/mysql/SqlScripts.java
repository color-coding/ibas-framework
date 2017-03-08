package org.colorcoding.ibas.bobas.db.mysql;

import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.db.SqlScriptsException;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.util.StringBuilder;

public class SqlScripts extends org.colorcoding.ibas.bobas.db.SqlScripts {

	/**
	 * 无参构造函数
	 */
	public SqlScripts() {

	}

	@Override
	public String getDbObjectSign() {
		return "`%s`";
	}

	/**
	 * 获取当前日期时间
	 */
	@Override
	public ISqlQuery getServerTimeScript() {
		return new SqlQuery("now()");
	}

	/**
	 * 将字段转换成条件字段的类型
	 */
	@Override
	public String getFieldValueCastType(DbFieldType dbFieldType) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%s");
		if (dbFieldType != null) {
			if (dbFieldType == DbFieldType.ALPHANUMERIC) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("CHAR");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (dbFieldType == DbFieldType.DATE) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("DATETIME");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (dbFieldType == DbFieldType.NUMERIC) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("SIGNED");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (dbFieldType == DbFieldType.DECIMAL) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("DECIMAL");
				stringBuilder.append(")");
				return stringBuilder.toString();
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public DbFieldType getDbFieldType(String dbType) {
		if (dbType.equals("varchar") || dbType.equals("text")) {
			return DbFieldType.ALPHANUMERIC;
		} else if (dbType.equals("int") || dbType.equals("smallint")) {
			return DbFieldType.NUMERIC;
		} else if (dbType.equals("datetime")) {
			return DbFieldType.DATE;
		} else if (dbType.equals("decimal")) {
			return DbFieldType.DECIMAL;
		}
		return DbFieldType.UNKNOWN;
	}

	/**
	 * 获取特定行数的数据
	 */
	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result)
			throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append(partSelect);
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		if (partWhere != null && !partWhere.isEmpty()) {
			stringBuilder.append(" ");
			stringBuilder.append("WHERE");
			stringBuilder.append(" ");
			stringBuilder.append(partWhere);
		}
		if (partOrder != null && !partOrder.isEmpty()) {
			stringBuilder.append(" ");
			stringBuilder.append("ORDER BY");
			stringBuilder.append(" ");
			stringBuilder.append(partOrder);
		}
		if (result >= 0) {
			stringBuilder.append(" ");
			stringBuilder.append("LIMIT");
			stringBuilder.append(" ");
			stringBuilder.append(result);
		}
		return stringBuilder.toString();
	}

	/**
	 * 获得主键
	 */
	@Override
	public String getBOPrimaryKeyQuery(String boCode) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("`AutoKey`");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.appendFormat("`%s_SYS_ONNM`", this.getCompanyId());
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("`ObjectCode`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		stringBuilder.append(" ");
		stringBuilder.append("FOR UPDATE");
		return stringBuilder.toString();
	}

	/**
	 * 更新主键
	 */
	@Override
	public String getUpdateBOPrimaryKeyScript(String boCode, int addValue) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.appendFormat("`%s_SYS_ONNM`", this.getCompanyId());
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		stringBuilder.append("`AutoKey`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("`AutoKey`");
		stringBuilder.append(" ");
		stringBuilder.append("+");
		stringBuilder.append(" ");
		stringBuilder.append(addValue);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("`ObjectCode`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

	/**
	 * 获取某一列的最大值，如果为空返回1
	 */
	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("IFNULL");
		stringBuilder.append("(");
		stringBuilder.append("MAX");
		stringBuilder.append("(");
		stringBuilder.append(field);
		stringBuilder.append(")");
		stringBuilder.append(",");
		stringBuilder.append("0");
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append(partWhere);
		return stringBuilder.toString();
	}

	@Override
	public String getBOTransactionNotificationScript(String boCode, String type, int keyCount, String keyNames,
			String keyValues) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.appendFormat("`%s_SP_TRANSACTION_NOTIFICATION`", this.getCompanyId());
		stringBuilder.append("(");
		stringBuilder.append(" ");
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
	public String groupStoredProcedure(String spName, KeyValue[] parameters) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append("`");
		stringBuilder.append(spName);
		stringBuilder.append("`(");
		for (int i = 0; i < parameters.length; i++) {
			KeyValue keyValue = parameters[i];
			if (i > 0) {
				stringBuilder.append(", ");
			}
			if (keyValue.value == null) {
				stringBuilder.append("''");
			} else if (keyValue.value.getClass().equals(Integer.class)) {
				stringBuilder.append(String.valueOf(keyValue.value));
			} else if (keyValue.value.getClass().equals(Double.class)) {
				stringBuilder.append(String.valueOf(keyValue.value));
			} else {
				stringBuilder.append("N'");
				stringBuilder.append(this.checkSecurity(String.valueOf(keyValue.value)));
				stringBuilder.append("'");
			}
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
