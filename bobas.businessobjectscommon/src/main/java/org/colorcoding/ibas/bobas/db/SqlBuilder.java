package org.colorcoding.ibas.bobas.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL语句构建器，通过PreparedStatement接口收集参数，生成填充参数后的完整SQL
 */
public class SqlBuilder implements PreparedStatement {

	/**
	 * 参数项，同时容纳值与SQL类型
	 */
	static class Parameter {
		Object value;
		int sqlType;

		Parameter(Object value, int sqlType) {
			this.value = value;
			this.sqlType = sqlType;
		}
	}

	public SqlBuilder(String sql) {
		this.originalSql = sql;
	}

	private final String originalSql;
	private final List<Parameter> parameters = new ArrayList<>();

	private void addParam(int index, Object value, int sqlType) {
		int idx = index - 1;
		while (this.parameters.size() <= idx) {
			this.parameters.add(new Parameter(null, Types.NULL));
		}
		this.parameters.get(idx).value = value;
		this.parameters.get(idx).sqlType = sqlType;
	}

	/**
	 * 判断SQL类型是否为字符串类型（需要加引号）
	 */
	private static boolean isStringType(int sqlType) {
		return sqlType == Types.CHAR || sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR
				|| sqlType == Types.NCHAR || sqlType == Types.NVARCHAR || sqlType == Types.LONGNVARCHAR
				|| sqlType == Types.CLOB || sqlType == Types.NCLOB;
	}

	/**
	 * 判断SQL类型是否为日期时间类型（需要加引号）
	 */
	private static boolean isDateTimeType(int sqlType) {
		return sqlType == Types.DATE || sqlType == Types.TIME || sqlType == Types.TIMESTAMP
				|| sqlType == Types.TIME_WITH_TIMEZONE || sqlType == Types.TIMESTAMP_WITH_TIMEZONE;
	}

	/**
	 * 判断SQL类型是否为数值类型（不需要加引号）
	 */
	private static boolean isNumericType(int sqlType) {
		return sqlType == Types.TINYINT || sqlType == Types.SMALLINT || sqlType == Types.INTEGER
				|| sqlType == Types.BIGINT || sqlType == Types.FLOAT || sqlType == Types.DOUBLE
				|| sqlType == Types.REAL || sqlType == Types.NUMERIC || sqlType == Types.DECIMAL;
	}

