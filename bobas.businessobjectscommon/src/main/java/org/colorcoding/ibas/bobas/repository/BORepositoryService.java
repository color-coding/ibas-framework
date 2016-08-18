package org.colorcoding.ibas.bobas.repository;

import java.util.LinkedList;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionsEvent;
import org.colorcoding.ibas.bobas.core.SaveActionsException;
import org.colorcoding.ibas.bobas.core.SaveActionsListener;
import org.colorcoding.ibas.bobas.core.SaveActionsType;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.organization.UnknownUser;

/**
 * 业务仓库服务
 * 
 * 
 * @author niuren.zhu
 *
 */
public class BORepositoryService implements IBORepositoryService, SaveActionsListener {

	public BORepositoryService() {
		// 是否使用缓存
		this.setUseCache(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_REPOSITORY_DISABLED_CACHE, false));
		// 是否保存后检索新实例
		this.setRefetchAfterSave(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_REFETCH, false));
		// 是否通知事务
		this.setPostTransaction(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_POST_TRANSACTION, false));
	}

	/**
	 * 主库标记
	 */
	public final static String MASTER_REPOSITORY_SIGN = "Master";
	private IBORepository repository = null;

	@Override
	public final synchronized IBORepository getRepository() {
		if (this.repository == null) {
			this.setRepository(new BORepository4Db(MASTER_REPOSITORY_SIGN));
		}
		return this.repository;
	}

	@Override
	public final synchronized void setRepository(IBORepository repository) {
		if (this.repository != null) {
			// 移出事件监听
			this.repository.removeSaveActionsListener(this);
		}
		this.repository = repository;
		if (this.repository != null) {
			// 同步用户信息
			if (this.currentUser != null && !(this.currentUser instanceof UnknownUser)) {
				this.repository.setCurrentUser(this.currentUser);
			} else {
				if (this.repository.getCurrentUser() != null
						&& !(this.repository.getCurrentUser() instanceof UnknownUser)) {
					this.setCurrentUser(this.repository.getCurrentUser());
				}
			}
			// 监听对象保存动作
			this.repository.addSaveActionsListener(this);
		}
	}

	@Override
	public void connectRepository(String type, String server, String name, String user, String password)
			throws InvalidRepositoryException {
		try {
			IBORepository4Db dbRepository = new BORepository4Db();
			dbRepository.connectDb(type, server, name, user, password);
			this.setRepository(dbRepository);
		} catch (Exception e) {
			throw new InvalidRepositoryException(e);
		}
	}

	@Override
	public void connectRepository(String server, String name, String user, String password)
			throws InvalidRepositoryException {
		this.connectRepository(null, server, name, user, password);
	}

	protected boolean openRepository() throws RepositoryException {
		try {
			if (this.getRepository() instanceof IBORepository4Db) {
				return ((IBORepository4Db) this.getRepository()).openDbConnection();
			}
			// throw new
			// NotSupportedException(i18n.prop("msg_bobas_not_supported"));
			return false;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	protected void closeRepository() throws RepositoryException {
		try {
			if (this.getRepository() instanceof IBORepository4Db) {
				((IBORepository4Db) this.getRepository()).closeDbConnection();
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	protected boolean inTransaction() {
		if (this.repository == null) {
			// 未初始化主仓库，则不存在事务
			return false;
		}
		return this.getRepository().inTransaction();
	}

	protected boolean beginTransaction() throws RepositoryException {
		return this.getRepository().beginTransaction();
	}

	protected void rollbackTransaction() throws RepositoryException {
		this.getRepository().rollbackTransaction();
	}

	protected void commitTransaction() throws RepositoryException {
		this.getRepository().commitTransaction();
	}

	public void dispose() throws RepositoryException {
		if (this.repository != null) {
			this.repository.dispose();
		}
		if (this.cacheRepository != null) {
			this.cacheRepository.dispose();
		}
	}

	private IBORepository4Cache cacheRepository = null;

	public final synchronized IBORepository4Cache getCacheRepository() {
		if (this.cacheRepository == null) {
			this.setCacheRepository(new BORepository4Cache());
		}
		return this.cacheRepository;
	}

	public final void setCacheRepository(IBORepository4Cache repository) {
		this.cacheRepository = repository;
		if (this.cacheRepository != null && this.currentUser != null && !(this.currentUser instanceof UnknownUser)) {
			this.cacheRepository.setCurrentUser(this.currentUser);
		}
	}

	private boolean useCache;

	/**
	 * 是否使用缓存
	 * 
	 * @return
	 */
	public final boolean isUseCache() {
		return useCache;
	}

	public final void setUseCache(boolean value) {
		this.useCache = value;
	}

	private boolean refetchAfterSave;

	/**
	 * 保存后是否重新查询数据
	 * 
	 * @return
	 */
	public boolean isRefetchAfterSave() {
		return refetchAfterSave;
	}

	public void setRefetchAfterSave(boolean value) {
		this.refetchAfterSave = value;
	}

	private boolean postTransaction;

	/**
	 * 是否通知事务
	 * 
	 * @return
	 */
	public boolean isPostTransaction() {
		return postTransaction;
	}

	public void setPostTransaction(boolean value) {
		this.postTransaction = value;
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
			this.currentUser = new UnknownUser();
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

	private void setCurrentUser(IUser user) {
		this.currentUser = user;
		if (this.repository != null) {
			this.getRepository().setCurrentUser(this.getCurrentUser());
		}
		if (this.cacheRepository != null) {
			this.getCacheRepository().setCurrentUser(this.getCurrentUser());
		}
		RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_CHANGED_USER, this.getCurrentUser());
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
	<P extends IBusinessObjectBase> OperationResult<P> fetch(IBORepositoryReadonly boRepository, ICriteria criteria,
			Class<P> boType) {
		OperationResult<P> operationResult = new OperationResult<P>();
		if (criteria == null) {
			criteria = new Criteria();
		}
		if (criteria.getNotLoadedChildren()) {
			// 不加载子项
			operationResult.copy(boRepository.fetch(criteria, boType));
		} else {
			// 加载子项
			operationResult.copy(boRepository.fetchEx(criteria, boType));
		}
		return operationResult;
	}

	/**
	 * 查询业务对象（数据库中）
	 * 
	 * @param criteria
	 *            查询条件
	 * 
	 * @param token
	 *            口令
	 * 
	 * @return 查询的结果
	 * @throws InvalidTokenException
	 */
	<P extends IBusinessObjectBase> OperationResult<P> fetchInDb(ICriteria criteria, Class<P> boType) {
		// 在数据库中查询
		RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_FETCHING_IN_DB, boType.getName());
		return this.fetch(this.getRepository(), criteria, boType);
	}

	/**
	 * 查询业务对象（缓存中）
	 * 
	 * @param criteria
	 *            查询条件
	 * 
	 * @param token
	 *            口令
	 * 
	 * @return 查询的结果
	 * @throws InvalidTokenException
	 */
	<P extends IBusinessObjectBase> OperationResult<P> fetchInCache(ICriteria criteria, Class<P> boType) {
		// 在缓冲中查询
		RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_FETCHING_IN_CACHE, boType.getName());
		return this.fetch(this.getCacheRepository(), criteria, boType);
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
	protected final <P extends IBusinessObjectBase> OperationResult<P> fetch(ICriteria criteria, String token,
			Class<P> boType) {
		try {
			this.setCurrentUser(token);// 解析并设置当前用户
		} catch (InvalidTokenException e) {
			RuntimeLog.log(e);
			return new OperationResult<P>(e);
		}
		if (this.isUseCache()) {
			// 优先使用缓存数据
			OperationResult<P> operationResult = this.fetchInCache(criteria, boType);
			if (operationResult.getResultCode() == 0
					&& operationResult.getResultObjects().size() >= criteria.getResultCount()
					&& criteria.getResultCount() > 0) {
				// 缓存中存在匹配数据，且结果数量满足要求
				return operationResult;
			} else {
				// 缓存中不存在数据，从数据库中查找
				ICriteria nCriteria = criteria.clone();// 不满足查询的部分
				nCriteria.setResultCount(criteria.getResultCount() - operationResult.getResultObjects().size());// 仅查询不够部分
				OperationResult<P> dbOpRslt = this.fetchInDb(nCriteria, boType);
				if (dbOpRslt.getError() != null) {
					operationResult.setError(dbOpRslt.getError());
					return operationResult;
				}
				if (dbOpRslt.getResultCode() != 0) {
					operationResult.setError(new RepositoryException(dbOpRslt.getMessage()));
					return operationResult;
				}
				// 接收数据库查询结果
				operationResult.addResultObjects(dbOpRslt.getResultObjects());
				operationResult.addInformations(dbOpRslt.getInformations());
				// 缓存新的数据
				try {
					this.getCacheRepository().cacheData(dbOpRslt.getResultObjects());
				} catch (Exception e) {
					RuntimeLog.log(e);
				}
				return operationResult;
			}
		}
		return this.fetchInDb(criteria, boType);
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
	IBusinessObjectBase save(IBORepository boRepository, IBusinessObjectBase bo) throws Exception {
		boolean myDbTrans = false;
		boolean myOpened = false;
		try {
			boolean toDelete = bo.isDeleted();
			boolean toAdd = bo.isNew();
			IBusinessObjectBase returnBO = null;// 返回的数据
			myOpened = this.openRepository();// 打开仓库
			myDbTrans = this.beginTransaction(); // 打开事务
			this.getProcessing().add(bo);// 添加待处理数据到列表
			IOperationResult<?> operationResult = boRepository.saveEx(bo); // 保存BO
			this.getProcessing().remove(bo);// 移出带处理数据
			// 其他
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			if (operationResult.getResultCode() != 0) {
				throw new Exception(operationResult.getMessage());
			}
			// 成功保存
			returnBO = (IBusinessObjectBase) operationResult.getResultObjects().firstOrDefault();
			if (this.isPostTransaction()) {
				// 通知事务
				TransactionType type = TransactionType.Update;
				if (toDelete) {
					type = TransactionType.Delete;
				} else if (toAdd) {
					type = TransactionType.Add;
				}
				this.noticeTransaction(type, bo);
			}
			if (myDbTrans)
				this.commitTransaction();// 结束事务
			if (toDelete) {
				// 删除操作，不返回实例
				returnBO = null;
			} else {
				// 非删除操作
				if (this.isRefetchAfterSave()) {
					// 要求重新查询
					try {
						operationResult = boRepository.fetchCopyEx(returnBO);
						if (operationResult.getError() != null) {
							throw operationResult.getError();
						}
						if (operationResult.getResultCode() != 0) {
							throw new Exception(operationResult.getMessage());
						}
						if (operationResult.getResultObjects().size() == 0) {
							throw new Exception(i18n.prop("msg_bobas_not_found_bo_copy", returnBO));
						}
						returnBO = (IBusinessObjectBase) operationResult.getResultObjects().firstOrDefault();
					} catch (Exception e) {
						throw new Exception(i18n.prop("msg_bobas_fetch_bo_copy_faild", returnBO));
					}
				}
			}
			return returnBO;
		} catch (Exception e) {
			if (myDbTrans)
				this.rollbackTransaction();// 自己打开的事务自己关闭
			throw e;
		} finally {
			if (myOpened)
				this.closeRepository();// 自己打开的连接，自己关闭
		}
	}

	private LinkedList<IBusinessObjectBase> processing;

	/**
	 * 处理中的数据
	 * 
	 * @return
	 */
	private LinkedList<IBusinessObjectBase> getProcessing() {
		if (processing == null) {
			synchronized (this) {
				if (processing == null) {
					processing = new LinkedList<>();
				}
			}
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
	 * @throws BOTransactionException
	 */
	private void noticeTransaction(TransactionType type, IBusinessObjectBase bo) throws BOTransactionException {
		// 通知事务
		try {
			if (this.getRepository() instanceof IBORepository4Db) {
				// 数据库仓库
				IBORepository4Db dbRepository = (IBORepository4Db) this.getRepository();
				IBOAdapter4Db adapter4Db = dbRepository.createDbAdapter().createBOAdapter();
				ISqlQuery sqlQuery = adapter4Db.parseBOTransactionNotification(type, bo);
				IOperationResult<?> spOpRslt = dbRepository.fetch(sqlQuery, TransactionMessage.class);
				if (spOpRslt.getError() != null) {
					throw spOpRslt.getError();
				}
				if (spOpRslt.getResultCode() != 0) {
					throw new Exception(spOpRslt.getMessage());
				}
				TransactionMessage message = (TransactionMessage) spOpRslt.getResultObjects().firstOrDefault();
				if (message == null) {
					throw new Exception(i18n.prop("msg_bobas_invaild_bo_transaction_message"));
				}
				if (message.getCode() != 0) {
					RuntimeLog.log(RuntimeLog.MSG_TRANSACTION_SP_VALUES, type.toString(), bo.toString(),
							message.getCode(), message.getMessage());
					throw new Exception(message.getMessage());
				}
			}
		} catch (Exception e) {
			throw new BOTransactionException(e.getMessage(), e);
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
	protected final <P extends IBusinessObjectBase> OperationResult<P> save(P bo, String token) {
		OperationResult<P> operationResult = new OperationResult<P>();
		try {
			this.setCurrentUser(token);// 解析并设置当前用户
			operationResult.addResultObjects(this.save(this.getRepository(), bo));
			if (this.isUseCache() && operationResult.getResultCode() == 0) {
				// 删除已缓存的数据
				try {
					this.getCacheRepository().clearData(bo);
				} catch (Exception e) {
					RuntimeLog.log(e);
				}
			}
		} catch (Exception e) {
			operationResult.setError(e);
			RuntimeLog.log(e);
		}
		return operationResult;
	}

	@Override
	public final boolean noticeActionsEvent(SaveActionsEvent event) {
		if (event == null) {
			return true;
		}
		if (!this.getProcessing().contains(event.getBO())) {
			// 不是自己导致的事件
			return true;
		}
		return this.onActionsEvent(event);
	}

	/**
	 * 监听对象保存事件
	 * 
	 * 每个对象保存都会触发，包括对象的子属性
	 */
	protected boolean onActionsEvent(SaveActionsEvent event) {

		if (event.getType() == SaveActionsType.before_updating) {
			if (!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_VERSION_CHECK, false)) {
				// 更新前，检查版本是否有效
				try {
					if (event.getBO() instanceof IBOStorageTag) {
						IOperationResult<?> opRslt = this.getRepository().fetchCopy(event.getBO());
						if (opRslt.getError() != null) {
							throw opRslt.getError();
						}
						if (opRslt.getResultCode() != 0) {
							throw new Exception(opRslt.getMessage());
						}
						Object boCopy = opRslt.getResultObjects().firstOrDefault();
						if (boCopy == null) {
							throw new Exception(i18n.prop("msg_bobas_not_found_bo_copy", event.getBO()));
						}
						IBOStorageTag boTag = (IBOStorageTag) event.getBO();
						IBOStorageTag copyTag = (IBOStorageTag) boCopy;
						if (copyTag.getLogInst() >= boTag.getLogInst()) {
							// 数据库版本更高
							throw new SaveActionsException(i18n.prop("msg_bobas_bo_copy_version_is_more_new"));
						}
					}
				} catch (Exception e) {
					throw new SaveActionsException(i18n.prop("msg_bobas_bo_version_check_faild", event.getBO()), e);
				}
			}
		}
		return true;
	}

}
