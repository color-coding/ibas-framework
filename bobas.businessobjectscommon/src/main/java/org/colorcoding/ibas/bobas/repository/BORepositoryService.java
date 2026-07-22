package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.Condition;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.expression.BOJudgmentLinkCondition;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 业务对象仓库
 * 
 * @author Niuren.Zhu
 *
 */
public class BORepositoryService extends BORepository4DB {

	/**
	 * 主库标记
	 */
	public final static String MASTER_REPOSITORY_SIGN = "Master";

	public BORepositoryService(String sign) {
		super(sign);
	}

	public BORepositoryService() {
		this(MASTER_REPOSITORY_SIGN);
	}

	private boolean skipInstanceFetch = false;

	/**
	 * 跳过对象实例检索
	 *
	 * @return 为true时保存后不重新获取对象实例
	 */
	protected final boolean isSkipInstanceFetch() {
		return skipInstanceFetch;
	}

	protected final void setSkipInstanceFetch(boolean skipInstanceFetch) {
		this.skipInstanceFetch = skipInstanceFetch;
	}

	/**
	 * 查询数据
	 * 
	 * 可基于子项结果再查父项
	 * 
	 * @param <T>      对象类型
	 * @param boType   对象类型
	 * @param criteria 查询条件
	 * @return 操作结果，包含查询到的对象或错误信息
	 */
	@Override
	protected <T extends IBusinessObject> OperationResult<T> fetch(Class<?> boType, ICriteria criteria) {
		if (criteria != null) {
			// 有子项的查询结果后，再筛选父项
			IChildCriteria cCriteria = criteria.getChildCriterias().firstOrDefault(c -> c.isEntry());
			if (cCriteria != null && !Strings.isNullOrEmpty(cCriteria.getPropertyPath())) {
				IPropertyInfo<?> propertyInfo = BOFactory.propertyInfos(boType)
						.firstOrDefault(c -> c.getName().equalsIgnoreCase(cCriteria.getPropertyPath()));
				if (propertyInfo == null) {
					return new OperationResult<>(new RepositoryException(
							I18N.prop("msg_bobas_not_found_bo_property", cCriteria.getPropertyPath())));
				}
				if (propertyInfo.getValueType() == null
						|| !IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
					return new OperationResult<>(
							new RepositoryException(I18N.prop("msg_bobas_invalid_argument", propertyInfo.getName())));
				}
				Class<?> subType = null;
				Object tmpObject = BOFactory.newInstance(boType);
				if (tmpObject instanceof IBusinessObject) {
					tmpObject = BOUtilities.propertyValue((IBusinessObject) tmpObject, propertyInfo);
					if (tmpObject instanceof IBusinessObjects<?, ?>) {
						subType = ((IBusinessObjects<?, ?>) tmpObject).getElementType();
					}
				}
				if (subType == null || !IBusinessObject.class.isAssignableFrom(subType)) {
					return new OperationResult<>(
							new RepositoryException(I18N.prop("msg_bobas_invalid_argument", propertyInfo.getName())));
				}
				IOperationResult<IBusinessObject> opRsltChilds = super.fetch(subType, cCriteria);
				if (opRsltChilds.getError() != null) {
					// 直接返回，不需要重新 new
					return new OperationResult<T>(opRsltChilds);
				}
				// 无结果，不进行父项查询
				if (opRsltChilds.getResultObjects().isEmpty()) {
					return new OperationResult<T>();
				}
				// 新建主项查询
				ICriteria pCriteria = new Criteria();

				for (IBusinessObject item : opRsltChilds.getResultObjects()) {
					if (item instanceof IBODocumentLine) {
						ICondition condition = new Condition();
						condition.setAlias(IBODocument.MASTER_PRIMARY_KEY_NAME);
						condition.setValue(((IBODocumentLine) item).getDocEntry());
						if (pCriteria.getConditions().contains(c -> Strings.equals(c.getAlias(), condition.getAlias())
								&& Strings.equals(c.getValue(), condition.getValue()))) {
							continue;
						}
						if (pCriteria.getConditions().size() > 0) {
							condition.setRelationship(ConditionRelationship.OR);
						}
						pCriteria.getConditions().add(condition);
					} else if (item instanceof IBOSimpleLine) {
						ICondition condition = new Condition();
						condition.setAlias(IBOSimple.MASTER_PRIMARY_KEY_NAME);
						condition.setValue(((IBOSimpleLine) item).getObjectKey());
						if (pCriteria.getConditions().contains(c -> Strings.equals(c.getAlias(), condition.getAlias())
								&& Strings.equals(c.getValue(), condition.getValue()))) {
							continue;
						}
						if (pCriteria.getConditions().size() > 0) {
							condition.setRelationship(ConditionRelationship.OR);
						}
						pCriteria.getConditions().add(condition);
					} else if (item instanceof IBOMasterDataLine) {
						ICondition condition = new Condition();
						condition.setAlias(IBOMasterData.MASTER_PRIMARY_KEY_NAME);
						condition.setValue(((IBOMasterDataLine) item).getCode());
						if (pCriteria.getConditions().contains(c -> Strings.equals(c.getAlias(), condition.getAlias())
								&& Strings.equals(c.getValue(), condition.getValue()))) {
							continue;
						}
						if (pCriteria.getConditions().size() > 0) {
							condition.setRelationship(ConditionRelationship.OR);
						}
						pCriteria.getConditions().add(condition);
					} else {
						ICondition condition = null;
						IPropertyInfo<?> cKey = null;
						int index = pCriteria.getConditions().size();
						List<IPropertyInfo<?>> cKeys = BOFactory.propertyInfos(subType).where(c -> c.isPrimaryKey());
						for (IPropertyInfo<?> pKey : BOFactory.propertyInfos(boType).where(c -> c.isPrimaryKey())) {
							cKey = cKeys.firstOrDefault(c -> Strings.equals(pKey.getName(), c.getName()));
							if (cKey == null) {
								return new OperationResult<>(new RepositoryException(
										I18N.prop("msg_bobas_invalid_argument", pKey.getName())));
							}
							condition = new Condition();
							condition.setAlias(pKey);
							condition.setValue(BOUtilities.propertyValue(item, cKey));
							pCriteria.getConditions().add(condition);
						}
						// 没有增加条件，跳过
						if (index == pCriteria.getConditions().size()) {
							continue;
						}
						if (pCriteria.getConditions().size() > (index + 1)) {
							pCriteria.getConditions().get(index).addBracketOpen();
							pCriteria.getConditions().lastOrDefault().addBracketClose();
						}
						// 不是首个条件
						if (index > 0) {
							pCriteria.getConditions().get(index).setRelationship(ConditionRelationship.OR);
						}
					}
				}
				// 合并查询
				ICriteria nCriteria = criteria.clone();
				nCriteria.setNoChilds(true);
				nCriteria.getChildCriterias().clear();
				if (nCriteria.getConditions().size() > 1) {
					nCriteria.getConditions().firstOrDefault().addBracketOpen();
					nCriteria.getConditions().lastOrDefault().addBracketClose();
				}
				if (pCriteria.getConditions().size() > 1) {
					pCriteria.getConditions().firstOrDefault().addBracketOpen();
					pCriteria.getConditions().lastOrDefault().addBracketClose();
				}
				nCriteria.getConditions().addAll(pCriteria.getConditions());
				// 查询父项，并填充子项
				OperationResult<T> opRsltParent = super.fetch(boType, nCriteria);
				if (opRsltParent.getError() != null) {
					// 直接返回，不需要重新 new
					return opRsltParent;
				}
				IBusinessObject cData = null;
				BOJudgmentLinkCondition judgmentLink = null;
				IBusinessObjects<IBusinessObject, IBusinessObject> tmpObjects = null;
				for (T data : opRsltParent.getResultObjects()) {
					tmpObjects = ((FieldedObject) data).getProperty(propertyInfo);
					if (tmpObjects instanceof IBusinessObjects<?, ?>) {
						judgmentLink = new BOJudgmentLinkCondition();
						judgmentLink.parsingConditions(tmpObjects.getElementCriteria().getConditions());
						for (int i = 0; i < opRsltChilds.getResultObjects().size(); i++) {
							cData = opRsltChilds.getResultObjects().get(i);
							if (cData == null || !judgmentLink.judge(cData)) {
								continue;
							}
							tmpObjects.add(cData);
							opRsltChilds.getResultObjects().set(i, null);
						}
					}
				}
				return opRsltParent;
			}
		}
		// 父项查询，再查子项
		return super.fetch(boType, criteria);
	}

