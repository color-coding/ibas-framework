package org.colorcoding.ibas.bobas.db;

import java.sql.SQLException;

import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

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

	private String connectionSign = null;

	String getConnectionSign() {
		return connectionSign;
	}

	void setConnectionSign(String connectionSign) {
		this.connectionSign = connectionSign;
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
	public void dispose() {
		if (this.dbConnection != null) {
			try {
				this.dbConnection.close();
				this.dbConnection = null;
			} catch (SQLException e) {
				Logger.log(e);
			}
		}
	}

	@Override
	public synchronized boolean open() throws DbException {
		if (this.opened) {
			// 已打开数据库连接，或打开事务
			return false;
		}
		this.recycled = false;
		this.opened = true;
		return true;
	}

	@Override
	public void close() throws DbException {
		this.close(false);
	}

	@Override
	public synchronized void close(boolean force) throws DbException {
		try {
			if (this.inTransaction()) {
				// 处于事务中，不允许关闭数据库连接
				throw new DbException(I18N.prop("msg_bobas_database_has_not_commit_transaction"));
			}
			if (force || !DbConnectionPool.isEnabled()) {
				// 没有开启缓存或强制关闭
				this.dbConnection.close();
				this.dbConnection = null;
			} else {
				boolean done = DbConnectionPool.giveBack(this);
				if (done) {
					this.recycled = true;
				} else {
					// 回收不成功，关闭连接
					this.dbConnection.close();
					this.dbConnection = null;
					this.recycled = false;
				}
			}
			// 重置状态
			this.opened = false;
		} catch (DbException e) {
			throw e;
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private volatile boolean recycled = false;

	/**
	 * 是否回收
	 * 
	 * @return
	 */
	final boolean isRecycled() {
		return recycled;
	}

	/**
	 * 设置回收状态
	 * 
	 * @param recycled
	 */
	final synchronized void setRecycled(boolean recycled) {
		this.recycled = recycled;
	}

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	final boolean isValid() {
		try {
			if (this.dbConnection == null) {
				// 没有有效的数据库连接
				return false;
			}
			if (this.dbConnection.isClosed()) {
				// 数据库连接关闭
				return false;
			}
			return true;
		} catch (SQLException e) {
			return false;
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
			if (this.recycled) {
				// 已被回收
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
