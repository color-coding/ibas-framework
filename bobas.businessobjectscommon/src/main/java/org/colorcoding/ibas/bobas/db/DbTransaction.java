package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
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
	@SuppressWarnings("unchecked")
	public <T extends IBusinessObject> T[] fetch(ICriteria criteria, Class<T> boType) throws RepositoryException {
		try {
			// 查询方法，不主动开启事务
			criteria = this.getAdapter().convert(criteria, boType);
			try (PreparedStatement statement = this.connection
					.prepareStatement(this.getAdapter().parsingSelect(boType, criteria))) {
				if (criteria != null && !criteria.getConditions().isEmpty()) {
					int index = 0;
					for (ICondition condition : criteria.getConditions()) {
						if (condition.getOperation() == ConditionOperation.IS_NULL
								|| condition.getOperation() == ConditionOperation.NOT_NULL) {
							// 空比较，不需要值
							continue;
						}
						if (!Strings.isNullOrEmpty(condition.getComparedAlias())) {
							// 字段间比较，不需要值
							continue;
						}
						// 填充语句参数值，注意：包含、开始等，类型应为字符，且需要补充%号
						if ((condition.getOperation() == ConditionOperation.START
								|| condition.getOperation() == ConditionOperation.END
								|| condition.getOperation() == ConditionOperation.CONTAIN
								|| condition.getOperation() == ConditionOperation.NOT_CONTAIN)) {
							statement.setObject(index, condition.getValue(),
									this.getAdapter().toSqlTypes(DbFieldType.ALPHANUMERIC));
						} else {
							statement.setObject(index, condition.getValue(),
									this.getAdapter().toSqlTypes(condition.getAliasDataType()));
						}
						index += 1;
					}
				}
				// 运行查询
				try (ResultSet resultSet = statement.executeQuery()) {
					return (T[]) this.getAdapter().parsingDatas(boType, resultSet).toArray();
				}
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public <T extends IBusinessObject> T save(T bo) {
		Objects.requireNonNull(bo);
		return bo;
	}

}
