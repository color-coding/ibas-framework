package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;

/**
 * 审批步骤
 * 
 * @author Niuren.Zhu
 *
 */
public interface IProcessStepData extends IBusinessObject {

	/**
	 * 获取-编号
	 * 
	 * @return
	 */
	int getId();

	/**
	 * 获取-开始时间
	 * 
	 * @return
	 */
	DateTime getStartedTime();

	/**
	 * 设置-开始时间
	 * 
	 * @param value
	 */
	void setStartedTime(DateTime value);

	/**
	 * 获取-结束时间
	 * 
	 * @return
	 */
	DateTime getFinishedTime();

	/**
	 * 设置-结束时间
	 * 
	 * @param value
	 */
	void setFinishedTime(DateTime value);

	/**
	 * 获取-审批意见
	 * 
	 * @return
	 */
	String getJudgment();

	/**
	 * 设置-审批意见
	 * 
	 * @param value
	 */
	void setJudgment(String value);

	/**
	 * 获取-审批状态
	 * 
	 * @return
	 */
	emApprovalStepStatus getStatus();

	/**
	 * 设置-审批状态
	 * 
	 * @param value
	 */
	void setStatus(emApprovalStepStatus value);
}
