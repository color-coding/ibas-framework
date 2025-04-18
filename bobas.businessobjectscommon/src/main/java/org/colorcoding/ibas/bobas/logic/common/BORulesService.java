package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.ITrackable;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.bobas.rule.BusinessRulesManager;
import org.colorcoding.ibas.bobas.rule.IBusinessRules;
import org.colorcoding.ibas.bobas.rule.ICheckRules;

@LogicContract(IBORulesContract.class)
public class BORulesService extends BusinessLogic<IBORulesContract, IBusinessObject> {

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data == this.getHost()) {
			if (data instanceof ITrackable) {
				ITrackable trackable = (ITrackable) data;
				if (trackable.isLoading()) {
					Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
							"isLoading", "true");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected IBusinessObject fetchBeAffected(IBORulesContract contract) {
		return BOUtilities.VALUE_EMPTY;
	}

	@Override
	protected void impact(IBORulesContract contract) {
		// 运行对象业务规则
		if (contract.getHost() instanceof BusinessObject<?>) {
			BusinessObject<?> bo = (BusinessObject<?>) contract.getHost();
			// 首先执行子项规则
			BOUtilities.traverse(bo, (data) -> {
				// 加载状态不执行逻辑
				if (data.isLoading()) {
					return;
				}
				// 执行规则
				this.executeRules(data);
			});
			// 执行自身规则
			this.executeRules(bo);
		} else if (contract.getHost() instanceof ICheckRules) {
			// 非业务对象，仅考虑自定义规则
			((ICheckRules) contract.getHost()).check();
		}
	}

	private final void executeRules(BusinessObject<?> bo) {
		IBusinessRules boRules = BusinessRulesManager.create().getRules(bo.getClass());
		if (boRules == null) {
			return;
		}
		// 业务逻辑
		boRules.execute(bo);
		// 自定义规则
		if (bo instanceof ICheckRules) {
			((ICheckRules) bo).check();
		}
	}

	@Override
	protected void revoke(IBORulesContract contract) {

	}

}
