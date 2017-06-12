package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;

/**
 * 审批步骤的条件
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcessStepCondition {
	/**
	 * 属性取值方式
	 * 
	 * @return
	 */
	ValueMode getPropertyValueMode();

	/**
	 * 获取-属性名称
	 * 
	 * @return
	 */
	String getPropertyName();

	/**
	 * 获取-条件操作
	 * 
	 * @return
	 */
	emConditionOperation getOperation();

	/**
	 * 获取-条件关系
	 * 
	 * @return
	 */
	emConditionRelationship getRelation();

	/**
	 * 属性取值方式
	 * 
	 * @return
	 */
	ValueMode getConditionValueMode();

	/**
	 * 获取-条件值
	 * 
	 * @return
	 */
	String getConditionValue();

}
