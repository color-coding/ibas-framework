package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务对象仓库-缓存
 */
public interface IBORepository4Cache extends IBORepositoryReadonly {
	/**
	 * 缓存业务对象
	 * 
	 * @param bo
	 *            业务对象
	 */
	void cacheData(IBusinessObjectBase bo);

	/**
	 * 缓存结果集
	 * 
	 * @param bos
	 *            业务对象集合
	 */
	void cacheData(Iterable<? extends IBusinessObjectBase> bos);

	/**
	 * 清除业务对象
	 * 
	 * @param bo
	 *            业务对象
	 */
	void clearData(IBusinessObjectBase bo);
}
