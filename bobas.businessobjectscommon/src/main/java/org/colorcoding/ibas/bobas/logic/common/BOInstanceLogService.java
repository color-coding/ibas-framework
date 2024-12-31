package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;

@LogicContract(IBOInstanceLogContract.class)
public class BOInstanceLogService extends BusinessLogic<IBOInstanceLogContract, IBusinessObject> {

	@Override
	protected IBusinessObject fetchBeAffected(IBOInstanceLogContract contract) {
		return null;
	}

	@Override
	protected void impact(IBOInstanceLogContract contract) {

	}

	@Override
	protected void revoke(IBOInstanceLogContract contract) {

	}

}
