package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DbFactory;
import org.colorcoding.ibas.bobas.db.DbTransaction;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

/**
 * 业务对象仓库（数据库方式），自动管理数据库连接和事务
 *
 * @author Niuren.Zhu
 *
 */
public class BORepository4DB extends BORepository {

	public BORepository4DB() {
		this(Strings.VALUE_EMPTY);
	}

	public BORepository4DB(String dbSign) {
		this.dbSign = dbSign;
	}

	private String dbSign;
	private boolean myConnection = false;

	/**
	 * 连接数据库，参数为空时从配置读取默认值。已有连接时抛出异常。
	 *
	 * @param dbType     数据库类型，为空时从配置读取
	 * @param dbServer   服务器地址，为空时从配置读取
	 * @param dbName     数据库名，为空时从配置读取
	 * @param dbUser     用户名，为空时从配置读取
	 * @param dbPassword 密码，为空时从配置读取
	 * @throws RepositoryException 连接已存在或创建失败
	 */
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
			DbTransaction transaction = new DbTransaction(
					DbFactory.create().createConnection(dbType, dbServer, dbName, dbUser, dbPassword));
			transaction.setUser(this.getCurrentUser());
			this.setTransaction(transaction);
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

	/** 使用配置默认值连接数据库 */
	public final void connect() throws RepositoryException {
		this.connect(Strings.VALUE_EMPTY, Strings.VALUE_EMPTY, Strings.VALUE_EMPTY, Strings.VALUE_EMPTY,
				Strings.VALUE_EMPTY);
	}

	@Override
	public synchronized void close() throws RuntimeException {
		try {
			if (this.myConnection == true) {
				// 非自建链接不释放
				if (this.getTransaction() != null) {
					this.getTransaction().close();
				}
				this.myConnection = false;
			}
			super.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void setTransaction(ITransaction transaction) {
		super.setTransaction(transaction);
		this.myConnection = false;
		if (transaction instanceof DbTransaction) {
			DbTransaction dbTrans = (DbTransaction) transaction;
			// 从 transaction 读取用户，同步到 repository
			IUser user = dbTrans.getUser();
			if (user != null && user != OrganizationFactory.UNKNOWN_USER) {
				try {
					this.setUserToken(user.getToken());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * 初始化事务，无事务时自动连接数据库
	 *
	 * @return true自建连接，false事务已存在
	 * @throws RepositoryException 连接失败
	 */
	@Override
	protected synchronized boolean initTransaction() throws RepositoryException {
		if (this.getTransaction() == null) {
			this.connect();
			return true;
		}
		if (this.getTransaction() == null) {
			throw new RepositoryException(I18N.prop("msg_bobas_invalid_database_connection"));
		}
		return false;
	}

}