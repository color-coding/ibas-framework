package org.colorcoding.ibas.bobas.db.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DataType;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			String application = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_APPLICATION_NAME);
			if (Strings.isNullOrEmpty(application)) {
				application = Strings.format("ibas_%s", this.hashCode());
			}
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// 清洗 server 和 dbName 中的 JDBC URL 注入字符
			if (server != null) {
				server = server.replace(";", Strings.VALUE_EMPTY).replace("=", Strings.VALUE_EMPTY);
			}
			if (dbName != null) {
				dbName = dbName.replace(";", Strings.VALUE_EMPTY).replace("=", Strings.VALUE_EMPTY);
			}
			String dbURL = String.format(
					"jdbc:sqlserver://%s;DatabaseName=%s;encrypt=false;ApplicationName=%s;useBulkCopyForBatchInsert=true",
					server, dbName, application);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
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
		if (type == DataType.ALPHANUMERIC) {
			StringBuilder stringBuilder = new StringBuilder(64);
			stringBuilder.append("CONVERT");
			stringBuilder.append("(");
			stringBuilder.append("NVARCHAR");
			stringBuilder.append(",");
			stringBuilder.append(" ");
			// 转义标识符引号字符，防止注入
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(",");
			stringBuilder.append(" ");
			stringBuilder.append("120");
			stringBuilder.append(")");
			return stringBuilder.toString();
		}
		return super.castAs(type, alias);
	}

	/**
	 * 翻译 SQL Server 异常为友好的国际化消息。
	 * <p>
	 * 按 ErrorCode 识别常见错误：
	 * <ul>
	 * <li>2601：不能在唯一索引中插入重复键</li>
	 * <li>2627：违反 UNIQUE KEY 约束</li>
	 * <li>547：INSERT/UPDATE 与 FOREIGN KEY 约束冲突</li>
	 * <li>515：不能将值 NULL 插入列</li>
	 * <li>207：Invalid column name（无效的列名）</li>
	 * <li>208：Invalid object name（无效的对象名）</li>
	 * <li>8152 / 2628：String or binary data would be truncated（数据被截断）</li>
	 * <li>245 / 8114：Conversion failed（数据类型转换失败）</li>
	 * <li>102 / 156：Incorrect syntax / 语法错误</li>
	 * </ul>
	 * 兼容 {@link java.sql.BatchUpdateException}：会沿
	 * {@link SQLException#getNextException()} 链向下查找真正的错误码；同时对消息特征做兜底识别（部分驱动批处理时不透传
	 * ErrorCode）。 未匹配时回退到基类实现（返回异常原始消息）。
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
		case 2627: {
			String value = this.extractDuplicateValue(message);
			return Strings.isNullOrEmpty(value) ? I18N.prop("msg_bobas_db_duplicate_key")
					: I18N.prop("msg_bobas_db_duplicate_key_value", value);
		}
		case 547:
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		case 515:
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
		case 2628:
		case 8152:
			return I18N.prop("msg_bobas_db_data_truncation");
		case 245:
		case 8114:
			return I18N.prop("msg_bobas_db_data_conversion_failed");
		case 102:
		case 156:
			return I18N.prop("msg_bobas_db_syntax_error");
		default:
			// 兜底：基于消息特征识别（适用于错误码丢失的批处理场景）
			String featured = this.translateByMessage(message);
			if (!Strings.isNullOrEmpty(featured)) {
				return featured;
			}
			return super.translateException(exception);
		}
	}

	/**
	 * 基于异常消息特征识别错误类型（错误码不可用时的兜底逻辑）。
	 *
	 * @param message 异常消息
	 * @return 国际化消息；未识别返回 null
	 */
	private String translateByMessage(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		String lower = message.toLowerCase();
		if (lower.contains("duplicate key") || lower.contains("unique index") || lower.contains("unique constraint")
				|| lower.contains("unique key")) {
			String value = this.extractDuplicateValue(message);
			return Strings.isNullOrEmpty(value) ? I18N.prop("msg_bobas_db_duplicate_key")
					: I18N.prop("msg_bobas_db_duplicate_key_value", value);
		}
		if (lower.contains("foreign key")) {
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		}
		if (lower.contains("cannot insert the value null") || lower.contains("does not allow nulls")) {
			return I18N.prop("msg_bobas_db_not_null_violation");
		}
		if (lower.contains("invalid column name")) {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_column")
					: I18N.prop("msg_bobas_db_invalid_column_name", name);
		}
		if (lower.contains("invalid object name")) {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_object")
					: I18N.prop("msg_bobas_db_invalid_object_name", name);
		}
		if (lower.contains("string or binary data would be truncated")) {
			return I18N.prop("msg_bobas_db_data_truncation");
		}
		if (lower.contains("conversion failed") || lower.contains("error converting")
				|| lower.contains("arithmetic overflow")) {
			return I18N.prop("msg_bobas_db_data_conversion_failed");
		}
		if (lower.contains("incorrect syntax")) {
			return I18N.prop("msg_bobas_db_syntax_error");
		}
		return null;
	}

	/**
	 * 从 SQL Server 异常消息中提取重复键值。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * Cannot insert duplicate key row in object 'dbo.CC_MM_OITM' with unique index 'UK_CC_MM_OITM'. The duplicate key value is (Z00002).
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
	 * 从异常消息中提取首个被单引号包裹的名称。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * Invalid column name 'Remarks'.
	 * Invalid object name 'dbo.NonExist'.
	 * </pre>
	 * 
	 * 提取规则：取消息中第一对单引号包含的内容。
	 *
	 * @param message 异常消息
	 * @return 名称；解析失败返回 null
	 */
	private String extractQuotedName(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		int start = message.indexOf("'");
		if (start >= 0) {
			int end = message.indexOf("'", start + 1);
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
