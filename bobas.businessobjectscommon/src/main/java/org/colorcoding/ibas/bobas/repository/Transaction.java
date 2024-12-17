package org.colorcoding.ibas.bobas.repository;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.expression.BOJudgmentLinkCondition;
import org.colorcoding.ibas.bobas.expression.JudgmentLink;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;

public abstract class Transaction implements ITransaction {

	public Transaction() {
		this.setId(UUID.randomUUID().toString());
	}

	private String id;

	public synchronized final String getId() {
		return id;
	}

	private synchronized final void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return Strings.format("{trans: %s...}", Strings.substring(this.getId(), 8));
	}

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
	 * 释放占用资源
	 * 
	 * @throws
	 */
	@Override
	public void close() throws Exception {
		this.cacheDatas.clear();
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
	public <T extends IBusinessObject> T[] fetchInCache(ICriteria criteria, Class<?> boType)
			throws RepositoryException {
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
			try {
				if (this.judge(judgmentLink, datas, criteria)) {
					datas.add((T) item);
				}
			} catch (Exception e) {
				throw new RepositoryException(e);
			}
		}
		return (T[]) datas.toArray();
	}

	private boolean judge(JudgmentLink judgmentLink, Object data, ICriteria criteria) throws Exception {
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
							throw e;
						}
					}
					return pass;
				}
				return pass;
			}
		} catch (JudmentOperationException e) {
			throw e;
		}
		return false;
	}
}
