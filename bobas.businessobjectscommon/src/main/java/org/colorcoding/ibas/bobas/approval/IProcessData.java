package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 审批流程（数据）
 * 
 * @author Niuren.Zhu
 *
 */
public interface IProcessData extends IBusinessObject {

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
	 * 设置-状态
	 * 
	 * @param value
	 */
	void setStatus(emApprovalStatus value);

	/**
	 * 获取-激活的
	 * 
	 * @return 值
	 */
	emYesNo getActivated();

	/**
	 * 设置-激活的
	 * 
	 * @param value 值
	 */
	void setActivated(emYesNo value);

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
