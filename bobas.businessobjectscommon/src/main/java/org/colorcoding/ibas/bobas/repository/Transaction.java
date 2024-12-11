package org.colorcoding.ibas.bobas.repository;

import java.lang.reflect.Method;
import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.expression.BOJudgmentLinkCondition;
import org.colorcoding.ibas.bobas.expression.JudgmentLink;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.bobas.logging.Logger;

public abstract class Transaction implements ITransaction {

	private volatile ArrayList<IBusinessObject> cacheDatas = new ArrayList<>();

	/**
	 * 缓存数据
	 * 
	 * @param data 待缓存数据
	 * @return true:缓存成功（新）; false:已缓存
	 */
	public synchronized boolean cache(IBusinessObject data) {
		Objects.requireNonNull(data);
		if (this.cacheDatas.contains(data)) {
			return false;
		}
		this.cacheDatas.add(data);
		return true;
	}

	/**
	 * 缓存中查询数据
	 * 
	 * @param <T>      数据类型
	 * @param criteria 查询条件
	 * @param boType   类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends IBusinessObject> T[] fetchInCache(ICriteria criteria, Class<T> boType) {
		Objects.requireNonNull(criteria);
		Objects.requireNonNull(boType);

		BOJudgmentLinkCondition judgmentLink = new BOJudgmentLinkCondition();
		judgmentLink.parsingConditions(criteria.getConditions());
		ArrayList<T> datas = new ArrayList<>();
		for (IBusinessObject item : this.cacheDatas) {
			if (item == null) {
				continue;
			}
			if (!boType.isInstance(item)) {
				continue;
			}
			if (this.judge(judgmentLink, datas, criteria)) {
				datas.add((T) item);
			}
		}
		return (T[]) datas.toArray(new Object[] {});
	}

	private boolean judge(JudgmentLink judgmentLink, Object data, ICriteria criteria) {
		try {
			if (judgmentLink.judge(data)) {
				boolean pass = true;
				if (!criteria.getChildCriterias().isEmpty()) {
					// 存在子项查询
					for (IChildCriteria child : criteria.getChildCriterias()) {
						try {
							Method method = data.getClass().getMethod("get" + child.getPropertyPath());
							if (method == null) {
								// 对象没有带比较的属性
								pass = false;
							}
							Object pData = method.invoke(data);
							if (pData == null) {
								// 属性没有值，不能进行比较
								pass = false;
							}
							if (pData instanceof Iterable) {
								for (Object pDataItem : (Iterable<?>) pData) {
									pass = this.judge(judgmentLink, pDataItem, child);
									if (!pass) {
										// 比较不通过，后续不在处理
										break;
									}
								}
							} else {
								pass = this.judge(judgmentLink, pData, child);
							}
							if (!pass) {
								// 比较不通过，后续不在处理
								break;
							}
						} catch (Exception e) {
							Logger.log(e);
						}
					}
					return pass;
				}
				return pass;
			}
		} catch (JudmentOperationException e) {
			Logger.log(e);
		}
		return false;
	}
}
