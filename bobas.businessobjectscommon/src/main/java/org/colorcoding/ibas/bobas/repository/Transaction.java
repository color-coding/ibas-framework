package org.colorcoding.ibas.bobas.repository;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;

public abstract class Transaction implements ITransaction {

	public Transaction() {
		this.setId(UUID.randomUUID().toString());
	}

	private String id;

	public final String getId() {
		return id;
	}

	private final void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return Strings.format("{trans: %s...}", Strings.substring(this.getId(), 8));
	}

	private volatile HashSet<IBusinessObject> cacheDatas = new HashSet<>();

	/**
	 * 缓存数据
	 * 
	 * @param data 待缓存数据
	 * @return true:缓存成功（新）; false:已缓存
	 */
	public synchronized boolean cache(IBusinessObject data) {
		Objects.requireNonNull(data);
		return this.cacheDatas.add(data);
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
	 * @param boType   类型
	 * @param criteria 查询条件
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends IBusinessObject> T[] fetchInCache(Class<?> boType, ICriteria criteria)
			throws RepositoryException {
		try {
			Objects.requireNonNull(criteria);
			Objects.requireNonNull(boType);
			// 缓存数据迭代器（不一次取出）
			Iterator<?> iterator = new Iterator<Object>() {
				// 数据迭代器
				Iterator<?> current = Transaction.this.cacheDatas != null ? Transaction.this.cacheDatas.iterator()
						: null;

				@Override
				public boolean hasNext() {
					if (this.current != null && this.current.hasNext()) {
						return true;
					}
					return false;
				}

				@Override
				public Object next() {
					if (this.current != null && this.current.hasNext()) {
						Object data = this.current.next();
						if (boType.isInstance(data)) {
							return data;
						} else {
							return this.next();
						}
					}
					return null;
				}
			};
			return BOUtilities.fetch(iterator, criteria).toArray((T[]) Array.newInstance(boType, 0));
		} catch (JudmentOperationException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.cacheDatas = null;
		super.finalize();
	}
}
