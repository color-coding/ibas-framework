package org.colorcoding.ibas.bobas.db;

import java.sql.SQLException;
import java.sql.Statement;

import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

public class DbConnection implements IDbConnection {

	private DbConnection() {

	}

	public DbConnection(java.sql.Connection connection) throws DbException {
		this();
		if (connection == null) {
			throw new DbException(i18n.prop("msg_bobas_invaild_database_connection"));
		}
		this.dbConnection = connection;
		try {
			// 手动提交事务
			this.dbConnection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DbException(e);
		}
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
				throw new Exception(i18n.prop("msg_bobas_database_connection_is_closed"));
			}
			return this.dbConnection.getMetaData().getURL();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public String getUserName() throws DbException {
		try {
			if (this.isClosed()) {
				throw new Exception(i18n.prop("msg_bobas_database_connection_is_closed"));
			}
			return this.dbConnection.getMetaData().getUserName();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	private java.sql.Connection dbConnection = null;
	private volatile boolean opened = false;

	@Override
	public void dispose() {
		if (this.dbConnection != null) {
			try {
				this.dbConnection.close();
			} catch (SQLException e) {
				RuntimeLog.log(e);
			}
		}
	}

	@Override
	public boolean open() throws DbException {
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
	public void close(boolean force) throws DbException {
		try {
			if (this.inTransaction()) {
				// 处于事务中，不允许关闭数据库连接
				throw new Exception(i18n.prop("msg_bobas_database_has_not_commit_transaction"));
			}
			if (force || !DbConnectionPool.isEnabled()) {
				// 没有开启缓存或强制关闭
				this.dbConnection.close();
			} else {
				boolean done = DbConnectionPool.giveBack(this);
				if (done) {
					this.recycled = true;
				} else {
					// 回收不成功，关闭连接
					this.dbConnection.close();
					this.recycled = false;
				}
			}
			// 重置状态
			this.opened = false;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
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
	final void setRecycled(boolean recycled) {
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
	public boolean isClosed() throws DbException {
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
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public IDbCommand createCommand() throws DbException {
		try {
			if (this.isClosed()) {
				throw new Exception(i18n.prop("msg_bobas_database_connection_is_closed"));
			}
			Statement statement = this.dbConnection.createStatement();
			return new DbCommand(statement);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean beginTransaction() throws DbException {
		try {
			// 手动提交事务
			if (this.isClosed()) {
				throw new Exception(i18n.prop("msg_bobas_database_connection_is_closed"));
			}
			if (this.inTransaction()) {
				return false;
			}
			this.inTransaction = true;
			if (!this.opened) {
				this.open();
			}
			return true;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public void rollbackTransaction() throws DbException {
		try {
			if (this.isClosed()) {
				throw new Exception(i18n.prop("msg_bobas_database_connection_is_closed"));
			}
			this.dbConnection.rollback();
			this.inTransaction = false;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public void commitTransaction() throws DbException {
		try {
			if (this.isClosed()) {
				throw new Exception(i18n.prop("msg_bobas_database_connection_is_closed"));
			}
			this.dbConnection.commit();
			this.inTransaction = false;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	private volatile boolean inTransaction = false;

	@Override
	public boolean inTransaction() {
		return this.inTransaction;
	}

}
