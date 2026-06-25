package org.colorcoding.ibas.bobas.db.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Files;
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
			Class.forName("org.sqlite.JDBC");
			File file = Files.valueOf(dbName);
			if (file.getParentFile() == null || !file.getParentFile().isDirectory()) {
				file = Files.valueOf(MyConfiguration.getDataFolder(), file.getName());
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			String dbURL = String.format("jdbc:sqlite:%s?date_string_format=%s", file.getAbsolutePath(),
					DateTimes.FORMAT_DATE);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			// 接数据库失败
			throw new RuntimeException(e);
		}
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
			stringBuilder.append("CHAR");
			stringBuilder.append(")");
		} else if (type == DataType.DATE) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("DATETIME");
			stringBuilder.append(")");
		} else if (type == DataType.NUMERIC) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("SIGNED");
			stringBuilder.append(")");
			return stringBuilder.toString();
		} else if (type == DataType.DECIMAL) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("DECIMAL(19, 6)");
			stringBuilder.append(")");
		} else {
			stringBuilder.append(this.identifier(alias));
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
		return stringBuilder.toString();
	}

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

	public String parsingStoredProcedure(String spName, String... args) {
		StringBuilder stringBuilder = new StringBuilder(spName.length() + args.length * 16 + 32);
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("*");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier(MyConfiguration.applyVariables(spName)));
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		if (args.length > 0) {
			stringBuilder.append(" ");
			int count = stringBuilder.length();
			for (int i = 0; i < args.length; i++) {
				if (stringBuilder.length() > count) {
					stringBuilder.append(" ");
					stringBuilder.append("OR");
					stringBuilder.append(" ");
				}
				stringBuilder.append("?");
				stringBuilder.append(" ");
				stringBuilder.append("IS NOT NULL");
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 翻译 SQLite 异常为友好的国际化消息。
	 * <p>
	 * 按扩展 result code 识别常见错误：
	 * <ul>
	 * <li>1555 (SQLITE_CONSTRAINT_PRIMARYKEY)：主键冲突</li>
	 * <li>2067 (SQLITE_CONSTRAINT_UNIQUE)：唯一约束冲突</li>
	 * <li>787 (SQLITE_CONSTRAINT_FOREIGNKEY)：外键约束冲突</li>
	 * <li>1299 (SQLITE_CONSTRAINT_NOTNULL)：非空约束冲突</li>
	 * <li>1 (SQLITE_ERROR)：通用错误，按消息识别 列不存在 / 对象不存在 / 语法错误</li>
	 * </ul>
	 * 当 ErrorCode 仅为主类码 19 (SQLITE_CONSTRAINT) 时，按消息内容关键字识别子类型。
	 * <p>
	 * 兼容 {@link java.sql.BatchUpdateException}：会沿
	 * {@link SQLException#getNextException()} 链向下查找真正的错误码。SQLite
	 * 异常消息不携带重复键值，因此唯一/主键冲突仅返回通用提示。 未匹配时回退到基类实现（返回异常原始消息）。
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
		case 1555:
		case 2067:
			return I18N.prop("msg_bobas_db_duplicate_key");
		case 787:
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		case 1299:
			return I18N.prop("msg_bobas_db_not_null_violation");
		default:
			// SQLite 主错误码 19 表示通用约束冲突，进一步靠消息内容识别
			if (code == 19) {
				String lower = message.toLowerCase();
				if (lower.contains("unique")) {
					return I18N.prop("msg_bobas_db_duplicate_key");
				}
				if (lower.contains("foreign key")) {
					return I18N.prop("msg_bobas_db_foreign_key_violation");
				}
				if (lower.contains("not null")) {
					return I18N.prop("msg_bobas_db_not_null_violation");
				}
				return I18N.prop("msg_bobas_db_integrity_constraint_violation");
			}
			// SQLite 主错误码 1 (SQLITE_ERROR) 为通用错误，按消息识别
			if (code == 1) {
				String featured = this.translateByMessage(message);
				if (!Strings.isNullOrEmpty(featured)) {
					return featured;
				}
			}
			return super.translateException(exception);
		}
	}

	/**
	 * 基于异常消息特征识别错误类型（SQLITE_ERROR 通用错误时使用）。
	 *
	 * @param message 异常消息
	 * @return 国际化消息；未识别返回 null
	 */
	private String translateByMessage(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		String lower = message.toLowerCase();
		if (lower.contains("no such column")) {
			String name = this.extractColumnName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_column")
					: I18N.prop("msg_bobas_db_invalid_column_name", name);
		}
		if (lower.contains("no such table") || lower.contains("no such view")) {
			String name = this.extractColumnName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_object")
					: I18N.prop("msg_bobas_db_invalid_object_name", name);
		}
		if (lower.contains("syntax error") || lower.contains("near \"")) {
			return I18N.prop("msg_bobas_db_syntax_error");
		}
		return null;
	}

	/**
	 * 从 SQLite 异常消息中提取列/表名。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * no such column: Remarks
	 * no such table: T_X
	 * </pre>
	 * 
	 * 提取规则：取最后一个冒号之后的非空内容（去除首尾空白）。
	 *
	 * @param message 异常消息
	 * @return 名称；解析失败返回 null
	 */
	private String extractColumnName(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		int idx = message.lastIndexOf(':');
		if (idx >= 0 && idx + 1 < message.length()) {
			String value = message.substring(idx + 1).trim();
			// 去除末尾的句号或其它标点
			if (value.endsWith(".") || value.endsWith(",")) {
				value = value.substring(0, value.length() - 1).trim();
			}
			if (!Strings.isNullOrEmpty(value)) {
				return value;
			}
		}
		return null;
	}

}
