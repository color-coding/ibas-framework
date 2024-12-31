package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;

@LogicContract(IBOApprovalContract.class)
public class BOApprovalService extends BusinessLogic<IBOApprovalContract, IBusinessObject> {

	@Override
	protected IBusinessObject fetchBeAffected(IBOApprovalContract contract) {
		return null;
	}

	@Override
	protected void impact(IBOApprovalContract contract) {
	}

	@Override
	protected void revoke(IBOApprovalContract contract) {

	}

}
