package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.ownership.IDataOwnership;
import org.colorcoding.ibas.bobas.ownership.IOwnershipJudger;
import org.colorcoding.ibas.bobas.ownership.OwnershipFactory;
import org.colorcoding.ibas.bobas.ownership.UnauthorizedException;

/**
 * 业务仓库服务应用
 * 
 * 含权限体系处理
 * 
 * @author Niuren.Zhu
 *
 */
public class BORepositoryServiceApplication extends BORepositorySmartService implements IBORepositoryApplication {

	private String userToken = null;

	@Override
	public final String getUserToken() {
		return userToken;
	}

	@Override
	public final void setUserToken(String userToken) throws InvalidTokenException {
		this.userToken = userToken;
		this.setCurrentUser(this.getUserToken());
	}

	@Override
	protected void onCurrentUserChanged() {
		super.onCurrentUserChanged();
		try {
			this.setUserToken(this.getCurrentUser().getToken());
		} catch (InvalidTokenException e) {
			throw new RuntimeException(e);
		}
	}

	private IOwnershipJudger ownershipJudger = null;

	private final IOwnershipJudger getOwnershipJudger() {
		if (this.ownershipJudger == null) {
			this.ownershipJudger = OwnershipFactory.create().createJudger();
		}
		return this.ownershipJudger;
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
	@Override
	<P extends IBusinessObjectBase> OperationResult<P> fetch(IBORepositoryReadonly boRepository, ICriteria criteria,
			Class<P> boType) {
		OperationResult<P> operationResult = new OperationResult<P>();
		try {
			if (criteria == null) {
				criteria = new Criteria();
			}
			Integer filterCount = 0;// 过滤的数量
			Integer fetchTime = 0;// 查询的次数
			Integer fetchCount = 0;// 查询的数量
			boolean dataFull = true;// 数据填充满
			if (criteria.getResultCount() > 0) {
				// 有结果数量约束
				dataFull = false;
			}
			do {
				// 循环查询数据，直至填满或没有新的数据
				IOperationResult<P> opRslt = super.fetch(boRepository, criteria, boType);
				fetchTime++;// 查询计数加1
				if (opRslt.getError() != null) {
					throw opRslt.getError();
				}
				if (opRslt.getResultCode() != 0) {
					throw new RepositoryException(opRslt.getMessage(), opRslt.getError());
				}
				fetchCount += opRslt.getResultObjects().size();
				if (this.getOwnershipJudger() != null) {
					// 数据权限过滤
					for (Object item : opRslt.getResultObjects()) {
						if ((item instanceof IDataOwnership)) {
							// 有继承数据权限
							if (!this.getOwnershipJudger().canRead((IDataOwnership) item, this.getCurrentUser())) {
								// 没读取权限，过滤数量加1
								filterCount++;
								continue;
							}
						}
						operationResult.addResultObjects(item);
					}
				} else {
					operationResult.addResultObjects(opRslt.getResultObjects());
				}
				if (operationResult.getResultObjects().size() >= criteria.getResultCount()
						|| opRslt.getResultObjects().size() < criteria.getResultCount()) {
					// 结果数量大于要求数量或此次查询结果不够应返回数量
					dataFull = true;// 标记满
				}
				if (!dataFull) {
					// 结果数量不满足，进行下一组数据查询
					IBusinessObjectBase lastBO = opRslt.getResultObjects().lastOrDefault();
					criteria = criteria.next(lastBO);// 下组数据的查询条件
				}
			} while (!dataFull);
			if (filterCount > 0) {
				// 发生数据过滤，返回过滤信息
				operationResult.addInformations(OwnershipFactory.createOwnershipJudgeInfo(fetchTime, filterCount));
			}
			RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_FETCH_AND_FILTERING, boType.getName(), fetchTime, fetchCount,
					filterCount);
		} catch (Exception e) {
			// 如果出错，不返回处理一半的数据
			operationResult = new OperationResult<P>();
			operationResult.setError(e);
		}
		return operationResult;
	}

	/**
	 * 保存业务对象
	 * 
	 * @param boRepository
	 *            业务对象仓库
	 * 
	 * @param bo
	 *            业务对象
	 * 
	 * @param token
	 *            口令
	 * 
	 * @return 查询的结果
	 * @throws Exception
	 */
	@Override
	IBusinessObjectBase save(IBORepository boRepository, IBusinessObjectBase bo) throws Exception {
		if (this.getOwnershipJudger() != null && bo instanceof IDataOwnership) {
			// 数据权限过滤
			if (!this.getOwnershipJudger().canSave((IDataOwnership) bo, this.getCurrentUser(), true)) {
				throw new UnauthorizedException(i18n.prop("msg_bobas_to_save_bo_unauthorized", bo.toString()));
			}
		}
		return super.save(boRepository, bo);
	}

}
