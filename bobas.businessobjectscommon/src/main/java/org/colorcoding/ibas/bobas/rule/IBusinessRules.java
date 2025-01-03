package org.colorcoding.ibas.bobas.rule;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 业务规则集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessRules extends Iterable<IBusinessRule> {

	/**
	 * 是否已初始化
	 * 
	 * @return
	 */
	boolean isInitialized();

	/**
	 * 注册规则
	 * 
	 * @param rules
	 */
	void register(IBusinessRule[] rules);

	/**
	 * 大小
	 * 
	 * @return
	 */
	int size();

	/**
	 * 运行业务规则
	 * 
	 * @param bo
	 *            执行规则的业务对象
	 * @param properties
	 *            变化的属性
	 */
	void execute(IBusinessObject bo, IPropertyInfo<?>... properties) throws BusinessRuleException;

}
