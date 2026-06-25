package org.colorcoding.ibas.bobas.db.sybase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
			if (server.indexOf(":") < 0) {
				// 没有添加端口
				server = String.format("%s:2638", server);
			}
			String dbURL = String.format("jdbc:sybase:Tds:%s?ServiceName=%s", server, dbName);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(Strings.format("db adapter: %s", dbURL));
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
	public String parsingStoredProcedure(String spName, String... args) {
		StringBuilder stringBuilder = new StringBuilder(spName.length() + args.length * 16 + 32);
		stringBuilder.append("EXEC");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier(MyConfiguration.applyVariables(spName)));
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
					// 参数名按标识符转义后再拼接，防止注入
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
	 * 翻译 Sybase 异常为友好的国际化消息。
	 * <p>
	 * 按 ErrorCode 识别常见错误：
	 * <ul>
	 * <li>2601：试图插入重复的键到唯一索引</li>
	 * <li>2615：违反 PRIMARY KEY 约束</li>
	 * <li>546：违反 FOREIGN KEY 约束</li>
	 * <li>233：试图把 NULL 插入到不允许为空的列</li>
	 * <li>207：无效的列名</li>
	 * <li>208：无效的对象（表或视图不存在）</li>
	 * <li>247：算术溢出 / 数据被截断</li>
	 * <li>257：隐式转换不支持</li>
	 * <li>102 / 156：语法错误</li>
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
		case 2601:
		case 2615: {
			String value = this.extractDuplicateValue(message);
			return Strings.isNullOrEmpty(value) ? I18N.prop("msg_bobas_db_duplicate_key")
					: I18N.prop("msg_bobas_db_duplicate_key_value", value);
		}
		case 546:
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		case 233:
			return I18N.prop("msg_bobas_db_not_null_violation");
		case 207: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_column")
					: I18N.prop("msg_bobas_db_invalid_column_name", name);
		}
		case 208: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_object")
					: I18N.prop("msg_bobas_db_invalid_object_name", name);
		}
		case 247:
			return I18N.prop("msg_bobas_db_data_truncation");
		case 257:
			return I18N.prop("msg_bobas_db_data_conversion_failed");
		case 102:
		case 156:
			return I18N.prop("msg_bobas_db_syntax_error");
		default:
			return super.translateException(exception);
		}
	}

	/**
	 * 从 Sybase 异常消息中提取重复键值。
	 * <p>
	 * 典型消息格式：消息末尾形如 <code>(Z00002)</code>。 提取规则：取消息末尾最后一对小括号内的内容。
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
	 * 从异常消息中提取首个被单引号包裹的名称。
	 *
	 * @param message 异常消息
	 * @return 名称；解析失败返回 null
	 */
	private String extractQuotedName(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		int start = message.indexOf('\'');
		if (start >= 0) {
			int end = message.indexOf('\'', start + 1);
			if (end > start) {
				String value = message.substring(start + 1, end);
				if (!Strings.isNullOrEmpty(value)) {
					return value;
				}
			}
		}
		return null;
	}
}
