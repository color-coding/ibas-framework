package org.colorcoding.ibas.bobas.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TimeZone;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DataType;
import org.colorcoding.ibas.bobas.db.MaxValue;
import org.colorcoding.ibas.bobas.db.SqlStatement;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String timeZone = TimeZone.getDefault().getID();
			if (timeZone == null || timeZone.isEmpty()) {
				timeZone = "UTC";
			}
			String dbURL = String.format(
					"jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=%s&useServerPrepStmts=true&cachePrepStmts=true",
					server, dbName, java.net.URLEncoder.encode(timeZone, "UTF-8"));
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG,
						Strings.format("db adapter: %s", java.net.URLDecoder.decode(dbURL, "UTF-8")));
			}
			Connection connection = DriverManager.getConnection(dbURL, userName, userPwd);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			return connection;
		} catch (Exception e) {
			// 接数据库失败
			throw new RuntimeException(e);
		}
	}

	public String identifier() {
		return "`";
	}

	public String escape() {
		// 改变默认"\"字符
		return "/";
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
			stringBuilder.append("CHAR");
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
			stringBuilder.append("DATETIME");
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
			stringBuilder.append("SIGNED");
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
	 * 解析SQL语句；对反斜杠进行双写转义
	 */
	@Override
	public String parsing(SqlStatement sqlStatement) {
		// 处理转义字符
		return Strings.replace(super.parsing(sqlStatement), "\\", "\\\\");
	}

	/**
	 * 翻译 MySQL 异常为友好的国际化消息。
	 * <p>
	 * 按 ErrorCode 识别常见错误：
	 * <ul>
	 * <li>1062：Duplicate entry（唯一键冲突）</li>
	 * <li>1451：Cannot delete or update a parent row（外键约束）</li>
	 * <li>1452：Cannot add or update a child row（外键约束）</li>
	 * <li>1048：Column cannot be null（非空约束）</li>
	 * <li>1054：Unknown column（无效的列名）</li>
	 * <li>1146：Table doesn't exist（无效的对象）</li>
	 * <li>1406：Data too long for column（数据被截断）</li>
	 * <li>1366：Incorrect value for column（数据类型转换失败）</li>
	 * <li>1064：SQL syntax error（语法错误）</li>
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
		case 1062: {
			String value = this.extractDuplicateValue(message);
			return Strings.isNullOrEmpty(value) ? I18N.prop("msg_bobas_db_duplicate_key")
					: I18N.prop("msg_bobas_db_duplicate_key_value", value);
		}
		case 1451:
		case 1452:
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		case 1048:
			return I18N.prop("msg_bobas_db_not_null_violation");
		case 1054: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_column")
					: I18N.prop("msg_bobas_db_invalid_column_name", name);
		}
		case 1146: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_object")
					: I18N.prop("msg_bobas_db_invalid_object_name", name);
		}
		case 1406:
			return I18N.prop("msg_bobas_db_data_truncation");
		case 1366:
			return I18N.prop("msg_bobas_db_data_conversion_failed");
		case 1064:
			return I18N.prop("msg_bobas_db_syntax_error");
		default:
			return super.translateException(exception);
		}
	}

	/**
	 * 从 MySQL 异常消息中提取重复键值。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * Duplicate entry 'Z00002' for key 'UK_xxx'
	 * </pre>
	 * 
	 * 提取规则：取消息中第一对单引号包含的内容。
	 *
	 * @param message 异常消息
	 * @return 重复键值；解析失败返回 null
	 */
	private String extractDuplicateValue(String message) {
		return this.extractQuotedName(message);
	}

	/**
	 * 从异常消息中提取首个被单引号或反引号包裹的名称。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * Unknown column 'Remarks' in 'field list'
	 * Table 'db.tbl' doesn't exist
	 * </pre>
	 * 
	 * 提取规则：取消息中第一对单引号或反引号包含的内容。
	 *
	 * @param message 异常消息
	 * @return 名称；解析失败返回 null
	 */
	private String extractQuotedName(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		for (char quote : new char[] { '\'', '`' }) {
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
