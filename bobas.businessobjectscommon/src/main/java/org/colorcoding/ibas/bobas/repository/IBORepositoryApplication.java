package org.colorcoding.ibas.bobas.repository;

/**
 * 业务仓库应用
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepositoryApplication extends IBORepositorySmartService {

	/**
	 * 获取-用户口令
	 */
	void setUserToken(String token);

	/**
	 * 设置-用户口令
	 */
	String getUserToken();
}
