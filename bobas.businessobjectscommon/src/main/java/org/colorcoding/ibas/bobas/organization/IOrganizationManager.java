package org.colorcoding.ibas.bobas.organization;

/**
 * 组织管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IOrganizationManager {
    /**
     * 未知用户ID
     */
    static final int UNKNOWN_USER_SIGN = -1;

    /**
     * 初始化
     */
    void initialize();

    /**
     * 获取用户
     * 
     * @param token
     *            用户的口令
     */
    IUser getUser(String token);

    /**
     * 获取用户
     * 
     * @param id
     *            用户ID
     * @return
     */
    IUser getUser(int id);
}
