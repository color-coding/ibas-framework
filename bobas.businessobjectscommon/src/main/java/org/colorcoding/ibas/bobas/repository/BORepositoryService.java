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
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
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
	 * @return
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
	 * @return
	 */
	@Override
	protected <T extends IBusinessObject> OperationResult<T> fetch(Class<?> boType, ICriteria criteria) {
		if (criteria != null) {
			// 有子项的查询结果后，再筛选父项
			IChildCriteria cCriteria = criteria.getChildCriterias().firstOrDefault(c -> c.isEntry());
			if (cCriteria != null && !Strings.isNullOrEmpty(cCriteria.getPropertyPath())) {
				try {
					IPropertyInfo<?> propertyInfo = BOFactory.propertyInfos(boType)
							.firstOrDefault(c -> c.getName().equalsIgnoreCase(cCriteria.getPropertyPath()));
					if (propertyInfo == null) {
						throw new Exception(I18N.prop("msg_bobas_not_found_bo_property", cCriteria.getPropertyPath()));
					}
					if (propertyInfo.getValueType() == null
							|| !IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
						throw new Exception(I18N.prop("msg_bobas_invalid_argument", propertyInfo.getName()));
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
						throw new Exception(I18N.prop("msg_bobas_invalid_argument", propertyInfo.getName()));
					}
					// 新建主项查询，不再查子项
					ICriteria nCriteria = criteria.clone();
					nCriteria.setNoChilds(true);
					nCriteria.getChildCriterias().clear();
					ICondition condition = null;
					int count = criteria.getConditions().size();

					IOperationResult<IBusinessObject> opRsltChilds = this.fetch(subType, cCriteria);
					if (opRsltChilds.getError() != null) {
						throw opRsltChilds.getError();
					}
					for (IBusinessObject item : opRsltChilds.getResultObjects()) {
						if (item instanceof IBODocumentLine) {
							condition = nCriteria.getConditions().create();
							condition.setAlias(IBODocument.MASTER_PRIMARY_KEY_NAME);
							condition.setValue(((IBODocumentLine) item).getDocEntry());
						} else if (item instanceof IBOSimpleLine) {
							condition = nCriteria.getConditions().create();
							condition.setAlias(IBOSimple.MASTER_PRIMARY_KEY_NAME);
							condition.setValue(((IBOSimpleLine) item).getObjectKey());
						} else if (item instanceof IBOMasterDataLine) {
							condition = nCriteria.getConditions().create();
							condition.setAlias(IBOMasterData.MASTER_PRIMARY_KEY_NAME);
							condition.setValue(((IBOMasterDataLine) item).getCode());
						} else {
							int index = nCriteria.getConditions().size();
							// 设置父项主键
							IPropertyInfo<?> cKey = null;
							List<IPropertyInfo<?>> cKeys = BOFactory.propertyInfos(subType)
									.where(c -> c.isPrimaryKey());
							for (IPropertyInfo<?> pItem : BOFactory.propertyInfos(boType)
									.where(c -> c.isPrimaryKey())) {
								cKey = cKeys.firstOrDefault(c -> Strings.equals(pItem.getName(), c.getName()));
								if (cKey == null) {
									throw new Exception(I18N.prop("msg_bobas_invalid_argument", pItem.getName()));
								}
								condition = nCriteria.getConditions().create();
								condition.setAlias(pItem);
								condition.setValue(BOUtilities.propertyValue(item, cKey));
							}
							if (nCriteria.getConditions().size() > (index + 2)) {
								condition = nCriteria.getConditions().get(index);
								condition.setBracketOpen(condition.getBracketOpen() + 1);
								condition = nCriteria.getConditions().lastOrDefault();
								condition.setBracketClose(condition.getBracketClose() + 1);
							}
							// 置为添加的首个
							condition = nCriteria.getConditions().get(index);
						}
						if (nCriteria.getConditions().size() > (count + 1)) {
							condition.setRelationship(ConditionRelationship.OR);
						}
					}
					if (nCriteria.getConditions().size() > (count + 2)) {
						condition = nCriteria.getConditions().get(count);
						condition.setBracketOpen(condition.getBracketOpen() + 1);
						condition = nCriteria.getConditions().lastOrDefault();
						condition.setBracketClose(condition.getBracketClose() + 1);
					}
					// 查询父项，并填充子项
					OperationResult<T> opRsltParent = this.fetch(boType, nCriteria);
					if (opRsltParent.getError() != null) {
						throw opRsltParent.getError();
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
				} catch (Exception e) {
					return new OperationResult<>(e);
				}
			}
		}
		// 父项查询，再查子项
		return super.fetch(boType, criteria);
	}

	/**
	 * 保存数据
	 * 
	 * 自建事务，重新获取对象实例
	 * 
	 * @param <T> 对象类型
	 * @param bo  待保存对象
	 * @return
	 */
	@Override
	protected <T extends IBusinessObject> OperationResult<T> save(T bo) {
		try {
			boolean mine = this.beginTransaction();
			try {
				OperationResult<T> operationResult = super.save(bo);
				if (operationResult.getError() != null) {
					throw operationResult.getError();
				}
				// 自建事务，提交；并重新获取对象实例
				if (mine == true) {
					this.commitTransaction();
					mine = false;
					// 是否需要获取新实例
					if (!this.isSkipInstanceFetch()) {
						// 获取新实例（存储过程影响后的数据）
						return this.fetch(bo.getClass(), bo.getCriteria());
					}
				}
				// 非自建事务，不获取新对象实例
				return operationResult;
			} catch (Exception e) {
				if (mine == true) {
					this.rollbackTransaction();
				}
				throw e;
			}
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}
}
