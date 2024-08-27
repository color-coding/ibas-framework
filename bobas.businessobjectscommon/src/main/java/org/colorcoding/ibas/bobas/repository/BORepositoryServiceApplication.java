package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.mapping.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
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
	static final String CRITERIA_CONDITION_ALIAS_TAG_DELETED = "Deleted";
	protected static final String MSG_REPOSITORY_FETCH_AND_FILTERING = "repository: fetch [%s] [%s] times, result [%s] filtering [%s].";
	protected static final String MSG_REPOSITORY_FILTERING_AND_FETCH = "repository: filter fetch [%s], result [%s].";
	/**
	 * 操作信息：数据检索数量
	 */
	public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_FETCH_COUNT = "DATA_OWNERSHIP_FETCH_COUNT";
	/**
	 * 操作信息：数据过滤数量
	 */
	public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT = "DATA_OWNERSHIP_FILTER_COUNT";
	/**
	 * 操作信息标签：权限判断
	 */
	public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_TAG = "DATA_OWNERSHIP_JUDGE";

	public BORepositoryServiceApplication() {
		super();
		// 是否保存后检索新实例
		this.setRefetchAfterSave(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_REFETCH, false));
		// 是否删除前重新查询
		this.setRefetchBeforeDelete(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_REFETCH_BEFORE_DELETE, false));
		// 是否过滤标记删除数据
		this.setFilterTagDeleted(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_FILTER_TAG_DELETED, true));
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

	private boolean filterTagDeleted;

	public final boolean isFilterTagDeleted() {
		return filterTagDeleted;
	}

	public final void setFilterTagDeleted(boolean filterTagDeleted) {
		this.filterTagDeleted = filterTagDeleted;
	}

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
	 * @param boRepository 使用的仓库
	 * 
	 * @param criteria     查询条件
	 * 
	 * @param token        口令
	 * 
	 * @return 查询的结果
	 */
	@Override
	<P extends IBusinessObject> OperationResult<P> fetch(IBORepositoryReadonly boRepository, ICriteria criteria,
			Class<P> boType) {
		OperationResult<P> operationResult = new OperationResult<P>();
		try {
			if (criteria == null) {
				criteria = new Criteria();
			}
			if (IBOTagDeleted.class.isAssignableFrom(boType)) {
				// 标记删除类型
				if (criteria.getConditions().firstOrDefault(
						c -> c.getAlias().equalsIgnoreCase(CRITERIA_CONDITION_ALIAS_TAG_DELETED)) == null) {
					// 不存在删除条件，添加过滤
					if (criteria.getConditions().size() > 1) {
						criteria.getConditions().firstOrDefault()
								.setBracketOpen(criteria.getConditions().firstOrDefault().getBracketOpen() + 1);
						criteria.getConditions().lastOrDefault()
								.setBracketClose(criteria.getConditions().lastOrDefault().getBracketClose() + 1);
					}
					ICondition condition = criteria.getConditions().create();
					condition.setBracketOpen(1);
					condition.setRelationship(ConditionRelationship.AND);
					condition.setAlias(CRITERIA_CONDITION_ALIAS_TAG_DELETED);
					condition.setOperation(ConditionOperation.NOT_EQUAL);
					condition.setValue(emYesNo.YES);
					condition = criteria.getConditions().create();
					condition.setRelationship(ConditionRelationship.OR);
					condition.setAlias(CRITERIA_CONDITION_ALIAS_TAG_DELETED);
					condition.setOperation(ConditionOperation.IS_NULL);
					condition.setBracketClose(1);
				}
			}
			IOwnershipJudger ownershipJudger = this.getOwnershipJudger();
			// 系统用户不受权限控制
			if (this.getCurrentUser() == OrganizationFactory.SYSTEM_USER) {
				ownershipJudger = null;
			}
			// 获取过滤查询
			ICriteria filterCriteria = null;
			if (ownershipJudger != null) {
				if (IDataOwnership.class.isAssignableFrom(boType)) {
					BusinessObjectUnit boUnit = boType.getAnnotation(BusinessObjectUnit.class);
					if (boUnit != null) {
						filterCriteria = ownershipJudger.filterCriteria(boUnit, this.getCurrentUser());
					}
				}
			}
			if (filterCriteria != null && filterCriteria.getChildCriterias().isEmpty()
					&& !filterCriteria.getConditions().isEmpty()
					&& filterCriteria.getConditions().firstOrDefault().getRelationship() == ConditionRelationship.AND) {
				// 使用过滤查询（存在子查询则不使用，第一个条件为“或”不能使用）
				criteria = criteria.copyFrom(filterCriteria);
				IOperationResult<P> opRslt = super.fetch(boRepository, criteria, boType);
				Logger.log(MSG_REPOSITORY_FILTERING_AND_FETCH, boType.getName(), opRslt.getResultObjects().size());
				operationResult.addResultObjects(opRslt.getResultObjects());
			} else {
				int filterCount = 0;// 过滤的数量
				int fetchTime = 0;// 查询的次数
				int fetchCount = 0;// 查询的数量
				boolean dataFull = criteria.getResultCount() > 0 ? false : true;// 数据填充满
				ICriteria oCriteria = criteria;// 原始查询
				// 使用对象过滤
				do {
					// 循环查询数据，直至填满或没有新的数据
					IOperationResult<P> opRslt = super.fetch(boRepository, criteria, boType);
					fetchTime++;// 查询计数加1
					if (opRslt.getError() != null) {
						throw opRslt.getError();
					}
					fetchCount += opRslt.getResultObjects().size();
					if (ownershipJudger != null) {
						// 数据权限过滤，系统用户不考虑权限
						for (Object item : opRslt.getResultObjects()) {
							if (item instanceof IDataOwnership) {
								// 有继承数据权限
								if (!this.getOwnershipJudger().canRead((IDataOwnership) item, this.getCurrentUser())) {
									// 没读取权限，过滤数量加1
									filterCount++;
									continue;
								}
							}
							operationResult.addResultObjects(item);
							if (operationResult.getResultObjects().size() >= criteria.getResultCount()
									&& criteria.getResultCount() > 0) {
								// 够了退出
								break;
							}
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
						IBusinessObject lastBO = opRslt.getResultObjects().lastOrDefault();
						criteria = oCriteria.next(lastBO);// 下组数据的查询条件
					}
				} while (!dataFull);
				if (filterCount > 0) {
					// 发生数据过滤，返回过滤信息
					operationResult.addInformations(OPERATION_INFORMATION_DATA_OWNERSHIP_FETCH_COUNT,
							I18N.prop("msg_bobas_data_ownership_fetch_count", fetchCount),
							OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
					operationResult.addInformations(OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT,
							I18N.prop("msg_bobas_data_ownership_filter_count", filterCount),
							OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
				}
				Logger.log(MSG_REPOSITORY_FETCH_AND_FILTERING, boType.getName(), fetchTime, fetchCount, filterCount);
			}
		} catch (Exception e) {
			// 如果出错，不返回处理一半的数据
			operationResult = new OperationResult<P>(e);
		}
		return operationResult;
	}

	/**
	 * 保存业务对象
	 * 
	 * @param boRepository 业务对象仓库
	 * 
	 * @param bo           业务对象
	 * 
	 * @param token        口令
	 * 
	 * @return 查询的结果
	 * @throws Exception
	 */
	@Override
	<P extends IBusinessObject> P save(IBORepository boRepository, P bo) throws Exception {
		// 数据权限过滤，系统用户不考虑权限
		if (this.getOwnershipJudger() != null && bo instanceof IDataOwnership
				&& this.getCurrentUser() != OrganizationFactory.SYSTEM_USER) {
			if (!this.getOwnershipJudger().canSave((IDataOwnership) bo, this.getCurrentUser(), true)) {
				throw new UnauthorizedException(
						I18N.prop("msg_bobas_to_save_bo_unauthorized", bo.getClass().getSimpleName()));
			}
		}
		// 将要删除的数据
		boolean deleted = bo.isDeleted();
		if (deleted) {
			// 已使用的数据，是否允许标记删除
			if (bo instanceof IBOTagDeleted) {
				IBOTagDeleted boTag = (IBOTagDeleted) bo;
				if (boTag.getReferenced() == emYesNo.YES && !MyConfiguration
						.getConfigValue(MyConfiguration.CONFIG_ITEM_ENABLED_AUTOMATIC_TAG_DELETION, false)) {
					throw new BusinessLogicException(I18N.prop("msg_bobas_not_allow_delete_referenced_bo", bo));
				}
			}
			// 删除前重新查询数据，避免漏或多删子项
			if (this.isRefetchBeforeDelete()) {
				IOperationResult<P> opRsltCopy = boRepository.fetchCopyEx(bo);
				if (opRsltCopy.getError() != null) {
					throw opRsltCopy.getError();
				}
				P boCopy = opRsltCopy.getResultObjects().firstOrDefault();
				if (boCopy != null && boCopy.getClass() == bo.getClass()) {
					// 使用BO的删除方法，引用对象时不进行删除操作
					boCopy.delete();
					bo = boCopy;
					Logger.log(MessageLevel.DEBUG, MSG_REPOSITORY_REPLACED_BE_DELETED_BO, bo);
				} else {
					// 没有找到有效的副本
					throw new Exception(I18N.prop("msg_bobas_not_found_bo_copy", bo));
				}
			}
		}
		P nBO = super.save(boRepository, bo);
		// 要求重新查询
		if (!deleted && nBO.isSavable() && this.isRefetchAfterSave()) {
			// 要求重新查询
			IOperationResult<P> opRsltCopy = boRepository.fetchCopyEx(nBO);
			if (opRsltCopy.getError() != null) {
				throw opRsltCopy.getError();
			}
			P boCopy = opRsltCopy.getResultObjects().firstOrDefault();
			if (boCopy != null && boCopy.getClass() == nBO.getClass()) {
				nBO = boCopy;
			} else {
				// 没有找到有效的副本
				throw new Exception(I18N.prop("msg_bobas_not_found_bo_copy", nBO));
			}
		}
		// 记录对象实例（重新查询的则有存储过程影响）
		if (nBO instanceof IBOStorageTag && nBO.isSavable()) {
			// nBo非空（非删除）
			this.getOwnershipJudger().logging((IBOStorageTag) nBO, this.getCurrentUser());
		} else if (deleted && bo instanceof IBOStorageTag && bo.isSavable()) {
			// 删除时，使用旧实例
			this.getOwnershipJudger().logging((IBOStorageTag) bo, this.getCurrentUser());
		}
		return nBO;
	}

}
