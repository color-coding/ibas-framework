package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 聪明的业务仓库服务
 * 
 * 数据库读写分离逻辑（开启事务使用主库；没有开启使用只读库）
 * 
 * @author niuren.zhu
 *
 */
public class BORepositorySmartService extends BORepositoryLogicService implements IBORepositorySmartService {

	protected static final String MSG_REPOSITORY_FETCHING_IN_READONLY_REPOSITORY = "repository: fetching [%s] in readonly repository.";
	protected static final String MSG_REPOSITORY_FETCHING_IN_MASTER_REPOSITORY = "repository: fetching [%s] in master repository.";

	public BORepositorySmartService() {
		super();
		this.setEnabledReadonlyRepository(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_ENABLED_READONLY_REPOSITORY, false));
	}

	private boolean enabledReadonlyRepository = false;

	/**
	 * 是否开启只读仓库
	 * 
	 * @return
	 */
	public final boolean isEnabledReadonlyRepository() {
		return enabledReadonlyRepository;
	}

	/**
	 * 设置只读仓库是否开启
	 * 
	 * @param value
	 */
	public final void setEnabledReadonlyRepository(boolean value) {
		this.enabledReadonlyRepository = value;
	}

	/**
	 * 只读库标记
	 */
	public final static String READONLY_REPOSITORY_SIGN = "Readonly";
	private IBORepositoryReadonly readonlyRepository = null;

	@Override
	public final IBORepositoryReadonly getReadonlyRepository() {
		if (this.readonlyRepository == null) {
			if (this.isEnabledReadonlyRepository()) {
				this.setReadonlyRepository(new BORepository4DbReadonly(READONLY_REPOSITORY_SIGN));
			}
		}
		return this.readonlyRepository;
	}

	@Override
	public final void setReadonlyRepository(IBORepositoryReadonly repository) {
		this.readonlyRepository = repository;
		if (this.readonlyRepository != null) {
			this.readonlyRepository.setCurrentUser(this.getCurrentUser());
		}
	}

	@Override
	public void connectRepository(String type, String server, String name, String user, String password)
			throws RepositoryException {
		super.connectRepository(type, server, name, user, password);
		this.setEnabledReadonlyRepository(false);// 手动连接不启动只读库
	}

	@Override
	public void connectReadonlyRepository(String type, String server, String name, String user, String password)
			throws RepositoryException {
		try {
			IBORepository4DbReadonly dbRepository = new BORepository4DbReadonly();
			dbRepository.connectDb(type, server, name, user, password);
			this.setReadonlyRepository(dbRepository);
			this.setEnabledReadonlyRepository(true);// 启动只读库
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void connectReadonlyRepository(String server, String name, String user, String password)
			throws RepositoryException {
		this.connectReadonlyRepository(null, server, name, user, password);
	}

	@Override
	public void dispose() throws RepositoryException {
		super.dispose();
		if (this.readonlyRepository != null) {
			this.readonlyRepository.dispose();
		}
	}

	/**
	 * 重写数据库查询（处于事务中使用主库查询；非事务中使用只读库）
	 */
	@Override
	<P extends IBusinessObject> IOperationResult<P> fetch(ICriteria criteria, Class<P> boType) {
		if (!this.inTransaction() && this.isEnabledReadonlyRepository()) {
			// 没在事务中，则使用只读库
			IBORepositoryReadonly repository = this.getReadonlyRepository();
			if (repository != null) {
				// 只读库有效
				Logger.log(MSG_REPOSITORY_FETCHING_IN_READONLY_REPOSITORY, boType.getName());
				return this.fetch(this.getReadonlyRepository(), criteria, boType);
			}
		}
		// 处于事务中或只读库无效，则使用主库
		Logger.log(MSG_REPOSITORY_FETCHING_IN_MASTER_REPOSITORY, boType.getName());
		return this.fetch(this.getRepository(), criteria, boType);
	}

}
