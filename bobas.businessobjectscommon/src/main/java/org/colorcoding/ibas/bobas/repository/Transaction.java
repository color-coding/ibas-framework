package org.colorcoding.ibas.bobas.repository;

import java.util.UUID;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 事务基类，提供标识和缓存功能
 */
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
	 * @param data 待缓存数据，不可为null
	 * @return true缓存成功（新增），false已存在
	 */
	public abstract boolean cache(Object data);

	/**
	 * 从缓存中查询数据
	 *
	 * @param boType   数据类型
	 * @param criteria 查询条件
	 * @return 匹配的数据数组，可能为空
	 * @throws RepositoryException 查询失败
	 */
	public abstract <T> T[] fetchInCache(Class<?> boType, ICriteria criteria) throws RepositoryException;
}