package org.colorcoding.ibas.bobas.repository;

public class BORepositoryService extends BORepository4DB {

	/**
	 * 主库标记
	 */
	public final static String MASTER_REPOSITORY_SIGN = "Master";

	public BORepositoryService() {
		super(MASTER_REPOSITORY_SIGN);
	}
}
