package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;

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
	 * 是否新创建的审批流程
	 * 
	 * @return
	 */
	boolean isNew();

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
	 * 开始流程
	 * 
	 * @param data
	 *            数据
	 * @return 是否成功开始流程
	 */
	boolean start(IApprovalData data);

	/**
	 * 批准流程
	 * 
	 * @param stepId
	 *            步骤ID
	 * @param apResult
	 *            结果
	 * @param authorizationCode
	 *            授权码
	 * @param judgment
	 *            意见
	 * @throws InvalidAuthorizationException
	 * @throws ApprovalException
	 * @throws RepositoryException
	 */
	void approval(int stepId, emApprovalResult apResult, String authorizationCode, String judgment)
			throws InvalidAuthorizationException, ApprovalException, RepositoryException;

	/**
	 * 取消流程
	 * 
	 * @param authorizationCode
	 *            授权码
	 * @param remarks
	 *            备注
	 * @return true，执行取消；false，没有执行取消
	 * @throws ApprovalProcessException
	 * @throws InvalidAuthorizationException
	 */
	boolean cancel(String authorizationCode, String remarks)
			throws ApprovalProcessException, InvalidAuthorizationException;

	/**
	 * 设置业务仓库
	 * 
	 * @param boRepository
	 * @throws ApprovalProcessException
	 */
	void setRepository(IBORepository boRepository);

	/**
	 * 保存审批流程
	 * 
	 * @param boRepository
	 *            保存到的业务仓库
	 * @throws ApprovalProcessException
	 */
	void save() throws ApprovalProcessException;

	/**
	 * 检查保存权限
	 * 
	 * @param user
	 *            操作用户
	 * @throws ApprovalProcessException
	 */
	void checkToSave(IUser user) throws ApprovalProcessException;

}
