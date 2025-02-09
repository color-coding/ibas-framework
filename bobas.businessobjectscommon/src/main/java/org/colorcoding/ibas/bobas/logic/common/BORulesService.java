package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.bobas.rule.ICheckRules;

@LogicContract(IBORulesContract.class)
public class BORulesService extends BusinessLogic<IBORulesContract, IBusinessObject> {

	@Override
	protected IBusinessObject fetchBeAffected(IBORulesContract contract) {
		return BOUtilities.VALUE_EMPTY;
	}

	@Override
	protected void impact(IBORulesContract contract) {
		// 加载状态不执行逻辑
		if (contract.getHost().isLoading()) {
			return;
		}
		// 运行对象业务规则
		if (contract.getHost() instanceof BusinessObject<?>) {
			BusinessObject<?> bo = (BusinessObject<?>) contract.getHost();
			// 首先执行子项规则
			BOUtilities.traverse(bo, (data) -> {
				if (data == null) {
					return;
				}
				this.executeRules(data);
			});
			// 执行自身规则
			this.executeRules(bo);
		}
	}

	protected void executeRules(BusinessObject<?> bo) {

		if (bo instanceof ICheckRules) {
			((ICheckRules) bo).check();
		}
	}

	@Override
	protected void revoke(IBORulesContract contract) {

	}

}
