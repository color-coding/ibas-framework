package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务逻辑
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogic<B extends IBusinessObjectBase> {

	/**
	 * 设置-契约
	 * 
	 * @param contract
	 */
	void setContract(IBusinessLogicContract contract);

	/**
	 * 设置-仓库
	 * 
	 * @param boRepository
	 */
	void setRepository(IBORepository repository);

	/**
	 * 正向执行逻辑
	 * 
	 */
	void forward();

	/**
	 * 反向执行逻辑
	 * 
	 */
	void reverse();

	/**
	 * 被影响的对象
	 * 
	 * @return
	 */
	B getBeAffected();

	/**
	 * 是否完成
	 * 
	 * @return
	 */
	boolean isDone();

	/**
	 * 提交逻辑
	 */
	void commit();
}
