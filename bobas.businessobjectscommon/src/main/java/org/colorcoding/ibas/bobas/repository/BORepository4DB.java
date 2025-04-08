package org.colorcoding.ibas.bobas.repository;

import java.util.function.Supplier;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DbFactory;
import org.colorcoding.ibas.bobas.db.DbTransaction;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

public class BORepository4DB extends BORepository {

	public BORepository4DB() {
		this(Strings.VALUE_EMPTY);
	}

	public BORepository4DB(String dbSign) {
		this.dbSign = dbSign;
	}

	private String dbSign;
	private boolean myConnection = false;

	public final synchronized void connect(String dbType, String dbServer, String dbName, String dbUser,
			String dbPassword) throws RepositoryException {
		try {
			if (this.getTransaction() != null) {
				throw new RepositoryException(I18N.prop("msg_bobas_database_connection_already_exists"));
			}
			if (Strings.isNullOrEmpty(dbType)) {
				dbType = MyConfiguration
						.getConfigValue(Strings.concat(this.dbSign, MyConfiguration.CONFIG_ITEM_DB_TYPE), "sqlite");
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
			this.setTransaction(new DbTransaction(
					DbFactory.create().createConnection(dbType, dbServer, dbName, dbUser, dbPassword)) {
				@Override
				public Supplier<IUser> supplier() {
					return new Supplier<IUser>() {
						@Override
						public IUser get() {
							return BORepository4DB.this.getCurrentUser();
						}
					};
				}
			});
			this.myConnection = true;
		} catch (RepositoryException e) {
			throw e;
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
		if (this.myConnection == true) {
			// 非自建链接不释放
			if (this.getTransaction() != null) {
				this.getTransaction().close();
			}
		}
		super.close();
	}

	@Override
	public synchronized void setTransaction(ITransaction transaction) throws RepositoryException {
		super.setTransaction(transaction);
		this.myConnection = false;
		if (transaction instanceof DbTransaction) {
			Supplier<IUser> supplier = ((DbTransaction) transaction).supplier();
			if (supplier != null) {
				IUser user = supplier.get();
				if (user != OrganizationFactory.UNKNOWN_USER) {
					try {
						this.setUserToken(user.getToken());
					} catch (Exception e) {
						throw new RepositoryException(e);
					}
				}
			}
		}
	}

	@Override
	synchronized void initTransaction() throws RepositoryException {
		if (this.getTransaction() == null) {
			this.connect();
		}
		if (this.getTransaction() == null) {
			throw new RepositoryException(I18N.prop("msg_bobas_invaild_database_connection"));
		}
	}

}
