package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.approval.ApprovalFactory;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IApprovalProcess;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;

@LogicContract(IBOApprovalContract.class)
public class BOApprovalService extends BusinessLogic<IBOApprovalContract, IApprovalProcess> {

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
			} else {
				Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"isApprovalData", "false");
				return false;
			}
		}
		return true;
	}

	@Override
	protected IApprovalProcess fetchBeAffected(IBOApprovalContract contract) {
		return ApprovalFactory.create().createManager().checkProcess(contract.getHost());
	}

	@Override
	protected void impact(IBOApprovalContract contract) {
		IApprovalProcess process = this.getBeAffected();
		try {
			// 检查用户是否可以修改数据
			process.checkToSave(this.getUser());
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	@Override
	protected void revoke(IBOApprovalContract contract) {

	}

}
