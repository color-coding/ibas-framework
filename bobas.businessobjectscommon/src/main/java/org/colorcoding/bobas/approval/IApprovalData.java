package org.colorcoding.bobas.approval;

import org.colorcoding.bobas.common.ICriteria;
import org.colorcoding.bobas.core.ITrackStatus;
import org.colorcoding.bobas.data.emApprovalStatus;

/**
 * 审批的数据
 */
public interface IApprovalData extends ITrackStatus {
	/**
	 * 获取-数据类型
	 * 
	 * @return
	 */
	String getObjectCode();

	/**
	 * 获取-数据所有人
	 * 
	 * @return
	 */
	int getDataOwner();

	/**
	 * 获取-审批状态
	 * 
	 * @return
	 */
	emApprovalStatus getApprovalStatus();

	/**
	 * 获取数据查询条件
	 * 
	 * @return
	 */
	ICriteria getCriteria();

}
