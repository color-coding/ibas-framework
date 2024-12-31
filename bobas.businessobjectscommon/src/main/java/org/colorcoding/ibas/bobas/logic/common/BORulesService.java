package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;

@LogicContract(IBORulesContract.class)
public class BORulesService extends BusinessLogic<IBORulesContract, IBusinessObject> {

	@Override
	protected IBusinessObject fetchBeAffected(IBORulesContract contract) {
		return BOUtilities.VALUE_EMPTY;
	}

	@Override
	protected void impact(IBORulesContract contract) {

	}

	@Override
	protected void revoke(IBORulesContract contract) {
	}

}
