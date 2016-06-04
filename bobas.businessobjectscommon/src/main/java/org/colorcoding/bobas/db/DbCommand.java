package org.colorcoding.bobas.db;

import java.sql.ResultSet;
import java.sql.Statement;

import org.colorcoding.bobas.common.ISqlQuery;
import org.colorcoding.bobas.messages.RuntimeLog;

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
			RuntimeLog.log(RuntimeLog.MSG_SQL_SCRIPTS, sql);
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
			RuntimeLog.log(RuntimeLog.MSG_SQL_SCRIPTS, sql);
			return this.statement.executeUpdate(sql);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

}
