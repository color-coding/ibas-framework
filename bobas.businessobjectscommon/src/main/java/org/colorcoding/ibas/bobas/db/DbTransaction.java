package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.RepositoryException;
import org.colorcoding.ibas.bobas.repository.Transaction;

public class DbTransaction extends Transaction {

	public DbTransaction(Connection connection) {
		Objects.requireNonNull(connection);
		this.connection = connection;
	}

	private Connection connection;

	protected synchronized final Connection getConnection() throws SQLException {
		if (this.connection == null) {
			throw new SQLException(I18N.prop("msg_bobas_invaild_database_connection"));
		}
		return connection;
	}

	private DbAdapter adapter;

	protected DbAdapter getAdapter() throws Exception {
		if (this.adapter == null) {
			this.adapter = DbFactory.create().createAdapter(this.connection.getClass().getName());
		}
		if (this.adapter == null) {
			throw new Exception("not found db adapter.");
		}
		return this.adapter;
	}

	public synchronized boolean inTransaction() throws SQLException {
		return !this.getConnection().getAutoCommit();
	}

	public synchronized boolean beginTransaction() throws SQLException {
		if (this.isClosed()) {
			throw new SQLException(I18N.prop("msg_bobas_database_connection_is_closed"));
		}
		if (this.inTransaction()) {
			return false;
		}
		// 手动提交事务
		this.getConnection().setAutoCommit(false);
		return true;
	}

	public synchronized boolean isClosed() throws SQLException {
		if (this.getConnection().isClosed()) {
			return true;
		}
		return false;
	}

	@Override
	public synchronized void close() throws Exception {
		try {
			if (this.inTransaction()) {
				// 处于事务中，不允许关闭数据库连接
				throw new SQLException(I18N.prop("msg_bobas_database_has_not_commit_transaction"));
			}
			if (this.isClosed() == false) {
				this.getConnection().close();
			}
			super.close();
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public synchronized void rollback() throws RepositoryException {
		try {
			if (this.isClosed()) {
				throw new SQLException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			this.getConnection().rollback();
			this.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public synchronized void commit() throws RepositoryException {
		try {
			if (this.isClosed()) {
				throw new SQLException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			this.getConnection().commit();
			this.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public <T extends IBusinessObject> T[] fetch(ICriteria criteria, Class<T> boType) {
		// 查询方法，不主动开启事务

		return null;
	}

	@Override
	public <T extends IBusinessObject> T save(T bo) {
		// TODO Auto-generated method stub
		return null;
	}

}
