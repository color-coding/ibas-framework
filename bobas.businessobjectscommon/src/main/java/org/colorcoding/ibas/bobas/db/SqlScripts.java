package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionAliasDataType;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public class SqlScripts implements ISqlScripts {

	private String companyId = null;

	protected String getCompanyId() {
		if (companyId == null)
			companyId = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_COMPANY, "CC");
		return companyId;
	}

	@Override
	public ISqlQuery getServerTimeQuery() {
		return new SqlQuery("SELECT GETDATE() 'NOW'");
	}

	@Override
	public String getDbObjectSign() {
		return "\"%s\"";
	}

	@Override
	public String getScriptBreakSign() {
		return "";
	}

	@Override
	public String getNullSign() {
		return "NULL";
	}

	@Override
	public String getCastTypeString(ConditionAliasDataType type) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%s");
		if (type != null) {
			if (type == ConditionAliasDataType.ALPHANUMERIC) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("NVARCHAR");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (type == ConditionAliasDataType.DATE) {
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
			} else if (type == ConditionAliasDataType.NUMERIC) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("INT");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (type == ConditionAliasDataType.DECIMAL) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("NUMERIC(19, 6)");
				stringBuilder.append(")");
				return stringBuilder.toString();
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public DbFieldType toDbFieldType(String dbType) {
		if (dbType.equalsIgnoreCase("nvarchar") || dbType.equalsIgnoreCase("ntext")) {
			return DbFieldType.ALPHANUMERIC;
		} else if (dbType.equalsIgnoreCase("int") || dbType.equalsIgnoreCase("smallint")
				|| dbType.equalsIgnoreCase("bigint")) {
			return DbFieldType.NUMERIC;
		} else if (dbType.equalsIgnoreCase("datetime")) {
			return DbFieldType.DATE;
		} else if (dbType.equalsIgnoreCase("numeric")) {
			return DbFieldType.DECIMAL;
		} else if (dbType.equalsIgnoreCase("bit")) {
			return DbFieldType.BYTES;
		}
		return DbFieldType.UNKNOWN;
	}

	@Override
	public String getFieldBreakSign() {
		return ", ";
	}

	@Override
	public String getAndSign() {
		return " AND ";
	}

	@Override
	public String getSqlString(ConditionRelationship value) {
		if (value == ConditionRelationship.AND) {
			return "AND";
		} else if (value == ConditionRelationship.OR) {
			return "OR";
		} else if (value == ConditionRelationship.NONE) {
			return this.getFieldBreakSign();
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String getSqlString(ConditionOperation value) {
		if (value == ConditionOperation.EQUAL) {
			return "=";
		} else if (value == ConditionOperation.NOT_EQUAL) {
			return "<>";
		} else if (value == ConditionOperation.GRATER_THAN) {
			return ">";
		} else if (value == ConditionOperation.LESS_THAN) {
			return "<";
		} else if (value == ConditionOperation.GRATER_EQUAL) {
			return ">=";
		} else if (value == ConditionOperation.LESS_EQUAL) {
			return "<=";
		} else if (value == ConditionOperation.IS_NULL) {
			return "IS NULL";
		} else if (value == ConditionOperation.NOT_NULL) {
			return "IS NOT NULL";
		} else if (value == ConditionOperation.CONTAIN) {
			return "LIKE";
		} else if (value == ConditionOperation.NOT_CONTAIN) {
			return "NOT LIKE";
		} else if (value == ConditionOperation.START) {
			return "LIKE";
		} else if (value == ConditionOperation.END) {
			return "LIKE";
		} else if (value == ConditionOperation.IN) {
			return "IN";
		} else if (value == ConditionOperation.NOT_IN) {
			return "NOT IN";
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	/**
	 * 数据的安全性检查
	 * 
	 * @param data
	 */
	protected String checkSecurity(String data) {
		if (data == null || data.isEmpty()) {
			return data;
		}
		// 处理单引号
		return data.replace("'", "''");
	}

	@Override
	public String getSqlString(ConditionOperation value, String opValue) {
		opValue = this.checkSecurity(opValue);
		if (value == ConditionOperation.CONTAIN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append("%");
			stringBuilder.append(opValue);
			stringBuilder.append("%");
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.NOT_CONTAIN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append("%");
			stringBuilder.append(opValue);
			stringBuilder.append("%");
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.END) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append("%");
			stringBuilder.append(opValue);
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.START) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append(opValue);
			stringBuilder.append("%");
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.NOT_EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.GRATER_EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.GRATER_THAN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.IS_NULL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			return stringBuilder.toString();
		} else if (value == ConditionOperation.NOT_NULL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			return stringBuilder.toString();
		} else if (value == ConditionOperation.LESS_EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.LESS_THAN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.IN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.NOT_IN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getSqlString(value));
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String getSqlString(DbFieldType type, String value) {
		if (value == null) {
			return this.getNullSign();
		}
		value = this.checkSecurity(value);
		StringBuilder stringBuilder = new StringBuilder();
		if (type == DbFieldType.NUMERIC || type == DbFieldType.DECIMAL) {
			stringBuilder.append(value);
		} else if (type == DbFieldType.DATE) {
			stringBuilder.append("'");
			stringBuilder.append(value);
			stringBuilder.append("'");
		} else {
			stringBuilder.append("N'");
			stringBuilder.append(value);
			stringBuilder.append("'");
		}
		return stringBuilder.toString();
	}

	@Override
	public String getSqlString(SortType value) {
		if (value == SortType.ASCENDING) {
			return "ASC";
		} else if (value == SortType.DESCENDING) {
			return "DESC";
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result) {
		StringBuilder stringBuilder = new StringBuilder();
		if (result >= 0) {
			stringBuilder.append("SELECT");
			stringBuilder.append(" ");
			stringBuilder.append("TOP");
			stringBuilder.append(" ");
			stringBuilder.append(result);
			stringBuilder.append(" ");
			stringBuilder.append(partSelect);
		} else {
			stringBuilder.append("SELECT");
			stringBuilder.append(" ");
			stringBuilder.append(partSelect);
		}
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
		return stringBuilder.toString();
	}

	@Override
	public String groupDeleteScript(String table, String partWhere) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DELETE");
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
	public String groupUpdateScript(String table, String partFieldValues, String partWhere) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		stringBuilder.append(partFieldValues);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append(partWhere);
		return stringBuilder.toString();
	}

	@Override
	public String groupInsertScript(String table, String partFields, String partValues) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT");
		stringBuilder.append(" ");
		stringBuilder.append("INTO");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		stringBuilder.append(" ");
		stringBuilder.append("(");
		stringBuilder.append(partFields);
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("VALUES");
		stringBuilder.append(" ");
		stringBuilder.append("(");
		stringBuilder.append(partValues);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	@Override
	public String getPrimaryKeyQuery(String boCode) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("\"AutoKey\"");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("\"%s_SYS_ONNM\"", this.getCompanyId()));
		stringBuilder.append(" ");
		stringBuilder.append("WITH (UPDLOCK)");
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("\"ObjectCode\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

	@Override
	public String getUpdatePrimaryKeyScript(String boCode, int addValue) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("\"%s_SYS_ONNM\"", this.getCompanyId()));
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		stringBuilder.append("\"AutoKey\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("\"AutoKey\"");
		stringBuilder.append(" ");
		stringBuilder.append("+");
		stringBuilder.append(" ");
		stringBuilder.append(addValue);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("\"ObjectCode\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("MAX");
		stringBuilder.append("(");
		stringBuilder.append(field);
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
		stringBuilder.append("EXEC");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("\"%s_SP_TRANSACTION_NOTIFICATION\"", this.getCompanyId()));
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
		return stringBuilder.toString();
	}

	@Override
	public String groupStoredProcedure(String spName, KeyValue[] parameters) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("EXEC");
		stringBuilder.append(" ");
		stringBuilder.append("\"");
		stringBuilder.append(spName);
		stringBuilder.append("\"");
		stringBuilder.append(" ");
		for (int i = 0; i < parameters.length; i++) {
			KeyValue keyValue = parameters[i];
			if (i > 0) {
				stringBuilder.append(",");
				stringBuilder.append(" ");
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
		return stringBuilder.toString();
	}

	@Override
	public String getSeriesKeyQuery(String boCode, int series) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("\"NextNum\"");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("\"Template\"");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("\"%s_SYS_NNM1\"", this.getCompanyId()));
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("\"ObjectCode\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		stringBuilder.append(" ");
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("\"Series\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append(series);
		stringBuilder.append(" ");
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("\"Locked\"");
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
		stringBuilder.append(String.format("\"%s_SYS_NNM1\"", this.getCompanyId()));
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		stringBuilder.append("\"NextNum\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("\"NextNum\"");
		stringBuilder.append(" ");
		stringBuilder.append("+");
		stringBuilder.append(" ");
		stringBuilder.append(addValue);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("\"ObjectCode\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		stringBuilder.append(" ");
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("\"Series\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append(series);
		stringBuilder.append(" ");
		stringBuilder.append("AND");
		stringBuilder.append(" ");
		stringBuilder.append("\"Locked\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append("N");
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

}