	/**
	 * 获取填充参数后的完整SQL语句
	 */
	public String getExecutableSql() {
		StringBuilder sb = new StringBuilder(this.originalSql.length() + this.parameters.size() * 16);
		int paramIndex = 0;
		for (int i = 0; i < this.originalSql.length(); i++) {
			char c = this.originalSql.charAt(i);
			if (c == '?') {
				if (paramIndex < this.parameters.size()) {
					Parameter param = this.parameters.get(paramIndex);
					sb.append(this.formatValue(param.value, param.sqlType));
				}
				paramIndex++;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 将参数值格式化为SQL字面量
	 */
	private String formatValue(Object value, int sqlType) {
		if (value == null) {
			return "NULL";
		}
		// 优先根据SQL类型决定格式
		if (isStringType(sqlType)) {
			return "'" + escapeString(value.toString()) + "'";
		}
		if (isDateTimeType(sqlType)) {
			return "'" + escapeString(value.toString()) + "'";
		}
		if (isNumericType(sqlType)) {
			return value.toString();
		}
		// SQL类型未知时，根据Java类型回退判断
		if (value instanceof Number) {
			return value.toString();
		}
		if (value instanceof Boolean) {
			return ((Boolean) value) ? "1" : "0";
		}
		// 默认作为字符串处理
		return "'" + escapeString(value.toString()) + "'";
	}

	/**
	 * 转义SQL字符串中的特殊字符
	 */
	private String escapeString(String value) {
		StringBuilder sb = new StringBuilder(value.length() + 8);
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
			case '\'':
				sb.append("''");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\0':
				// 空字符替换为空格，防止截断
				sb.append(' ');
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	// ---- 以下为PreparedStatement接口中setXxx方法的参数记录实现 ----

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.addParam(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.addParam(parameterIndex, x, Types.OTHER);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		this.addParam(parameterIndex, x, Types.VARCHAR);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.addParam(parameterIndex, x, Types.INTEGER);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.addParam(parameterIndex, x, Types.BIGINT);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.addParam(parameterIndex, x, Types.DOUBLE);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.addParam(parameterIndex, x, Types.FLOAT);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.addParam(parameterIndex, x, Types.SMALLINT);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.addParam(parameterIndex, x, Types.TINYINT);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.addParam(parameterIndex, x, Types.BOOLEAN);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.addParam(parameterIndex, x, Types.DATE);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.addParam(parameterIndex, x, Types.TIME);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.addParam(parameterIndex, x, Types.TIMESTAMP);
	}

	@Override
	public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) throws SQLException {
		this.addParam(parameterIndex, x, Types.DECIMAL);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.addParam(parameterIndex, null, sqlType);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.addParam(parameterIndex, x, Types.VARBINARY);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		this.addParam(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setDate(int parameterIndex, Date x, java.util.Calendar cal) throws SQLException {
		this.addParam(parameterIndex, x, Types.DATE);
	}

	@Override
	public void setTime(int parameterIndex, Time x, java.util.Calendar cal) throws SQLException {
		this.addParam(parameterIndex, x, Types.TIME);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, java.util.Calendar cal) throws SQLException {
		this.addParam(parameterIndex, x, Types.TIMESTAMP);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.addParam(parameterIndex, null, sqlType);
	}

	@Override
	public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARBINARY);
	}

	@Override
	public void setCharacterStream(int parameterIndex, java.io.Reader x, int length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBinaryStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARBINARY);
	}

	@Override
	public void setCharacterStream(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setAsciiStream(int parameterIndex, java.io.InputStream x) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBinaryStream(int parameterIndex, java.io.InputStream x) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARBINARY);
	}

	@Override
	public void setCharacterStream(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBlob(int parameterIndex, java.sql.Blob x) throws SQLException {
		this.addParam(parameterIndex, x, Types.BLOB);
	}

	@Override
	public void setClob(int parameterIndex, java.sql.Clob x) throws SQLException {
		this.addParam(parameterIndex, x, Types.CLOB);
	}

	@Override
	public void setArray(int parameterIndex, java.sql.Array x) throws SQLException {
		this.addParam(parameterIndex, x, Types.ARRAY);
	}

	@Override
	public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
		this.addParam(parameterIndex, x, Types.VARCHAR);
	}

	@Override
	public void setNString(int parameterIndex, String x) throws SQLException {
		this.addParam(parameterIndex, x, Types.NVARCHAR);
	}

	@Override
	public void setNClob(int parameterIndex, java.sql.NClob x) throws SQLException {
		this.addParam(parameterIndex, x, Types.NCLOB);
	}

	@Override
	public void setSQLXML(int parameterIndex, java.sql.SQLXML x) throws SQLException {
		this.addParam(parameterIndex, x, Types.SQLXML);
	}

	@Override
	public void setRef(int parameterIndex, java.sql.Ref x) throws SQLException {
		this.addParam(parameterIndex, x, Types.REF);
	}

	@Override
	public void setRowId(int parameterIndex, java.sql.RowId x) throws SQLException {
		this.addParam(parameterIndex, x, Types.ROWID);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGNVARCHAR);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGNVARCHAR);
	}

	@Override
	public void setClob(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParam(parameterIndex, x, Types.CLOB);
	}

	@Override
	public void setBlob(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		this.addParam(parameterIndex, x, Types.BLOB);
	}

	@Override
	public void setNClob(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParam(parameterIndex, x, Types.NCLOB);
	}

	@Override
	public void setClob(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParam(parameterIndex, x, Types.CLOB);
	}

	@Override
	public void setBlob(int parameterIndex, java.io.InputStream x) throws SQLException {
		this.addParam(parameterIndex, x, Types.BLOB);
	}

	@Override
	public void setNClob(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParam(parameterIndex, x, Types.NCLOB);
	}

	@Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		this.addParam(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void clearParameters() throws SQLException {
		this.parameters.clear();
	}

	// ---- 以下为Statement/PreparedStatement中不需要的方法 ----

	@Override
	public void addBatch() throws SQLException {
	}

	@Override
	public int[] executeBatch() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.ResultSet executeQuery() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.ResultSetMetaData getMetaData() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.ParameterMetaData getParameterMetaData() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancel() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearBatch() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.ResultSet executeQuery(String sql) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.Connection getConnection() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.ResultSet getGeneratedKeys() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxRows() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.ResultSet getResultSet() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getResultSetType() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.sql.SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		throw new UnsupportedOperationException();
	}
}
