package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

/**
 * 文件仓库服务
 * 
 * 
 * @author niuren.zhu
 *
 */
public class FileRepositoryService implements IFileRepositoryService {

    public FileRepositoryService() {

    }

    private IFileRepository repository;

    @Override
    public final IFileRepository getRepository() {
        if (this.repository == null) {
            this.setRepository(new FileRepository());
        }
        return this.repository;
    }

    @Override
    public final void setRepository(IFileRepository repository) {
        this.repository = repository;
    }

    private IUser currentUser = null;

    /**
     * 当前用户
     * 
     * @return
     */
    public IUser getCurrentUser() {
        if (this.currentUser == null) {
            // 未设置用户则为未知用户
            this.currentUser = OrganizationFactory.UNKNOWN_USER;
        }
        return this.currentUser;
    }

    /**
     * 设置当前用户
     * 
     * @param token
     *            用户口令
     * @throws InvalidTokenException
     */
    void setCurrentUser(String token) throws InvalidTokenException {
        try {
            if (this.currentUser != null && this.currentUser.getToken() != null
                    && this.currentUser.getToken().equals(token)) {
                // 与当前的口令相同，不做处理
                return;
            }
            IOrganizationManager orgManager = OrganizationFactory.createManager();
            IUser user = orgManager.getUser(token);
            if (user == null) {
                // 没有用户匹配次口令
                throw new InvalidTokenException(i18n.prop("msg_bobas_no_user_match_the_token"));
            }
            this.setCurrentUser(user);
        } catch (Exception e) {
            throw new InvalidTokenException(e.getMessage(), e);
        }
    }

    void setCurrentUser(IUser user) {
        this.currentUser = user;
        RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_CHANGED_USER, this.getCurrentUser());
    }

    /**
     * 查询文件数据
     * 
     * @param criteria
     *            查询
     * @param token
     *            用户口令
     * @return
     */
    protected IOperationResult<FileData> fetch(ICriteria criteria, String token) {
        try {
            this.setCurrentUser(token);
            return this.getRepository().fetch(criteria);
        } catch (Exception e) {
            RuntimeLog.log(e);
            return new OperationResult<>(e);
        }
    }

    /**
     * 保存文件数据
     * 
     * @param fileData
     *            文件数据
     * @param token
     *            用户口令
     * @return
     */
    protected IOperationResult<FileData> save(FileData fileData, String token) {
        try {
            this.setCurrentUser(token);
            return this.getRepository().save(fileData);
        } catch (Exception e) {
            RuntimeLog.log(e);
            return new OperationResult<>(e);
        }
    }
}
