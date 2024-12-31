package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IPeriodData;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;

@LogicContract(IBOPeriodContract.class)
public class BOPeriodService extends BusinessLogic<IBOPeriodContract, IBusinessObject> {

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data == this.getHost()) {
			if (!(data instanceof IPeriodData)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected IBusinessObject fetchBeAffected(IBOPeriodContract contract) {
		return BOUtilities.VALUE_EMPTY;
	}

	@Override
	protected void impact(IBOPeriodContract contract) {

	}

	@Override
	protected void revoke(IBOPeriodContract contract) {

	}

}
