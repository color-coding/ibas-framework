package org.colorcoding.ibas.bobas.repository;

import java.util.List;
import java.util.Vector;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionEvent;
import org.colorcoding.ibas.bobas.core.SaveActionListener;
import org.colorcoding.ibas.bobas.core.SaveActionType;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

/**
 * 业务仓库服务
 * 
 * 
 * @author niuren.zhu
 *
 */
public class BORepositoryService implements IBORepositoryService {
	protected static final String MSG_REPOSITORY_FETCHING_IN_DB = "repository: fetching [%s] in db repository.";
	protected static final String MSG_REPOSITORY_CHANGED_USER = "repository: changed user [%s].";
	protected static final String MSG_REPOSITORY_REPLACED_BE_DELETED_BO = "repository: replaced be deleted bo [%s].";
	protected static final String MSG_REPOSITORY_NOT_FOUND_BE_DELETED_BO = "repository: not found be deleted bo [%s].";
	protected static final String MSG_REPOSITORY_CANNOT_BE_OPENED = "repository: cannot be opened.";
	protected static final String MSG_TRANSACTION_SP_VALUES = "transaction: sp [%s] [%s] [%s - %s]";

	public BORepositoryService() {
		// 是否保存后检索新实例
		this.setRefetchAfterSave(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_REFETCH, false));
		// 是否通知事务
		this.setPostTransaction(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_POST_TRANSACTION, false));
		// 是否删除前重新查询
		this.setRefetchBeforeDelete(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_REFETCH_BEFORE_DELETE, false));
	}

	/**
	 * 主库标记
	 */
	public final static String MASTER_REPOSITORY_SIGN = "Master";
	private IBORepository repository = null;

	@Override
	public final IBORepository getRepository() {
		if (this.repository == null) {
			this.setRepository(new BORepository4Db(MASTER_REPOSITORY_SIGN));
		}
		return this.repository;
	}

	private SaveActionListener saveListener = new SaveActionListener() {
		@Override
		public void onActionEvent(SaveActionEvent event) throws RepositoryException {
			if (BORepositoryService.this.getProcessing().contains(event.getTrigger())) {
				if (event.getTrigger() instanceof IBusinessObject) {
					BORepositoryService.this.onSaveActionEvent(event.getType(), (IBusinessObject) event.getTrigger());

				}
			}
		}
	};

	@Override
	public final void setRepository(IBORepository repository) {
		if (this.repository != null) {
			// 移出事件监听
			this.repository.removeListener(this.saveListener);
		}
		this.repository = repository;
		if (this.repository != null) {
			// 同步用户信息
			if (this.currentUser != null) {
				this.repository.setCurrentUser(this.currentUser);
			} else {
				if (this.repository.getCurrentUser() != null
						&& this.repository.getCurrentUser() != OrganizationFactory.UNKNOWN_USER) {
					this.setCurrentUser(this.repository.getCurrentUser());
				}
			}
			// 监听对象保存动作
			this.repository.registerListener(this.saveListener);
		}
	}

	@Override
	public void connectRepository(String type, String server, String name, String user, String password)
			throws RepositoryException {
		try {
			IBORepository4Db dbRepository = new BORepository4Db();
			dbRepository.connectDb(type, server, name, user, password);
			this.setRepository(dbRepository);
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public final void connectRepository(String server, String name, String user, String password)
			throws RepositoryException {
		this.connectRepository(null, server, name, user, password);
	}

	protected boolean openRepository() throws RepositoryException {
		try {
			if (this.getRepository() instanceof IBORepository4Db) {
				return ((IBORepository4Db) this.getRepository()).openDbConnection();
			}
			return false;
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	protected void closeRepository() throws RepositoryException {
		try {
			if (this.repository instanceof IBORepository4Db) {
				((IBORepository4Db) this.getRepository()).closeDbConnection();
			}
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	public boolean inTransaction() {
		if (this.repository == null) {
			// 未初始化主仓库，则不存在事务
			return false;
		}
		return this.getRepository().inTransaction();
	}

	public boolean beginTransaction() throws RepositoryException {
		return this.getRepository().beginTransaction();
	}

	public void rollbackTransaction() throws RepositoryException {
		this.getRepository().rollbackTransaction();
	}

	public void commitTransaction() throws RepositoryException {
		this.getRepository().commitTransaction();
	}

	public void dispose() throws RepositoryException {
		if (this.repository != null) {
			this.repository.dispose();
			this.repository = null;
		}
	}

	private boolean refetchAfterSave;

	/**
	 * 保存后是否重新查询数据
	 * 
	 * @return
	 */
	public final boolean isRefetchAfterSave() {
		return refetchAfterSave;
	}

	public final void setRefetchAfterSave(boolean value) {
		this.refetchAfterSave = value;
	}

	private boolean refetchBeforeDelete;

	/**
	 * 删除前是否查询数据
	 * 
	 * @return
	 */
	public final boolean isRefetchBeforeDelete() {
		return refetchBeforeDelete;
	}

	public final void setRefetchBeforeDelete(boolean value) {
		this.refetchBeforeDelete = value;
	}

	private boolean postTransaction;

	/**
	 * 是否通知事务
	 * 
	 * @return
	 */
	public final boolean isPostTransaction() {
		return postTransaction;
	}

	public final void setPostTransaction(boolean value) {
		this.postTransaction = value;
	}

	private IUser currentUser = null;

	/**
	 * 当前用户
	 * 
	 * @return
	 */
	public final IUser getCurrentUser() {
		if (this.currentUser == null) {
			// 未设置用户则为未知用户
			this.currentUser = OrganizationFactory.UNKNOWN_USER;
		}
		return this.currentUser;
	}

	/**
	 * 设置当前用户
	 * 
	 * @param user
	 */
	protected final void setCurrentUser(IUser user) {
		if (this.currentUser == user) {
			// 相同用户，不切换
			return;
		}
		this.currentUser = user;
		if (this.repository != null) {
			this.getRepository().setCurrentUser(this.getCurrentUser());
		}
		Logger.log(MSG_REPOSITORY_CHANGED_USER, this.getCurrentUser());
		this.onCurrentUserChanged();
	}

	/**
	 * 设置当前用户
	 * 
	 * @param token
	 *            用户口令
	 * @throws InvalidTokenException
	 */
	protected final void setCurrentUser(String token) throws InvalidTokenException {
		if (this.currentUser != null && this.currentUser.getToken() != null
				&& this.currentUser.getToken().equals(token)) {
			// 与当前的口令相同，不做处理
			return;
		}
		IOrganizationManager orgManager = OrganizationFactory.create().createManager();
		IUser user = orgManager.getUser(token);
		if (user == null) {
			// 没有用户匹配次口令
			throw new InvalidTokenException(I18N.prop("msg_bobas_no_user_match_the_token"));
		}
		this.setCurrentUser(user);
	}

	/**
	 * 当前用户变化
	 */
	protected void onCurrentUserChanged() {

	}

	/**
	 * 查询业务对象
	 * 
	 * @param boRepository
	 *            使用的仓库
	 * 
	 * @param criteria
	 *            查询条件
	 * 
	 * @param token
	 *            口令
	 * 
	 * @return 查询的结果
	 */
	<P extends IBusinessObject> IOperationResult<P> fetch(IBORepositoryReadonly boRepository, ICriteria criteria,
			Class<P> boType) {
		if (criteria == null) {
			criteria = new Criteria();
		}
		if (criteria.isNoChilds()) {
			// 不加载子项
			return boRepository.fetch(criteria, boType);
		} else {
			// 加载子项
			return boRepository.fetchEx(criteria, boType);
		}
	}

	/**
	 * 查询业务对象 根据配置是否启用缓存
	 * 
	 * @param criteria
	 *            查询条件
	 * 
	 * @param token
	 *            口令
	 * 
	 * @return 查询的结果
	 */
	protected final <P extends IBusinessObject> OperationResult<P> fetch(ICriteria criteria, String token,
			Class<P> boType) {
		try {
			// 解析并设置当前用户
			this.setCurrentUser(token);
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<P>(e);
		}
		return (OperationResult<P>) this.fetch(criteria, boType);
	}

	<P extends IBusinessObject> IOperationResult<P> fetch(ICriteria criteria, Class<P> boType) {
		return this.fetch(this.getRepository(), criteria, boType);
	}

	/**
	 * 保存业务对象
	 * 
	 * @param boRepository
	 *            业务对象仓库
	 * 
	 * @param bo
	 *            业务对象
	 * @return 注意删除时返回null
	 * @throws Exception
	 */
	<P extends IBusinessObject> P save(IBORepository boRepository, P bo) throws Exception {
		boolean myDbTrans = false;
		boolean myOpened = false;
		try {
			boolean toDelete = bo.isDeleted();
			boolean toAdd = bo.isNew();
			P returnBO = null;// 返回的数据
			myOpened = this.openRepository();// 打开仓库
			myDbTrans = this.beginTransaction(); // 打开事务
			this.getProcessing().add(bo);// 添加待处理数据到列表
			IOperationResult<P> operationResult = boRepository.saveEx(bo); // 保存BO
			this.getProcessing().remove(bo);// 移出带处理数据
			// 其他
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			// 成功保存
			returnBO = operationResult.getResultObjects().firstOrDefault();
			if (this.isPostTransaction()) {
				// 通知事务
				TransactionType type = TransactionType.UPDATE;
				if (toDelete) {
					type = TransactionType.DELETE;
				} else if (toAdd) {
					type = TransactionType.ADD;
				}
				this.postTransaction(type, bo);
			}
			if (myDbTrans) {
				this.commitTransaction();// 结束事务
			}
			if (toDelete) {
				// 删除操作，不返回实例
				returnBO = null;
			} else if (!bo.isSavable()) {
				// 此对象没有保存，则直接返回
			} else {
				// 非删除操作
				if (this.isRefetchAfterSave()) {
					// 要求重新查询
					try {
						operationResult = boRepository.fetchCopyEx(returnBO);
						if (operationResult.getError() != null) {
							throw operationResult.getError();
						}
						if (operationResult.getResultObjects().isEmpty()) {
							throw new Exception(I18N.prop("msg_bobas_not_found_bo_copy", returnBO));
						}
						returnBO = operationResult.getResultObjects().firstOrDefault();
					} catch (Exception e) {
						throw new Exception(I18N.prop("msg_bobas_fetch_bo_copy_faild", returnBO), e);
					}
				}
			}
			return returnBO;
		} catch (Exception e) {
			if (myDbTrans) {
				this.rollbackTransaction();// 自己打开的事务自己关闭
			}
			throw e;
		} finally {
			if (myOpened) {
				this.closeRepository();// 自己打开的连接，自己关闭
			}
		}
	}

	<P extends IBusinessObject> P save(P bo) throws Exception {
		return this.save(this.getRepository(), bo);
	}

	private List<IBusinessObject> processing;

	/**
	 * 处理中的数据
	 * 
	 * @return
	 */
	private List<IBusinessObject> getProcessing() {
		if (processing == null) {
			processing = new Vector<>();
		}
		return processing;
	}

	/**
	 * 通知业务对象的事务
	 * 
	 * @param type
	 *            事务类型
	 * @param bo
	 *            对象
	 * @throws Exception
	 * @throws BOTransactionException
	 */
	private void postTransaction(TransactionType type, IBusinessObject bo) throws Exception {
		// 通知事务
		if (this.getRepository() instanceof IBORepository4Db) {
			// 数据库仓库
			IBORepository4Db dbRepository = (IBORepository4Db) this.getRepository();
			IBOAdapter4Db adapter4Db = dbRepository.getBOAdapter();
			ISqlQuery sqlQuery = adapter4Db.parseTransactionNotification(type, bo);
			IOperationResult<TransactionMessage> spOpRslt = dbRepository.fetch(sqlQuery, TransactionMessage.class);
			if (spOpRslt.getError() != null) {
				throw spOpRslt.getError();
			}
			TransactionMessage message = spOpRslt.getResultObjects().firstOrDefault();
			if (message == null) {
				throw new Exception(I18N.prop("msg_bobas_invaild_bo_transaction_message"));
			}
			if (message.getCode() != 0) {
				Logger.log(MessageLevel.DEBUG, MSG_TRANSACTION_SP_VALUES, type.toString(), bo.toString(),
						message.getCode(), message.getMessage());
				throw new Exception(message.getMessage());
			}
		}
	}

	/**
	 * 保存业务对象
	 * 
	 * @param bo
	 *            业务对象
	 * 
	 * @param token
	 *            口令
	 * 
	 * @return 查询的结果
	 */
	protected final <P extends IBusinessObject> OperationResult<P> save(P bo, String token) {
		OperationResult<P> operationResult = new OperationResult<P>();
		try {
			this.setCurrentUser(token);// 解析并设置当前用户
			if (bo != null && bo.isDeleted() && this.isRefetchBeforeDelete()) {
				// 删除前重新查询数据，避免漏或多删子项
				@SuppressWarnings("unchecked")
				IOperationResult<P> opRsltFetch = this.fetch(bo.getCriteria(), (Class<P>) bo.getClass());
				P boCopy = opRsltFetch.getResultObjects().firstOrDefault();
				if (boCopy != null && boCopy.getClass() == bo.getClass()) {
					// 使用BO的删除方法，引用对象时不进行删除操作
					boCopy.delete();
					bo = boCopy;
					Logger.log(MessageLevel.DEBUG, MSG_REPOSITORY_REPLACED_BE_DELETED_BO, bo);
				} else {
					// 没有找到有效的副本
					Logger.log(MessageLevel.WARN, MSG_REPOSITORY_NOT_FOUND_BE_DELETED_BO, bo);
				}
			}
			operationResult.addResultObjects(this.save(bo));
		} catch (Exception e) {
			operationResult.setError(e);
			Logger.log(e);
		}
		return operationResult;
	}

	/**
	 * 保存事件
	 * 
	 * @param action
	 *            事件类型
	 * @param trigger
	 *            发生对象
	 * @return
	 */
	protected void onSaveActionEvent(SaveActionType action, IBusinessObject trigger) throws RepositoryException {
	}

}
