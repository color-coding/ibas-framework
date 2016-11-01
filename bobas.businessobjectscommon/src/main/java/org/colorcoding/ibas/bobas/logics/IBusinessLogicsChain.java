package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicsChain {
	/**
	 * 设置-标记
	 * 
	 * @param value
	 */
	void setId(String value);

	/**
	 * 获取-标记
	 * 
	 * @param value
	 */
	String getId();

	/**
	 * 获取-触发逻辑链对象
	 * 
	 * @return
	 */
	IBusinessObjectBase getTrigger();

	/**
	 * 设置-触发逻辑链对象
	 * 
	 * @param bo
	 */
	void setTrigger(IBusinessObjectBase bo);

	/**
	 * 改变业务对象仓库
	 * 
	 * @param boRepository
	 *            仓库
	 * @return 成功，true；失败，false
	 */
	void useRepository(IBORepository boRepository);

	/**
	 * 正向执行逻辑
	 * 
	 * @param bo
	 *            契约数据
	 */
	void forwardLogics(IBusinessObjectBase bo);

	/**
	 * 反向执行逻辑
	 * 
	 * @param bo
	 *            契约数据
	 */
	void reverseLogics(IBusinessObjectBase bo);

	/**
	 * 提交逻辑
	 */
	void commit(IBusinessObjectBase bo);

	/**
	 * 检索被影响的数据
	 * 
	 * @param criteria
	 *            条件
	 * @param type
	 *            数据类型
	 * @return
	 */
	<B> B fetchBeAffected(ICriteria criteria, Class<B> type);

	/**
	 * 检索父项副本
	 * 
	 * @param criteria
	 *            条件
	 * @param type
	 *            数据类型
	 * @return
	 */
	<B> B fetchOldParent(ICriteria criteria, Class<B> type);
}
