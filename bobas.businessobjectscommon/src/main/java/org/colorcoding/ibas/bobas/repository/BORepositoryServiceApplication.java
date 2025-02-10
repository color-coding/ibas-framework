package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.ownership.IDataOwnership;
import org.colorcoding.ibas.bobas.ownership.IOwnershipJudger;
import org.colorcoding.ibas.bobas.ownership.OwnershipFactory;
import org.colorcoding.ibas.bobas.ownership.UnauthorizedException;

public class BORepositoryServiceApplication extends BORepositoryService {

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

	/**
	 * 设置当前用户
	 * 
	 * @param token
	 */
	public final void setCurrentUser(String token) {
		if (this.getCurrentUser() != null && Strings.equalsIgnoreCase(this.getCurrentUser().getToken(), token)) {
			// 与当前的口令相同，不做处理
			return;
		}
		IOrganizationManager orgManager = OrganizationFactory.create().createManager();
		IUser user = orgManager.getUser(token);
		if (user == null) {
			// 没有用户匹配次口令
			throw new InvalidAuthorizationException(I18N.prop("msg_bobas_no_user_match_the_token"));
		}
		this.setCurrentUser(user);
	}

	/**
	 * 查询数据
	 * 
	 * @param <T>      对象类型
	 * @param criteria 查询条件
	 * @param boType   对象类型
	 * @return
	 */
	protected final <T extends IBusinessObject> IOperationResult<T> fetch(ICriteria criteria, Class<?> boType) {
		// 兼容性处理
		return this.fetch(boType, criteria);
	}

	private IOwnershipJudger ownershipJudger = null;

	private final IOwnershipJudger getOwnershipJudger() {
		if (this.ownershipJudger == null) {
			this.ownershipJudger = OwnershipFactory.create().createJudger();
		}
		return this.ownershipJudger;
	}

	@Override
	protected <T extends IBusinessObject> IOperationResult<T> fetch(Class<?> boType, ICriteria criteria) {
		try {
			if (criteria == null) {
				criteria = new Criteria();
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
			OperationResult<T> operationResult = new OperationResult<T>();
			if (filterCriteria != null && filterCriteria.getChildCriterias().isEmpty()
					&& !filterCriteria.getConditions().isEmpty()
					&& filterCriteria.getConditions().firstOrDefault().getRelationship() == ConditionRelationship.AND) {
				// 使用过滤查询（存在子查询则不使用，第一个条件为“或”不能使用）
				criteria = criteria.copyFrom(filterCriteria);
				IOperationResult<T> opRslt = super.fetch(boType, criteria);
				Logger.log("repository: filter fetch [%s], result [%s].", boType.getName(),
						opRslt.getResultObjects().size());
				operationResult.addResultObjects(opRslt.getResultObjects());
			} else {
				int filterCount = 0;// 过滤的数量
				int fetchTime = 0;// 查询的次数
				int fetchCount = 0;// 查询的数量
				boolean dataFull = true;// 数据填充满
				ICriteria oCriteria = criteria;// 原始查询
				if (criteria.getResultCount() > 0) {
					dataFull = false;
					oCriteria = criteria.clone();
				}
				// 使用对象过滤
				do {
					// 循环查询数据，直至填满或没有新的数据
					IOperationResult<T> opRslt = super.fetch(boType, criteria);
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
						criteria = oCriteria.next(opRslt.getResultObjects().lastOrDefault());
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
				Logger.log("repository: fetch [%s] [%s] times, result [%s] filtering [%s].", boType.getName(),
						fetchTime, fetchCount, filterCount);
			}
			return operationResult;
		} catch (Exception e) {
			// 如果出错，不返回处理一半的数据
			return new OperationResult<T>(e);
		}
	}

	@Override
	protected <T extends IBusinessObject> IOperationResult<T> save(T bo) {
		try {
			// 数据保存权限
			if (bo instanceof IDataOwnership) {
				IOwnershipJudger ownershipJudger = this.getOwnershipJudger();
				// 系统用户不受权限控制
				if (this.getCurrentUser() == OrganizationFactory.SYSTEM_USER) {
					ownershipJudger = null;
				}
				if (ownershipJudger != null) {
					if (!ownershipJudger.canSave((IDataOwnership) bo, this.getCurrentUser(), true)) {
						throw new UnauthorizedException(
								I18N.prop("msg_bobas_to_save_bo_unauthorized", bo.getClass().getSimpleName()));
					}
				}
			}
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
		return super.save(bo);
	}

}
