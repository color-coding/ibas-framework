package org.colorcoding.ibas.bobas.file;

public abstract class TransactionManager {

	String sign;

	public String getSign() {
		return this.sign;
	}

	public abstract FileTransaction createTransaction(String repository);
}
