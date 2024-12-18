package org.colorcoding.ibas.bobas.repository;

import java.util.Objects;
import java.util.UUID;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
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
		try {
			Objects.requireNonNull(criteria);
			Objects.requireNonNull(boType);
			return (T[]) BOUtilities.fetch(criteria, this.cacheDatas.where(c -> boType.isInstance(c))).toArray();
		} catch (JudmentOperationException e) {
			throw new RepositoryException(e);
		}
	}
}
