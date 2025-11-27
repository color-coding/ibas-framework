package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.approval.ApprovalFactory;
import org.colorcoding.ibas.bobas.approval.ApprovalProcess;
import org.colorcoding.ibas.bobas.approval.ApprovalProcessManager;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IProcessData;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.core.ITrackable;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

@LogicContract(IBOApprovalContract.class)
public class BOApprovalService extends BusinessLogic<IBOApprovalContract, IProcessData> {

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data == this.getHost()) {
			if (data instanceof IApprovalData) {
				IApprovalData apData = (IApprovalData) data;
				if (!apData.isSavable()) {
					Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
							"isSavable", "false");
					return false;
				}
				// 以下来着基类，跳过审批对象
				if (data instanceof ITrackable) {
					// 标记删除的数据无效
					ITrackable status = (ITrackable) data;
					if (status.isDeleted()) {
						Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
								"isDeleted", status.isDeleted());
						return false;
					}
				}
				if (data instanceof IBOTagDeleted) {
					// 引用数据，已标记删除的，不影响业务逻辑
					IBOTagDeleted refData = (IBOTagDeleted) data;
					if (refData.getDeleted() == emYesNo.YES) {
						Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
								"Deleted", refData.getDeleted());
						return false;
					}
				}
				if (data instanceof IBOTagCanceled) {
					// 引用数据，已标记取消的，不影响业务逻辑
					IBOTagCanceled refData = (IBOTagCanceled) data;
					if (refData.getCanceled() == emYesNo.YES) {
						Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
								"Canceled", refData.getCanceled());
						return false;
					}
				}
				if (data instanceof IBODocument) {
					// 单据类型
					IBODocument docData = (IBODocument) data;
					if (docData.getDocumentStatus() == emDocumentStatus.PLANNED) {
						// 计划状态
						Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
								"DocumentStatus", docData.getDocumentStatus());
						return false;
					}
				}
				if (data instanceof IBODocumentLine) {
					// 单据行
					IBODocumentLine lineData = (IBODocumentLine) data;
					if (lineData.getLineStatus() == emDocumentStatus.PLANNED) {
						// 计划状态
						Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
								"LineStatus", lineData.getLineStatus());
						return false;
					}
				}
				return true;
			} else {
				Logger.log(MessageLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
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
		try {
			IProcessData processData = null;
			// 非新建，则尝试加载存在请求
			if (!contract.getHost().isNew()) {
				processData = this.getProcessManager().loadProcessData(contract.getHost());
			}
			// 没有已存在的流程数据，则根据尝试创建
			if (processData == null) {
				ApprovalProcess<IProcessData> process = this.getProcessManager().startProcess(contract.getHost());
				if (process != null) {
					processData = process.getProcessData();
					if (!Enums.equals(contract.getHost().getApprovalStatus(), processData.getStatus())) {
						contract.getHost().setApprovalStatus(processData.getStatus());
					}
				}
			}
			return processData;
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	@Override
	protected void impact(IBOApprovalContract contract) {
		if (this.getBeAffected() == null) {
			// 未能形成审批流程，重置数据状态
			if (contract.getHost().getApprovalStatus() != emApprovalStatus.UNAFFECTED) {
				contract.getHost().setApprovalStatus(emApprovalStatus.UNAFFECTED);
			}
			return;
		}
		try {
			ApprovalProcess<IProcessData> process = null;
			if (!Enums.equals(this.getBeAffected().getStatus(), contract.getHost().getApprovalStatus())) {
				// 请求状态与数据状态，不一致，则重开始流程
				process = this.getProcessManager().startProcess(this.getBeAffected());
				if (process != null) {
					process.start(contract.getHost());
				}
			} else {
				process = this.getProcessManager().startProcess(this.getBeAffected(), contract.getHost());
				if (process != null) {
					// 检查用户是否可以修改数据
					process.checkToSave(this.getUser());
				}
			}
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	@Override
	protected void revoke(IBOApprovalContract contract) {
		if (this.getBeAffected() == null || this.getBeAffected().isNew()) {
			// 反向逻辑新键的审批流程，视为无效
			return;
		}
		// 反向逻辑退回过的，不检查审批
		if (Enums.equals(contract.getHost().getApprovalStatus(), emApprovalStatus.RETURNED)) {
			return;
		}
		try {
			ApprovalProcess<IProcessData> process = null;
			// 检查用户删除操作
			if (this.getTrigger() instanceof ITrackable) {
				ITrackable trackable = (ITrackable) this.getTrigger();
				if (trackable.isDeleted()) {
					if (process == null) {
						process = this.getProcessManager().startProcess(this.getBeAffected(), contract.getHost());
					}
					if (process != null) {
						process.checkToSave(this.getUser());
						if (process.cancel(this.getUser().getToken(),
								I18N.prop("msg_bobas_user_deleted_approval_data"))) {
							return;
						}
					}
				}
			}
			// 检查用户标记删除操作
			if (this.getTrigger() instanceof IBOTagDeleted) {
				IBOTagDeleted tagDeleted = (IBOTagDeleted) this.getTrigger();
				if (tagDeleted.getDeleted() == emYesNo.YES) {
					if (process == null) {
						process = this.getProcessManager().startProcess(this.getBeAffected(), contract.getHost());
					}
					if (process != null) {
						process.checkToSave(this.getUser());
						if (process.cancel(this.getUser().getToken(),
								I18N.prop("msg_bobas_user_deleted_approval_data"))) {
							if (this.getTrigger() instanceof IApprovalData) {
								// 审批流程取消，修改触发对象状态
								IApprovalData approvalData = (IApprovalData) this.getTrigger();
								if (approvalData.getApprovalStatus() == emApprovalStatus.PROCESSING) {
									approvalData.setApprovalStatus(emApprovalStatus.CANCELLED);
								}
							}
							return;
						}
					}
				}
			}
			// 检查用户取消操作
			if (this.getTrigger() instanceof IBOTagCanceled) {
				IBOTagCanceled tagCanceled = (IBOTagCanceled) this.getTrigger();
				if (tagCanceled.getCanceled() == emYesNo.YES) {
					if (process == null) {
						process = this.getProcessManager().startProcess(this.getBeAffected(), contract.getHost());
					}
					if (process != null) {
						process.checkToSave(this.getUser());
						if (process.cancel(this.getUser().getToken(),
								I18N.prop("msg_bobas_user_canceled_approval_data"))) {
							if (this.getTrigger() instanceof IApprovalData) {
								// 审批流程取消，修改触发对象状态
								IApprovalData approvalData = (IApprovalData) this.getTrigger();
								if (approvalData.getApprovalStatus() == emApprovalStatus.PROCESSING) {
									approvalData.setApprovalStatus(emApprovalStatus.CANCELLED);
								}
							}
							return;
						}
					}
				}
			}
			// 检查用户单据计划操作
			if (this.getTrigger() instanceof IBODocument) {
				IBODocument document = (IBODocument) this.getTrigger();
				if (document.getDocumentStatus() == emDocumentStatus.PLANNED) {
					if (process == null) {
						process = this.getProcessManager().startProcess(this.getBeAffected(), contract.getHost());
					}
					if (process != null) {
						process.checkToSave(this.getUser());
						if (process.cancel(this.getUser().getToken(),
								I18N.prop("msg_bobas_user_planed_approval_data"))) {
							if (this.getTrigger() instanceof IApprovalData) {
								// 审批流程取消，修改触发对象状态
								IApprovalData approvalData = (IApprovalData) this.getTrigger();
								if (approvalData.getApprovalStatus() == emApprovalStatus.PROCESSING) {
									approvalData.setApprovalStatus(emApprovalStatus.CANCELLED);
								}
							}
							return;
						}
					}
				}
			}
			// 检查用户单据行计划操作
			if (this.getTrigger() instanceof IBODocumentLine) {
				IBODocumentLine document = (IBODocumentLine) this.getTrigger();
				if (document.getLineStatus() == emDocumentStatus.PLANNED) {
					if (process == null) {
						process = this.getProcessManager().startProcess(this.getBeAffected(), contract.getHost());
					}
					if (process != null) {
						process.checkToSave(this.getUser());
						if (process.cancel(this.getUser().getToken(),
								I18N.prop("msg_bobas_user_planed_approval_data"))) {
							if (this.getTrigger() instanceof IApprovalData) {
								// 审批流程取消，修改触发对象状态
								IApprovalData approvalData = (IApprovalData) this.getTrigger();
								if (approvalData.getApprovalStatus() == emApprovalStatus.PROCESSING) {
									approvalData.setApprovalStatus(emApprovalStatus.CANCELLED);
								}
							}
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

}
