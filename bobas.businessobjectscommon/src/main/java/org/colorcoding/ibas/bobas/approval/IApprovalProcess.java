package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 审批流程
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcess {

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
	 * 获取流程所有者
	 * 
	 * @return
	 */
	IUser getOwner();

	/**
	 * 获取-流程步骤
	 * 
	 * @return
	 */
	IApprovalProcessStep[] getProcessSteps();

	/**
	 * 获取-当前步骤
	 * 
	 * @return
	 */
	IApprovalProcessStep currentStep();

	/**
	 * 下一个步骤
	 * 
	 */
	void nextStep();

	/**
	 * 开始流程
	 * 
	 * @param data
	 *            数据
	 * @return 是否成功开始流程
	 */
	boolean start(IApprovalData data);

	/**
	 * 保存审批流程
	 * 
	 * @param boRepository
	 *            保存到的业务仓库
	 */
	void save(IBORepository boRepository);
}
