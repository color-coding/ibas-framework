package org.colorcoding.ibas.bobas.bo;

/**
 * 业务对象的用户字段接口
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOUserFields {

	/**
	 * 用户自定义字段前缀（U_XXX）
	 */
	String USER_FIELD_PREFIX_SIGN = "U_";

	/**
	 * 用户字段集合
	 * 
	 * @return
	 */
	IUserFields getUserFields();

}
