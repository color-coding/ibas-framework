package org.colorcoding.ibas.bobas.repository;

import java.util.UUID;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;

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

	/**
	 * 缓存数据
	 * 
	 * @param data 待缓存数据
	 * @return true:缓存成功（新）; false:已缓存
	 */
	public abstract boolean cache(Object data);

	/**
	 * 缓存中查询数据
	 * 
	 * @param <T>      数据类型
	 * @param boType   类型
	 * @param criteria 查询条件
	 * @return
	 */
	public abstract <T> T[] fetchInCache(Class<?> boType, ICriteria criteria) throws RepositoryException;
}
