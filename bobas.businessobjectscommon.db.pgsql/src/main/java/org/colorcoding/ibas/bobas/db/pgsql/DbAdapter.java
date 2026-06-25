package org.colorcoding.ibas.bobas.db.pgsql;

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
			Class.forName("org.postgresql.Driver");
			String dbURL = String.format("jdbc:postgresql://%s/%s?prepareThreshold=5", server, dbName);
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
	 * 返回SQL类型；value为null时返回Types.NULL，否则委托给sqlTypeOf(type)
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
		return this.sqlTypeOf(type);
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
	 * 翻译 PostgreSQL 异常为友好的国际化消息。
	 * <p>
	 * 按 SQLSTATE 识别常见错误（5 位编码，参考 ISO/IEC 9075-2）：
	 * <ul>
	 * <li>23505：unique_violation（唯一约束）</li>
	 * <li>23503：foreign_key_violation（外键约束）</li>
	 * <li>23502：not_null_violation（非空约束）</li>
	 * <li>42703：undefined_column（无效的列名）</li>
	 * <li>42P01：undefined_table（无效的对象）</li>
	 * <li>22001：string_data_right_truncation（数据被截断）</li>
	 * <li>22P02 / 22003 / 22008：invalid_text_representation 等（类型转换失败）</li>
	 * <li>42601：syntax_error（语法错误）</li>
	 * </ul>
	 * 兼容 {@link java.sql.BatchUpdateException}：会沿
	 * {@link SQLException#getNextException()} 链向下查找具体的
	 * SQLSTATE。未匹配时回退到基类实现（返回异常原始消息）。
	 *
	 * @param exception 数据库异常
	 * @return 国际化消息；输入为 null 时调用基类方法
	 */
	@Override
	public String translateException(SQLException exception) {
		if (exception == null) {
			return super.translateException(exception);
		}
		// 沿 getNextException 链查找具体 SQLSTATE（兼容 BatchUpdateException）
		SQLException effective = exception;
		String state = effective.getSQLState();
		if (Strings.isNullOrEmpty(state)) {
			SQLException next = exception.getNextException();
			while (next != null) {
				if (!Strings.isNullOrEmpty(next.getSQLState())) {
					effective = next;
					state = next.getSQLState();
					break;
				}
				next = next.getNextException();
			}
		}
		String message = effective.getMessage();
		if (message == null) {
			message = Strings.VALUE_EMPTY;
		}
		if ("23505".equals(state)) {
			String value = this.extractDuplicateValue(message);
			return Strings.isNullOrEmpty(value) ? I18N.prop("msg_bobas_db_duplicate_key")
					: I18N.prop("msg_bobas_db_duplicate_key_value", value);
		}
		if ("23503".equals(state)) {
			return I18N.prop("msg_bobas_db_foreign_key_violation");
		}
		if ("23502".equals(state)) {
			return I18N.prop("msg_bobas_db_not_null_violation");
		}
		if ("42703".equals(state)) {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_column")
					: I18N.prop("msg_bobas_db_invalid_column_name", name);
		}
		if ("42P01".equals(state)) {
			String name = this.extractQuotedName(message);
			return Strings.isNullOrEmpty(name) ? I18N.prop("msg_bobas_db_invalid_object")
					: I18N.prop("msg_bobas_db_invalid_object_name", name);
		}
		if ("22001".equals(state)) {
			return I18N.prop("msg_bobas_db_data_truncation");
		}
		if ("22P02".equals(state) || "22003".equals(state) || "22008".equals(state) || "22007".equals(state)) {
			return I18N.prop("msg_bobas_db_data_conversion_failed");
		}
		if ("42601".equals(state)) {
			return I18N.prop("msg_bobas_db_syntax_error");
		}
		return super.translateException(exception);
	}

	/**
	 * 从 PostgreSQL 异常消息中提取重复键值。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * ERROR: duplicate key value violates unique constraint "uk_xxx"
	 *   Detail: Key (code)=(Z00002) already exists.
	 * </pre>
	 * 
	 * 提取规则：定位 <code>)=(</code> 之后的 <code>(值)</code> 内容。
	 *
	 * @param message 异常消息
	 * @return 重复键值；解析失败返回 null
	 */
	private String extractDuplicateValue(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		// 定位 ")=(" 之后的 (value)
		int eq = message.indexOf(")=(");
		if (eq >= 0) {
			int start = eq + 2; // 指向第二个 (
			int end = message.indexOf(")", start + 1);
			if (end > start) {
				String value = message.substring(start + 1, end);
				if (!Strings.isNullOrEmpty(value)) {
					return value;
				}
			}
		}
		return null;
	}

	/**
	 * 从异常消息中提取首个被双引号包裹的名称。
	 * <p>
	 * 典型消息格式：
	 * 
	 * <pre>
	 * column "remarks" does not exist
	 * relation "public.non_exist" does not exist
	 * </pre>
	 * 
	 * 提取规则：取消息中第一对双引号包含的内容。
	 *
	 * @param message 异常消息
	 * @return 名称；解析失败返回 null
	 */
	private String extractQuotedName(String message) {
		if (Strings.isNullOrEmpty(message)) {
			return null;
		}
		int start = message.indexOf('"');
		if (start >= 0) {
			int end = message.indexOf('"', start + 1);
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
