package org.colorcoding.ibas.bobas.db.dm8;

import org.colorcoding.ibas.bobas.db.IBOAdapter;
import org.colorcoding.ibas.bobas.db.ISqlScripts;

public class BOAdapter extends org.colorcoding.ibas.bobas.db.BOAdapter implements IBOAdapter {

	private ISqlScripts sqlScripts = null;

	@Override
	public ISqlScripts getSqlScripts() {
		if (this.sqlScripts == null) {
			this.sqlScripts = new SqlScripts();
		}
		return this.sqlScripts;
	}

}
