package org.colorcoding.ibas.bobas.db.mysql;

import org.colorcoding.ibas.bobas.db.BOAdapter4Db;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.ISqlScripts;

public class BOAdapter extends BOAdapter4Db implements IBOAdapter4Db {

	private ISqlScripts sqlScripts = null;

	@Override
	protected ISqlScripts getSqlScripts() {
		if (this.sqlScripts == null) {
			this.sqlScripts = new SqlScripts();
		}
		return this.sqlScripts;
	}

}
