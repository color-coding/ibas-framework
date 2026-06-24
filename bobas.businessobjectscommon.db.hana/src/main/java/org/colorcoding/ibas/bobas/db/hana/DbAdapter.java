package org.colorcoding.ibas.bobas.db.hana;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DataType;
import org.colorcoding.ibas.bobas.db.MaxValue;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			Class.forName("com.sap.db.jdbc.Driver");
			if (server.indexOf(":") < 0) {
				// 没有添加端口
				server = String.format("%s:30015", server);
			}
			String dbURL = String.format("jdbc:sap://%s/?currentschema=\"%s\"", server, dbName);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			// 接数据库失败
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回SQL值；字符串类型添加N前缀以支持Unicode
	 */
	@Override
	public String sqlValueOf(Object value, int sqlType) {
		String sqlValue = super.sqlValueOf(value, sqlType);
		if (value == null || "NULL".equals(sqlValue)) {
			return sqlValue;
		}
		if (java.sql.Types.VARCHAR == sqlType || java.sql.Types.NVARCHAR == sqlType
				|| java.sql.Types.LONGNVARCHAR == sqlType || java.sql.Types.LONGVARCHAR == sqlType
				|| java.sql.Types.CHAR == sqlType || java.sql.Types.CLOB == sqlType || java.sql.Types.NCHAR == sqlType
				|| java.sql.Types.NCLOB == sqlType) {
			sqlValue = "N" + sqlValue;
		}
		return sqlValue;
	}

	@Override
	public String castAs(DataType type, String alias) {
		StringBuilder stringBuilder = new StringBuilder(64);
		if (type == DataType.ALPHANUMERIC) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("NVARCHAR");
			stringBuilder.append(")");
		} else if (type == DataType.DATE) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("DATE");
			stringBuilder.append(")");
		} else if (type == DataType.NUMERIC) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("INTEGER");
			stringBuilder.append(")");
			return stringBuilder.toString();
		} else if (type == DataType.DECIMAL) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("DECIMAL(19, 6)");
			stringBuilder.append(")");
		} else {
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
		}
		return stringBuilder.toString();
	}

	public String parsingSelect(Class<?> boType, ICriteria criteria, boolean withLock) {
		StringBuilder stringBuilder = new StringBuilder(
				(3 + criteria.getConditions().size() + criteria.getSorts().size()) * 32);
		stringBuilder.append("SELECT");
		if (criteria.getResultCount() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("TOP");
			stringBuilder.append(" ");
			stringBuilder.append(criteria.getResultCount());
		}
		stringBuilder.append(" ");
		stringBuilder.append("*");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(this.table(boType));
		stringBuilder.append(this.identifier());
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
		if (withLock) {
			stringBuilder.append(" ");
			stringBuilder.append("FOR UPDATE");
		}
		return stringBuilder.toString();
	}

	public String parsingMaxValue(MaxValue maxValue, Collection<ICondition> conditions) {
		StringBuilder stringBuilder = new StringBuilder(conditions.size() * 32 + 96);
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("Max");
		stringBuilder.append("(");
		stringBuilder.append(this.identifier());
		stringBuilder.append(maxValue.getKeyField().getName());
		stringBuilder.append(this.identifier());
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(this.table(maxValue.getType()));
		stringBuilder.append(this.identifier());
		stringBuilder.append(" ");
		stringBuilder.append(this.where());
		stringBuilder.append(" ");
		stringBuilder.append(this.parsingWhere(conditions));
		return stringBuilder.toString();
	}

	@Override
	public String parsingStoredProcedure(String spName, String... args) {
		StringBuilder stringBuilder = new StringBuilder(spName.length() + args.length * 16 + 32);
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(MyConfiguration.applyVariables(spName));
		stringBuilder.append(this.identifier());
		if (args.length > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("(");
			int count = stringBuilder.length();
			for (String arg : args) {
				if (stringBuilder.length() > count) {
					stringBuilder.append(this.separation());
				}
				if (Strings.isNullOrEmpty(arg)) {
					stringBuilder.append("?");
				} else {
					stringBuilder.append(arg);
					stringBuilder.append(" ");
					stringBuilder.append("=");
					stringBuilder.append(" ");
					stringBuilder.append("?");
				}
			}
			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}

	/**
	 * 翻译 SAP HANA 异常为友好的国际化消息。
	 * <p>
	 * 按 ErrorCode 识别常见错误：
	 * <ul>
	 * <li>301：unique constraint violated（唯一约束）</li>
	 * <li>461：foreign key constraint violation - not allowed to
	 * insert/update（外键约束）</li>
	 * <li>462：foreign key constraint violation - cannot delete/update
	 * parent（外键约束）</li>
	 * <li>287：cannot insert NULL value into column（非空约束）</li>
	 * <li>261：invalid column name（无效的列名）</li>
	 * <li>259：invalid table name（无效的对象）</li>
	 * <li>268：column store error: data conversion / 数据类型转换失败</li>
	 * <li>257：sql syntax error（语法错误）</li>
	 * </ul>
	 * 兼容 {@link java.sql.BatchUpdateException}：会沿
	 * {@link SQLException#getNextException()} 链向下查找真正的错误码。未匹配时回退到基类实现（返回异常原始消息）。
	 *
	 * @param exception 数据库异常
	 * @return 国际化消息；输入为 null 时调用基类方法
	 */
	@Override
	public String translateException(SQLException exception) {
		if (exception == null) {
			return super.translateException(exception);
		}
		// 沿 getNextException 链查找最具体的错误码（兼容 BatchUpdateException）
		SQLException effective = exception;
		int code = effective.getErrorCode();
		if (code == 0) {
			SQLException next = exception.getNextException();
			while (next != null) {
				if (next.getErrorCode() != 0) {
					effective = next;
					code = next.getErrorCode();
					break;
				}
				next = next.getNextException();
			}
		}
		String message = effective.getMessage();
		if (message == null) {
			message = Strings.VALUE_EMPTY;
		}
		switch (code) {
		case 301: {
			String value = this.extractDuplicateValue(message);
			return Strings.isNullOrEmpty(value) ? I18N.prop("msg_bobas_db_duplicate_key")
					: I18N.prop("msg_bobas_db_duplicate_key_value", value);
		}
		case 461:
		case 462:
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		case 287:
			return I18N.prop("msg_bobas_db_not_null_violation");
		case 261: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_column")
					: I18N.prop("msg_bobas_db_invalid_column_name", name);
		}
		case 259: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_object")
					: I18N.prop("msg_bobas_db_invalid_object_name", name);
		}
		case 264:
			return I18N.prop("msg_bobas_db_data_truncation");
		case 266:
		case 268:
			return I18N.prop("msg_bobas_db_data_conversion_failed");
		case 257:
			return I18N.prop("msg_bobas_db_syntax_error");
		default:
			return super.translateException(exception);
		}
	}

	/**
	 * 从 SAP HANA 异常消息中提取重复键值。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * unique constraint violated: ... key (Z00002)
	 * </pre>
	 * 
	 * 提取规则：取消息末尾最后一对小括号内的内容。
	 *
	 * @param message 异常消息
	 * @return 重复键值；解析失败返回 null
	 */
	private String extractDuplicateValue(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		int end = message.lastIndexOf(")");
		int start = message.lastIndexOf("(", end);
		if (start >= 0 && end > start) {
			String value = message.substring(start + 1, end).trim();
			if (!Strings.isNullOrEmpty(value)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * 从异常消息中提取首个被双引号或单引号包裹的名称。
	 *
	 * @param message 异常消息
	 * @return 名称；解析失败返回 null
	 */
	private String extractQuotedName(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		for (char quote : new char[] { '"', '\'' }) {
			int start = message.indexOf(quote);
			if (start >= 0) {
				int end = message.indexOf(quote, start + 1);
				if (end > start) {
					String value = message.substring(start + 1, end);
					if (!Strings.isNullOrEmpty(value)) {
						return value;
					}
				}
			}
		}
		return null;
	}
}
