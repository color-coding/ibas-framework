package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.KeyValue;

/**
 * 业务对象编码管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOKeysManager {

	/**
	 * 使用主键（包含更新）
	 * 
	 * @param bo
	 *            业务对象
	 * @param others
	 *            参数
	 * @return 主键值
	 * @throws BOException
	 */
	KeyValue[] usePrimaryKeys(IBusinessObjectBase bo, Object... others) throws BOException;

	/**
	 * 使用主键（包含更新）
	 * 
	 * @param bos
	 *            业务对象集合
	 * @param others
	 *            参数
	 * @return 主键值
	 * @throws BOException
	 */
	KeyValue[] usePrimaryKeys(IBusinessObjectBase[] bos, Object... others) throws BOException;

	/**
	 * 设置主键值
	 * 
	 * @param bo
	 *            业务对象
	 * @param keys
	 *            主键值
	 */
	void applyPrimaryKeys(IBusinessObjectBase bo, KeyValue[] keys);

	/**
	 * 使用系列编号
	 * 
	 * @param bo
	 *            业务对象
	 * @param others
	 *            参数
	 * @return 系列编号
	 * @throws BOException
	 */
	KeyValue useSeriesKey(IBusinessObjectBase bo, Object... others) throws BOException;

	/**
	 * 使用系列编号
	 * 
	 * @param bos
	 *            业务对象集合
	 * @param others
	 *            参数
	 * @return 系列编号
	 * @throws BOException
	 */
	KeyValue useSeriesKey(IBusinessObjectBase[] bos, Object... others) throws BOException;

	/**
	 * 设置主键值
	 * 
	 * @param bo
	 *            业务对象
	 * @param key
	 *            系列编号
	 */
	void applySeriesKey(IBusinessObjectBase bo, KeyValue key);
}
