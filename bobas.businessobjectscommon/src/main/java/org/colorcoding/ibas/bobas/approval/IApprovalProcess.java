package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;

/**
 * 审批流程（数据）
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcess extends IBusinessObject {

	/**
	 * 获取-名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取-状态
	 * 
	 * @return
	 */
	emApprovalStatus getStatus();

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
	 * 获取审批数据
	 * 
	 * @return
	 */
	IApprovalData getApprovalData();

	/**
	 * 设置审批数据
	 * 
	 * @param value
	 */
	void setApprovalData(IApprovalData value);

}
