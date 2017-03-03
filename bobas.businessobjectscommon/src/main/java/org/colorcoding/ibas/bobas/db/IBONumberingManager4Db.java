package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.bo.BOException;
import org.colorcoding.ibas.bobas.bo.IBONumberingManager;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.KeyValue;

/**
 * 基于数据库的业务对象编号者
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBONumberingManager4Db extends IBONumberingManager {

	/**
	 * 解析业务对象
	 * 
	 * @param bo
	 *            业务对象
	 * @param command
	 *            数据库命令
	 * 
	 * @return 主键及值
	 * @throws BOException
	 */
	KeyValue[] parsePrimaryKeys(IBusinessObjectBase bo, IDbCommand command) throws BOException;

	/**
	 * 更新业务对象主键记录，主键值加一。
	 * 
	 * @param bo
	 *            业务对象（取值对象）
	 * @param command
	 *            数据库命令
	 * 
	 * @throws BOException
	 */
	void updatePrimaryKeyRecords(IBusinessObjectBase bo, IDbCommand command) throws BOException;

	/**
	 * 更新业务对象主键记录
	 * 
	 * @param bo
	 *            业务对象（取值对象）
	 * @param addValue
	 *            主键增加值
	 * @param command
	 *            数据库命令
	 * 
	 * @throws BOException
	 */
	void updatePrimaryKeyRecords(IBusinessObjectBase bo, int addValue, IDbCommand command) throws BOException;

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

}
