package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;

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

	/**
	 * 批准
	 * 
	 * @param judgment 意见
	 * @throws UnlogicalException
	 */
	void approve(String judgment) throws UnlogicalException;

	/**
	 * 拒绝
	 * 
	 * @param judgment 意见
	 * @throws UnlogicalException
	 */
	void reject(String judgment) throws UnlogicalException;

	/**
	 * 退回
	 * 
	 * @param judgment 意见
	 * @throws UnlogicalException
	 */
	void retreat(String judgment) throws UnlogicalException;

	/**
	 * 重置为进行中
	 * 
	 * @throws UnlogicalException
	 */
	void reset() throws UnlogicalException;

	/**
	 * 跳过
	 * 
	 * @throws UnlogicalException
	 */
	void skip() throws UnlogicalException;
}
