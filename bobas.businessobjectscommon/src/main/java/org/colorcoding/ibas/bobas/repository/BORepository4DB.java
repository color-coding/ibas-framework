package org.colorcoding.ibas.bobas.repository;

import java.sql.Connection;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DbFactory;
import org.colorcoding.ibas.bobas.db.DbTransaction;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class BORepository4DB extends BORepository {

	public BORepository4DB() {
		this(Strings.VALUE_EMPTY);
	}

	public BORepository4DB(String dbSign) {
		this.dbSign = dbSign;
	}

	private String dbSign;
	private Connection dbConnection;

	public final synchronized void connect(String dbType, String dbServer, String dbName, String dbUser,
			String dbPassword) throws RepositoryException {
		try {
			if (Strings.isNullOrEmpty(dbType)) {
				dbType = MyConfiguration
						.getConfigValue(Strings.concat(this.dbSign, MyConfiguration.CONFIG_ITEM_DB_TYPE, "sqlite"));
			}
			if (Strings.isNullOrEmpty(dbServer)) {
				dbServer = MyConfiguration.getConfigValue(
						Strings.concat(this.dbSign, MyConfiguration.CONFIG_ITEM_DB_SERVER), "localhost");
			}
			if (Strings.isNullOrEmpty(dbName)) {
				dbName = MyConfiguration
						.getConfigValue(Strings.concat(this.dbSign, MyConfiguration.CONFIG_ITEM_DB_NAME), "ibas_demo");
			}
			if (Strings.isNullOrEmpty(dbUser)) {
				dbUser = MyConfiguration
						.getConfigValue(Strings.concat(this.dbSign, MyConfiguration.CONFIG_ITEM_DB_USER_ID), "sa");
			}
			if (Strings.isNullOrEmpty(dbPassword)) {
				dbPassword = MyConfiguration.getConfigValue(
						Strings.concat(this.dbSign, MyConfiguration.CONFIG_ITEM_DB_USER_PASSWORD), "1q2w3e");
			}
			if (this.dbConnection == null) {
				this.dbConnection = DbFactory.create().createConnection(dbType, dbServer, dbName, dbName, dbUser);
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	public final void connect(String dbServer, String dbName, String dbUser, String dbPassword)
			throws RepositoryException {
		this.connect(Strings.VALUE_EMPTY, dbServer, dbName, dbUser, dbPassword);
	}

	public final void connect(String dbName, String dbUser, String dbPassword) throws RepositoryException {
		this.connect(Strings.VALUE_EMPTY, Strings.VALUE_EMPTY, dbName, dbUser, dbPassword);
	}

	public final void connect() throws RepositoryException {
		this.connect(Strings.VALUE_EMPTY, Strings.VALUE_EMPTY, Strings.VALUE_EMPTY, Strings.VALUE_EMPTY,
				Strings.VALUE_EMPTY);
	}

	@Override
	public synchronized void close() throws Exception {
		super.close();
		if (this.dbConnection != null) {
			if (this.dbConnection.isClosed() == false) {
				this.dbConnection.close();
			}
		}
		this.dbConnection = null;
	}

	@Override
	protected synchronized ITransaction startTransaction() throws RepositoryException {
		if (this.dbConnection == null) {
			this.connect();
		}
		if (this.dbConnection == null) {
			throw new RepositoryException(I18N.prop("msg_bobas_invaild_database_connection"));
		}
		return new DbTransaction(this.dbConnection);
	}

}
