package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.KeyValue;

/**
 * 业务对象编码管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBONumberingManager {
	/**
	 * 解析主键
	 * 
	 * @param bo
	 *            业务对象
	 * @param others
	 *            参数
	 * @return 主键值
	 * @throws BOException
	 */
	KeyValue[] parsePrimaryKeys(IBusinessObjectBase bo, Object... others) throws BOException;

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
	 * 更新主键记录，步长为1
	 * 
	 * @param bo
	 *            业务对象
	 * @param others
	 *            参数
	 * @throws BOException
	 */
	void updatePrimaryKeyRecords(IBusinessObjectBase bo, Object... others) throws BOException;

	/**
	 * 更新主键记录
	 * 
	 * @param bo
	 *            业务对象
	 * @param addValue
	 *            增加值
	 * @param others
	 *            其他参数
	 * @throws BOException
	 */
	void updatePrimaryKeyRecords(IBusinessObjectBase bo, int addValue, Object... others) throws BOException;

	/**
	 * 设置主键值
	 * 
	 * @param bo
	 *            业务对象
	 * @param keys
	 *            主键值
	 */
	void setPrimaryKeys(IBusinessObjectBase bo, KeyValue[] keys);
}
