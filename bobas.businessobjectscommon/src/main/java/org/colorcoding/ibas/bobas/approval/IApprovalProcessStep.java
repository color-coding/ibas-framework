package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 审批步骤
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcessStep {

	/**
	 * 获取-编号
	 * 
	 * @return
	 */
	int getId();

	/**
	 * 获取-条件
	 * 
	 * @return
	 */
	IApprovalProcessStepCondition[] getConditions();

	/**
	 * 获取-开始时间
	 * 
	 * @return
	 */
	DateTime getStartedTime();

	/**
	 * 获取-结束时间
	 * 
	 * @return
	 */
	DateTime getFinishedTime();

	/**
	 * 获取步骤所有者
	 * 
	 * @return
	 */
	IUser getOwner();

	/**
	 * 获取-审批意见
	 * 
	 * @return
	 */
	String getJudgment();

	/**
	 * 获取-审批状态
	 * 
	 * @return
	 */
	emApprovalStepStatus getStatus();

}
