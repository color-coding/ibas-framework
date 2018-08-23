package org.colorcoding.ibas.bobas.db;

import java.sql.SQLException;

import org.colorcoding.ibas.bobas.i18n.I18N;

public class DbConnection implements IDbConnection {

	private DbConnection() {

	}

	public DbConnection(java.sql.Connection connection) throws DbException {
		this();
		if (connection == null) {
			throw new DbException(I18N.prop("msg_bobas_invaild_database_connection"));
		}
		this.dbConnection = connection;
	}

	@Override
	public String getURL() throws DbException {
		try {
			if (this.isClosed()) {
				throw new DbException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			return this.dbConnection.getMetaData().getURL();
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public String getUserName() throws DbException {
		try {
			if (this.isClosed()) {
				throw new DbException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			return this.dbConnection.getMetaData().getUserName();
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private java.sql.Connection dbConnection = null;
	private volatile boolean opened = false;

	@Override
	public synchronized boolean open() throws DbException {
		if (this.opened) {
			// 已打开数据库连接，或打开事务
			return false;
		}
		this.opened = true;
		return true;
	}

	@Override
	public synchronized void close() throws DbException {
		try {
			if (this.inTransaction()) {
				// 处于事务中，不允许关闭数据库连接
				throw new DbException(I18N.prop("msg_bobas_database_has_not_commit_transaction"));
			}
			if (this.dbConnection != null) {
				this.dbConnection.close();
			}
			this.dbConnection = null;
			// 重置状态
			this.opened = false;
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public synchronized boolean isClosed() throws DbException {
		try {
			if (this.dbConnection == null) {
				// 没有有效的数据库连接，则认为是关闭的
				return true;
			}
			if (this.dbConnection.isClosed()) {
				// 数据库连接关闭
				return true;
			}
			return false;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public IDbCommand createCommand() throws DbException {
		try {
			if (this.isClosed()) {
				throw new DbException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			return new DbCommand(this.dbConnection.createStatement());
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public synchronized boolean beginTransaction() throws DbException {
		try {
			// 手动提交事务
			if (this.isClosed()) {
				throw new DbException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			if (this.inTransaction()) {
				return false;
			}
			if (!this.opened) {
				this.open();
			}
			// 手动提交事务
			this.dbConnection.setAutoCommit(false);
			return true;
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public synchronized void rollbackTransaction() throws DbException {
		try {
			if (this.isClosed()) {
				throw new DbException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			this.dbConnection.rollback();
			this.dbConnection.setAutoCommit(true);
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public synchronized void commitTransaction() throws DbException {
		try {
			if (this.isClosed()) {
				throw new DbException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			this.dbConnection.commit();
			this.dbConnection.setAutoCommit(true);
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	@Override
	public synchronized boolean inTransaction() throws DbException {
		try {
			return !this.dbConnection.getAutoCommit();
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

}
