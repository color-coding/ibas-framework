package org.colorcoding.ibas.bobas.db.dm8;

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
			Class.forName("dm.jdbc.driver.DmDriver");
			String dbURL = String.format("jdbc:dm://%s?schema=\"\"%s\"\"", server, dbName);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			// 接数据库失败
			throw new RuntimeException(e.getMessage(), e);
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
			stringBuilder.append("VARCHAR");
			stringBuilder.append(")");
		} else if (type == DataType.DATE) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier(alias));
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("DATE");
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
		StringBuilder stringBuilder = new StringBuilder(spName.length() + args.length * 16 + 32);
		stringBuilder.append("CALL");
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
	 * 翻译达梦 DM 异常为友好的国际化消息。
	 * <p>
	 * 按 ErrorCode 识别常见错误（达梦兼容 Oracle 风格）：
	 * <ul>
	 * <li>-6602：违反唯一性约束</li>
	 * <li>-6608：违反引用约束（外键）</li>
	 * <li>-6601：违反非空约束</li>
	 * <li>-2106 / -4083：无效的列名</li>
	 * <li>-2007：无效的表或视图</li>
	 * <li>-6004：数据被截断</li>
	 * <li>-6005：数据类型转换失败</li>
	 * <li>-2002：SQL 语法错误</li>
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
		case -6602: {
			String value = this.extractDuplicateValue(message);
			return Strings.isNullOrEmpty(value) ? I18N.prop("msg_bobas_db_duplicate_key")
					: I18N.prop("msg_bobas_db_duplicate_key_value", value);
		}
		case -6608:
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		case -6601:
			return I18N.prop("msg_bobas_db_not_null_violation");
		case -2106:
		case -4083: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_column")
					: I18N.prop("msg_bobas_db_invalid_column_name", name);
		}
		case -2007: {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_object")
					: I18N.prop("msg_bobas_db_invalid_object_name", name);
		}
		case -6004:
			return I18N.prop("msg_bobas_db_data_truncation");
		case -6005:
			return I18N.prop("msg_bobas_db_data_conversion_failed");
		case -2002:
			return I18N.prop("msg_bobas_db_syntax_error");
		default:
			return super.translateException(exception);
		}
	}

	/**
	 * 从达梦 DM 异常消息中提取重复键值。
	 * <p>
	 * 典型消息格式（Oracle 风格）：消息末尾形如 <code>(值: Z00002)</code> 或 <code>(Z00002)</code>。
	 * 提取规则：取消息末尾最后一对小括号内的内容；若含冒号，仅保留冒号之后的值。
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
			// 去除可能的前缀（如 "值:"、"value:"）
			int colon = value.indexOf(':');
			if (colon >= 0 && colon + 1 < value.length()) {
				value = value.substring(colon + 1).trim();
			}
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
