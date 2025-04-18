package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.bobas.period.IPeriodData;
import org.colorcoding.ibas.bobas.period.PeriodFactory;
import org.colorcoding.ibas.bobas.period.PeriodsManager;

@LogicContract(IBOPeriodContract.class)
public class BOPeriodService extends BusinessLogic<IBOPeriodContract, IBusinessObject> {

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data == this.getHost()) {
			if (!(data instanceof IPeriodData)) {
				Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"isPeriodData", "false");
				return false;
			}
		}
		return true;
	}

	@Override
	protected IBusinessObject fetchBeAffected(IBOPeriodContract contract) {
		return BOUtilities.VALUE_EMPTY;
	}

	private PeriodsManager periodsManager;

	protected final PeriodsManager getPeriodsManager() {
		if (this.periodsManager == null) {
			this.periodsManager = PeriodFactory.createManager();
		}
		return periodsManager;
	}

	@Override
	protected void impact(IBOPeriodContract contract) {
		try {
			// 补全对象期间
			this.getPeriodsManager().applyPeriod(contract.getHost());
			// 检查期间是否可用
			this.getPeriodsManager().checkPeriod(contract.getHost());
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	@Override
	protected void revoke(IBOPeriodContract contract) {
		try {
			// 检查期间是否可用
			this.getPeriodsManager().checkPeriod(contract.getHost());
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

}
