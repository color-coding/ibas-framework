package org.colorcoding.ibas.bobas.repository;

import java.util.UUID;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 事务基类，提供标识和缓存功能
 */
public abstract class Transaction implements ITransaction {

	public Transaction() {
		this.setId(newTransactionId());
	}

	/**
	 * 生成时间有序的事务标识（UUID v7 风格）
	 *
	 * 前48位为毫秒时间戳，使标识按时间递增，优化数据库主键索引性能；
	 * 后续位为随机数，保证同一毫秒内的唯一性。
	 *
	 * @return 事务标识
	 */
	protected static String newTransactionId() {
		long timestamp = System.currentTimeMillis();
		UUID random = UUID.randomUUID();
		// MSB: 48位时间戳 | 4位版本号(7) | 12位随机
		long mostSigBits = (timestamp << 16) | 0x7000 | (random.getMostSignificantBits() & 0x0FFF);
		// LSB: 2位变体标记(10) | 62位随机
		long leastSigBits = (random.getLeastSignificantBits() & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
		return new UUID(mostSigBits, leastSigBits).toString();
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

	/**
	 * 清理缓存数据，释放被引用的对象
	 */
	public void clearCache() {
	}
}