	/**
	 * 保存数据
	 *
	 * 自建事务时保存后重新获取对象实例；skipInstanceFetch为true时跳过重新获取
	 *
	 * @param <T> 对象类型
	 * @param bo  待保存对象
	 * @return 操作结果，自建事务时包含重新获取的实例，否则包含保存结果或错误信息
	 */
	@Override
	protected <T extends IBusinessObject> OperationResult<T> save(T bo) {
		boolean mine = false;
		try {
			mine = this.beginTransaction();
			OperationResult<T> operationResult = super.save(bo);
			if (operationResult.getError() != null) {
				if (mine == true) {
					try {
						this.rollbackTransaction();
					} catch (Exception e1) {
						// 回滚失败时，将回滚异常附加到原始异常上，避免丢失真正的故障原因
						operationResult.getError().addSuppressed(e1);
					}
				}
				return operationResult;
			}
			// 自建事务，提交；并重新获取对象实例
			if (mine == true) {
				this.commitTransaction();
				mine = false;
				// 是否需要获取新实例
				if (!this.isSkipInstanceFetch()) {
					// 获取新实例（存储过程影响后的数据）
					return super.fetch(bo.getClass(), bo.getCriteria());
				}
			}
			// 非自建事务，不获取新对象实例
			return operationResult;
		} catch (Exception e) {
			if (mine == true) {
				try {
					this.rollbackTransaction();
				} catch (Exception e1) {
					// 回滚失败时，将回滚异常附加到原始异常上，避免丢失真正的故障原因
					e.addSuppressed(e1);
				}
			}
			return new OperationResult<>(e);
		}
	}
}
