package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.bo.BOException;
import org.colorcoding.ibas.bobas.bo.IBOKeysManager;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.KeyValue;

/**
 * 基于数据库的业务对象编号者
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOKeysManager4Db extends IBOKeysManager {

	/**
	 * 使用主键（查询并更新） 应考虑多线程的并发问题
	 * 
	 * @param bo
	 *            业务对象
	 * @param command
	 *            数据库命令
	 * @return 业务对象主键
	 * @throws BOException
	 */
	KeyValue[] usePrimaryKeys(IBusinessObjectBase bo, IDbCommand command) throws BOException;

	/**
	 * 使用主键（查询并更新） 应考虑多线程的并发问题
	 * 
	 * @param bo
	 *            业务对象
	 * @param command
	 *            数据库命令
	 * @return 业务对象主键
	 * @throws BOException
	 */
	KeyValue[] usePrimaryKeys(IBusinessObjectBase[] bos, IDbCommand command) throws BOException;

	/**
	 * 使用系列编号
	 * 
	 * @param bo
	 *            业务对象
	 * @param command
	 *            数据库命令
	 * @return 系列编号
	 * @throws BOException
	 */
	KeyValue useSeriesKey(IBusinessObjectBase bo, IDbCommand command) throws BOException;

	/**
	 * 使用系列编号
	 * 
	 * @param bos
	 *            业务对象集合
	 * @param command
	 *            数据库命令
	 * @return 系列编号
	 * @throws BOException
	 */
	KeyValue useSeriesKey(IBusinessObjectBase[] bos, IDbCommand command) throws BOException;

}
