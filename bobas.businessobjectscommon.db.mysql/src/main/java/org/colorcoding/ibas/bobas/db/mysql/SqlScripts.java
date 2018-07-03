package org.colorcoding.ibas.bobas.db.mysql;

import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

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
	public ISqlQuery getServerTimeQuery() {
		return new SqlQuery("now()");
	}

	/**
	 * 将字段转换成条件字段的类型
	 */
	@Override
	public String getCastTypeString(DbFieldType dbFieldType) {
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
	public DbFieldType toDbFieldType(String dbType) {
		if (dbType.equalsIgnoreCase("varchar") || dbType.equalsIgnoreCase("text")) {
			return DbFieldType.ALPHANUMERIC;
		} else if (dbType.equalsIgnoreCase("int") || dbType.equalsIgnoreCase("smallint")) {
			return DbFieldType.NUMERIC;
		} else if (dbType.equalsIgnoreCase("datetime")) {
			return DbFieldType.DATE;
		} else if (dbType.equalsIgnoreCase("decimal")) {
			return DbFieldType.DECIMAL;
		}
		return DbFieldType.UNKNOWN;
	}

	/**
	 * 获取特定行数的数据
	 */
	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result) {
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
	public String getPrimaryKeyQuery(String boCode) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("`AutoKey`");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("`%s_SYS_ONNM`", this.getCompanyId()));
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
	public String getUpdatePrimaryKeyScript(String boCode, int addValue) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("`%s_SYS_ONNM`", this.getCompanyId()));
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
	public String groupMaxValueQuery(String field, String table, String partWhere) {
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
	public String getTransactionNotificationQuery(String boCode, String type, int keyCount, String keyNames,
			String keyValues) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("`%s_SP_TRANSACTION_NOTIFICATION`", this.getCompanyId()));
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
	public String groupStoredProcedure(String spName, KeyValue[] parameters) {
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

	@Override
	public String getSeriesKeyQuery(String boCode, int series) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("`NextNum`");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("`Template`");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("`%s_SYS_NNM1`", this.getCompanyId()));
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
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("`Series`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append(series);
		stringBuilder.append(" ");
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("`Locked`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append("N");
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

	@Override
	public String getUpdateSeriesKeyScript(String boCode, int series, int addValue) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("`%s_SYS_NNM1`", this.getCompanyId()));
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		stringBuilder.append("`NextNum`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("`NextNum`");
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
		stringBuilder.append(" ");
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("`Series`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append(series);
		stringBuilder.append(" ");
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("`Locked`");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append("N");
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

}
