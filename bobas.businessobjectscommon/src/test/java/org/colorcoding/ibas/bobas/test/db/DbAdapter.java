package org.colorcoding.ibas.bobas.test.db;

import org.colorcoding.ibas.bobas.db.BOAdapter;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;
import org.colorcoding.ibas.bobas.db.ISqlScripts;
import org.colorcoding.ibas.bobas.db.SqlScripts;

/**
 * 代理-测试用
 * 
 * @author Niuren.Zhu
 *
 */
public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	protected Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		String dbURL = String.format("jdbc:sqlserver://%s;DatabaseName=%s;ApplicationName=%s", server, dbName,
				applicationName);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Connection(dbURL, userName);
	}

	@Override
	public IBOAdapter createBOAdapter() {
		return new BOAdapter() {

			private ISqlScripts sqlScripts = null;

			@Override
			public ISqlScripts getSqlScripts() {
				if (this.sqlScripts == null) {
					this.sqlScripts = new SqlScripts();
				}
				return this.sqlScripts;
			}

		};
	}

}
