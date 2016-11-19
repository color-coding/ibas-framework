package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务对象批量保存的数据库仓库
 * 
 * @author niuren.zhu
 *
 */
public interface IBORepository4DbBatch extends IBORepository4Db {
	/**
	 * 保存对象
	 * 
	 * @param bo
	 *            被保存的对象数组
	 * 
	 * @return 操作结果及对象实例
	 */
	IOperationResult<?> save(IBusinessObjectBase[] bos);

	/**
	 * 保存对象包括子项
	 * 
	 * @param bos
	 *            被保存的对象数组
	 * 
	 * @return 操作结果及对象实例
	 */
	IOperationResult<?> saveEx(IBusinessObjectBase[] bos);
}
