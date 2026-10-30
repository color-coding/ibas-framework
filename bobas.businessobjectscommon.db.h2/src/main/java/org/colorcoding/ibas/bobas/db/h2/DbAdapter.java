package org.colorcoding.ibas.bobas.db.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DataType;
import org.colorcoding.ibas.bobas.db.MaxValue;
import org.colorcoding.ibas.bobas.exception.BasRuntimeException;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			Class.forName("org.h2.Driver");
			String dbURL = "jdbc:h2:" + dbName;
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (SQLException e) {
			Logger.log(MessageLevel.ERROR, e);
			throw new BasRuntimeException(this.translateException(e), e);
		} catch (Exception e) {
			Logger.log(MessageLevel.ERROR, e);
			throw new BasRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 返回SQL类型；value为null时返回Types.NULL，否则委托给基类按值细化后的映射。
	 *
	 * @param type  数据字段类型
	 * @param value 当前值，null时返回NULL类型
	 * @return 对应的java.sql.Types常量
	 */
	@Override
	public int sqlTypeOf(DataType type, Object value) {
		if (value == null) {
			return java.sql.Types.NULL;
		}
		return super.sqlTypeOf(type, value);
	}

	@Override
	public String castAs(DataType type, String alias) {
		StringBuilder stringBuilder = new StringBuilder(64);
		if (type == DataType.ALPHANUMERIC) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("VARCHAR");
			stringBuilder.append(")");
		} else if (type == DataType.DATE) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("TIMESTAMP");
			stringBuilder.append(")");
		} else if (type == DataType.NUMERIC) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("INTEGER");
			stringBuilder.append(")");
			return stringBuilder.toString();
		} else if (type == DataType.DECIMAL) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("NUMERIC(19, 6)");
			stringBuilder.append(")");
		} else {
			stringBuilder.append(this.identifier(alias));
		}
		return stringBuilder.toString();
	}

	@Override
	public String parsing(ConditionOperation value) {
		if (value == ConditionOperation.NOT_EQUAL) {
			return "!=";
		} else {
			return super.parsing(value);
		}
	}

	@Override
	public String parsingSelect(Class<?> boType, ICriteria criteria, boolean withLock) {
		StringBuilder stringBuilder = new StringBuilder(
				(3 + criteria.getConditions().size() + criteria.getSorts().size()) * 32);
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("*");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier(this.table(boType)));
		if (criteria.getConditions().size() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append(this.where());
			stringBuilder.append(" ");
			stringBuilder.append(this.parsingWhere(criteria.getConditions()));
		}
		if (criteria.getSorts().size() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("ORDER BY");
			stringBuilder.append(" ");
			stringBuilder.append(this.parsingOrder(criteria.getSorts()));
		}
		if (criteria.getResultCount() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("LIMIT");
			stringBuilder.append(" ");
			stringBuilder.append(criteria.getResultCount());
		}
		if (withLock) {
			stringBuilder.append(" ");
			stringBuilder.append("FOR UPDATE");
		}
		return stringBuilder.toString();
	}

	@Override
	public String parsingMaxValue(MaxValue maxValue, Collection<ICondition> conditions) {
		StringBuilder stringBuilder = new StringBuilder(conditions.size() * 32 + 96);
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("Max");
		stringBuilder.append("(");
		stringBuilder.append(this.identifier(maxValue.getKeyField().getName()));
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier(this.table(maxValue.getType())));
		stringBuilder.append(" ");
		stringBuilder.append(this.where());
		stringBuilder.append(" ");
		stringBuilder.append(this.parsingWhere(conditions));
		return stringBuilder.toString();
	}

	@Override
	public String parsingStoredProcedure(String spName, String... args) {
		String name = MyConfiguration.applyVariables(spName);
		StringBuilder sql = new StringBuilder();
		if (name.equalsIgnoreCase(this.getCompanyId() + "_SP_TRANSACTION_NOTIFICATION")) {
			sql.append("SELECT * FROM ").append(this.identifier(name));
			if (args.length > 0) {
				sql.append(" WHERE ");
				for (int i = 0; i < args.length; i++) {
					if (i > 0) {
						sql.append(" OR ");
					}
					sql.append("? IS NULL OR TRUE");
				}
			}
		} else if ((name.equalsIgnoreCase(this.getCompanyId() + "_SYS_SP_GET_USER_MODULES")
				|| name.equalsIgnoreCase(this.getCompanyId() + "_SYS_SP_GET_USER_PRIVILEGES")) && args.length == 2) {
			sql.append("SELECT * FROM ").append(this.identifier(name))
					.append(" WHERE \"Platform\" = ? AND \"UserCode\" = ?");
		} else {
			sql.append("CALL ").append(this.identifier(name)).append("(");
			for (int i = 0; i < args.length; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				sql.append("?");
			}
			sql.append(")");
		}
		return sql.toString();
	}
}
