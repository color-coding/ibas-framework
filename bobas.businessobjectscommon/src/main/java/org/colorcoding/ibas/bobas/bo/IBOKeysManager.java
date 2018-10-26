package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectsBase;
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
	 * @param bo 业务对象
	 * @return 主键值
	 * @throws Exception
	 */
	KeyValue[] usePrimaryKeys(IBusinessObjectBase bo) throws Exception;

	/**
	 * 使用主键（包含更新）
	 * 
	 * @param bos 业务对象集合
	 * @return 主键值
	 * @throws Exception
	 */
	KeyValue[] usePrimaryKeys(IBusinessObjectBase[] bos) throws Exception;

	/**
	 * 使用主键（包含更新）
	 * 
	 * @param bos 业务对象集合
	 * @return 主键值
	 * @throws Exception
	 */
	KeyValue[] usePrimaryKeys(IBusinessObjectsBase<?> bos) throws Exception;

	/**
	 * 设置主键值
	 * 
	 * @param bo   业务对象
	 * @param keys 主键值
	 */
	void applyPrimaryKeys(IBusinessObjectBase bo, KeyValue[] keys);

	/**
	 * 使用系列编号
	 * 
	 * @param bo 业务对象
	 * @return 系列编号
	 * @throws Exception
	 */
	KeyValue useSeriesKey(IBusinessObjectBase bo) throws Exception;

	/**
	 * 使用系列编号
	 * 
	 * @param bos 业务对象集合
	 * @return 系列编号
	 * @throws Exception
	 */
	KeyValue useSeriesKey(IBusinessObjectBase[] bos) throws Exception;

	/**
	 * 使用系列编号
	 * 
	 * @param bos 业务对象集合
	 * @return 系列编号
	 * @throws Exception
	 */
	KeyValue useSeriesKey(IBusinessObjectsBase<?> bos) throws Exception;

	/**
	 * 设置主键值
	 * 
	 * @param bo  业务对象
	 * @param key 系列编号
	 */
	void applySeriesKey(IBusinessObjectBase bo, KeyValue key);
}
