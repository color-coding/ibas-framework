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
	 * @return 已初始化返回true
	 */
	boolean isInitialized();

	/**
	 * 注册规则
	 *
	 * @param rules 要注册的业务规则数组
	 */
	void register(IBusinessRule[] rules);

	/**
	 * 已注册规则数量
	 *
	 * @return 规则数量
	 */
	int size();

	/**
	 * 运行业务规则
	 *
	 * @param bo         执行规则的业务对象
	 * @param properties 变化的属性；为空或null时执行全部规则
	 */
	void execute(IBusinessObject bo, IPropertyInfo<?>... properties) throws BusinessRuleException;

}