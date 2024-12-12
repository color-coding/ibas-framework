package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;

public abstract class DbAdapter {

	public abstract Connection createConnection(String server, String dbName, String userName, String userPwd);
}