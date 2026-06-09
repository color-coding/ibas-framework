package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.Strings;

/**
 * SQL语句构建器
 * 
 * 通过PreparedStatement接口收集参数，索引 1 开始
 */
public class SqlPreparedStatement extends SqlStatement implements PreparedStatement {

	private static final long serialVersionUID = -8302156932671231289L;

	/**
	 * 参数项
	 */
	protected class Parameter {

		/** 目标数据库字段类型 */
		public DbFieldType targetType;

		/** 参数值 */
		public Object value;

		/** 参数名称（存储过程使用） */
		public String name;

		@Override
		public String toString() {
			return Strings.format("{parameter: %s}", value);
		}
	}

	/**
	 * 构造
	 */
	public SqlPreparedStatement() {
	}

	/**
	 * 构造
	 *
	 * @param sql 原始SQL语句
	 */
	public SqlPreparedStatement(String sql) {
		super(sql);
	}

	private Map<Integer, Parameter> parameters;

	protected Map<Integer, Parameter> getParameters() {
		if (this.parameters == null) {
			this.parameters = new HashMap<Integer, Parameter>();
		}
		return this.parameters;
	}

	@Override
	public void clearParameters() {
		if (this.parameters != null) {
			this.parameters.clear();
		}
	}

	protected Parameter addParameter(Object value, DbFieldType targetType) {
		return this.addParameter(this.getParameters().size() + 1, value, targetType);
	}

	protected Parameter addParameter(int parameterIndex, Object value, int sqlType) {
		return this.addParameter(parameterIndex, value, this.getAdapter().dbFieldTypeOf(sqlType));
	}

	protected Parameter addParameter(int parameterIndex, Object value, DbFieldType targetType) {
		Parameter parameter = new Parameter();
		parameter.targetType = targetType;
		parameter.value = value;
		this.getParameters().put(parameterIndex, parameter);
		return parameter;
	}

	private DbAdapter adapter;

	/**
	 * 设置数据库适配器
	 *
	 * @param adapter 数据库适配器实例
	 */
	public void setAdapter(DbAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * 获取数据库适配器（未设置时使用默认实例）
	 */
	private DbAdapter getAdapter() {
		if (this.adapter == null) {
			this.adapter = new DbAdapter() {
				@Override
				public Connection createConnection(String server, String dbName, String userName, String userPwd) {
					return null;
				}
			};
		}
		return this.adapter;
	}

	public void setObject(int parameterIndex, Object value, DbFieldType targetType) throws SQLException {
		this.addParameter(parameterIndex, value, targetType);
	}

	/**
	 * 根据条件集合批量设置参数
	 *
	 * @param conditions 查询条件集合
	 * @param beginIndex 始于参数索引
	 * @throws SQLException
	 */
	public void setObject(Iterable<ICondition> conditions, int beginIndex) throws SQLException {
		this.getAdapter().bindParameters(this, conditions, beginIndex);
	}

	/**
	 * 根据条件集合批量设置参数
	 *
	 * @param conditions 查询条件集合
	 * @throws SQLException
	 */
	public void setObject(Iterable<ICondition> conditions) throws SQLException {
		this.setObject(conditions, 1);
	}

	/**
	 * 可执行SQL语句
	 */
	@Override
	public String executableSql() {
		DbAdapter dbAdapter = this.getAdapter();
		if (dbAdapter != null) {
			return dbAdapter.parsing(this);
		}
		throw new UnsupportedOperationException("db adapter is required.");
	}

	// ---- 以下为PreparedStatement接口中setXxx方法的参数记录实现 ----

	@Override
	public void setObject(int parameterIndex, Object x) {
		this.addParameter(parameterIndex, x, Types.OTHER);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.addParameter(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.VARCHAR);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.INTEGER);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.BIGINT);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.DOUBLE);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.FLOAT);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.SMALLINT);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.TINYINT);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.BOOLEAN);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.DATE);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.TIME);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.TIMESTAMP);
	}

	@Override
	public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.DECIMAL);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.addParameter(parameterIndex, null, sqlType);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.VARBINARY);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		this.addParameter(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setDate(int parameterIndex, Date x, java.util.Calendar cal) throws SQLException {
		this.addParameter(parameterIndex, x, Types.DATE);
	}

	@Override
	public void setTime(int parameterIndex, Time x, java.util.Calendar cal) throws SQLException {
		this.addParameter(parameterIndex, x, Types.TIME);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, java.util.Calendar cal) throws SQLException {
		this.addParameter(parameterIndex, x, Types.TIMESTAMP);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.addParameter(parameterIndex, null, sqlType);
	}

	@Override
	public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARBINARY);
	}

	@Override
	public void setCharacterStream(int parameterIndex, java.io.Reader x, int length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBinaryStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARBINARY);
	}

	@Override
	public void setCharacterStream(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setAsciiStream(int parameterIndex, java.io.InputStream x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBinaryStream(int parameterIndex, java.io.InputStream x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARBINARY);
	}

	@Override
	public void setCharacterStream(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARCHAR);
	}

	@Override
	public void setBlob(int parameterIndex, java.sql.Blob x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.BLOB);
	}

	@Override
	public void setClob(int parameterIndex, java.sql.Clob x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.CLOB);
	}

	@Override
	public void setArray(int parameterIndex, java.sql.Array x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.ARRAY);
	}

	@Override
	public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.VARCHAR);
	}

	@Override
	public void setNString(int parameterIndex, String x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.NVARCHAR);
	}

	@Override
	public void setNClob(int parameterIndex, java.sql.NClob x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.NCLOB);
	}

	@Override
	public void setSQLXML(int parameterIndex, java.sql.SQLXML x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.SQLXML);
	}

	@Override
	public void setRef(int parameterIndex, java.sql.Ref x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.REF);
	}

	@Override
	public void setRowId(int parameterIndex, java.sql.RowId x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.ROWID);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGNVARCHAR);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGNVARCHAR);
	}

	@Override
	public void setClob(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.CLOB);
	}

	@Override
	public void setBlob(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.BLOB);
	}

	@Override
	public void setNClob(int parameterIndex, java.io.Reader x, long length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.NCLOB);
	}

	@Override
	public void setClob(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.CLOB);
	}

	@Override
	public void setBlob(int parameterIndex, java.io.InputStream x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.BLOB);
	}

	@Override
	public void setNClob(int parameterIndex, java.io.Reader x) throws SQLException {
		this.addParameter(parameterIndex, x, Types.NCLOB);
	}

	@Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		this.addParameter(parameterIndex, x, Types.LONGVARCHAR);
	}

	// ---- 以下为Statement/PreparedStatement中不需要的方法 ----

	@Override
	public void addBatch() throws SQLException {
		throw new UnsupportedOperationException();
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
