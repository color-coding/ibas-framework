package org.colorcoding.ibas.bobas.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

public class DbCommand implements IDbCommand {
	private DbCommand() {

	}

	public DbCommand(Statement statement) throws DbException {
		this();
		if (statement == null) {
			throw new DbException();
		}
		this.statement = statement;
	}

	@Override
	public IDbDataReader executeReader(String sql) throws DbException {
		try {
			RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_SQL_SCRIPTS, sql);
			ResultSet resultSet = this.statement.executeQuery(sql);
			return new DbDataReader(resultSet);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public IDbDataReader executeReader(ISqlQuery sql) throws DbException {
		return this.executeReader(sql.getQueryString());
	}

	private Statement statement = null;

	@Override
	public void close() throws DbException {
		try {
			this.statement.close();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public int executeUpdate(ISqlQuery sql) throws DbException {
		return this.executeUpdate(sql.getQueryString());
	}

	@Override
	public int executeUpdate(String sql) throws DbException {
		try {
			RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_SQL_SCRIPTS, sql);
			return this.statement.executeUpdate(sql);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public void addBatch(String sql) throws DbException {
		try {
			RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_SQL_SCRIPTS, sql);
			this.statement.addBatch(sql);
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public void addBatch(ISqlQuery sql) throws DbException {
		this.addBatch(sql.getQueryString());
	}

	@Override
	public void clearBatch() throws DbException {
		try {
			this.statement.clearBatch();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public int[] executeBatch() throws DbException {
		try {
			return this.statement.executeBatch();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

}
