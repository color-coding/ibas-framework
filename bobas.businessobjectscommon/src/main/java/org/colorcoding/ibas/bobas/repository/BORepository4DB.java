package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DbException;

public class BORepository4DB extends BORepository {

	public BORepository4DB() {
		this(Strings.VALUE_EMPTY);
	}

	public BORepository4DB(String dbSign) {
		this.dbSign = dbSign;
	}

	private String dbSign;
	private String dbType;
	private String dbServer;
	private String dbName;
	private String dbUser;
	private String dbPassword;

	public void connect(String dbType, String dbServer, String dbName, String dbUser, String dbPassword)
			throws DbException {
		this.dbType = dbType;
		this.dbServer = dbServer;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

	public void connect(String dbServer, String dbName, String dbUser, String dbPassword) throws DbException {
		this.connect(Strings.VALUE_EMPTY, dbServer, dbName, dbUser, dbPassword);
	}

	@Override
	protected ITransaction startTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

}
