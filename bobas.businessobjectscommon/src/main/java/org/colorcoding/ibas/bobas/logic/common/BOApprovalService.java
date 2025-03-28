package org.colorcoding.ibas.bobas.logic.common;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.approval.ApprovalFactory;
import org.colorcoding.ibas.bobas.approval.ApprovalProcess;
import org.colorcoding.ibas.bobas.approval.ApprovalProcessManager;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IProcessData;
import org.colorcoding.ibas.bobas.core.ITrackable;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;

@LogicContract(IBOApprovalContract.class)
public class BOApprovalService extends BusinessLogic<IBOApprovalContract, IProcessData> {

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data == this.getHost()) {
			if (data instanceof IApprovalData) {
				IApprovalData apData = (IApprovalData) data;
				if (!apData.isSavable()) {
					Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
							"isSavable", "false");
					return false;
				}
				if (!apData.isDirty()) {
					Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
							"isDirty", "false");
					return false;
				}
				return super.checkDataStatus(apData);
			} else {
				Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"isApprovalData", "false");
				return false;
			}
		}
		return true;
	}

	private ApprovalProcessManager processManager;

	protected final ApprovalProcessManager getProcessManager() {
		if (this.processManager == null) {
			this.processManager = ApprovalFactory.createManager(this.getTransaction());
		}
		return processManager;
	}

	@Override
	protected IProcessData fetchBeAffected(IBOApprovalContract contract) {
		IProcessData processData = null;
		// 非新建则尝试加载
		if (!contract.getHost().isNew()) {
			processData = this.getProcessManager().loadProcessData(contract.getHost());
		}
		// 没有已存在的流程数据，则根据尝试创建
		if (processData == null) {
			ApprovalProcess<?> process = null;
			Iterator<ApprovalProcess<IProcessData>> processes = this.getProcessManager()
					.createApprovalProcess(contract.getHost().getObjectCode());
			while (processes != null && processes.hasNext()) {
				process = processes.next();
				if (process.start(contract.getHost())) {
					// 审批流程开始
					Logger.log("approval process: data [%s]'s approval process was started, name [%s].",
							contract.getHost().getIdentifiers(), process.getName());
					processData = process.getProcessData();
					break;
				}
			}
		}
		// 未能形成审批流程
		if (processData == null) {
			// 重置数据状态
			if (contract.getHost().getApprovalStatus() != emApprovalStatus.UNAFFECTED) {
				contract.getHost().setApprovalStatus(emApprovalStatus.UNAFFECTED);
			}
		}
		return processData;
	}

	@Override
	protected void impact(IBOApprovalContract contract) {
		try {
			ApprovalProcess<?> process = this.getProcessManager().createApprovalProcess(this.getBeAffected());
			// 检查用户是否可以修改数据
			process.checkToSave(this.getUser());
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	@Override
	protected void revoke(IBOApprovalContract contract) {
		if (contract.getHost() instanceof ITrackable) {
			ITrackable trackable = (ITrackable) contract.getHost();
			// 检查用户是否可以删除
			if (trackable.isDeleted()) {
				ApprovalProcess<?> process = this.getProcessManager().createApprovalProcess(this.getBeAffected());
				try {
					process.checkToSave(this.getUser());
				} catch (Exception e) {
					throw new BusinessLogicException(e);
				}
			}
		}
	}

}
