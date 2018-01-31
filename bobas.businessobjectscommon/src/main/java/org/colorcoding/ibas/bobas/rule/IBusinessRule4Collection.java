package org.colorcoding.ibas.bobas.rule;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;

/**
 * 业务规则（集合元素）
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessRule4Collection extends IBusinessRule {

	/**
	 * 运行业务规则
	 * 
	 * @param parent
	 *            父项
	 * @param elements
	 *            元素
	 */
	void execute(IBusinessObject parent, IBusinessObject[] elements) throws BusinessRuleException;

}
