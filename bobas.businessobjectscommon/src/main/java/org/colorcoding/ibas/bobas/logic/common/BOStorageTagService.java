package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IDataOwnership;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.LogicContract;

@LogicContract(IBOStorageTagContract.class)
public class BOStorageTagService extends BusinessLogic<IBOStorageTagContract, IBusinessObject> {

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data == this.getHost()) {
			if (!(data instanceof IBOStorageTag)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected IBusinessObject fetchBeAffected(IBOStorageTagContract contract) {
		return BOUtilities.VALUE_EMPTY;
	}

	@Override
	protected void impact(IBOStorageTagContract contract) {
		if (contract.getHost().isNew()) {
			// 新建对象
			contract.getHost().setCreateDate(DateTimes.today());
			contract.getHost().setCreateTime(DateTimes.time());
			// contract.getHost().setCreateUserSign(user.getId());
			if (contract.getHost().getCreateActionId() == null || contract.getHost().getCreateActionId().isEmpty()) {
				contract.getHost().setCreateActionId(this.getTransaction().getId());
			}
			contract.getHost().setLogInst(1);
			if (contract.getHost() instanceof IDataOwnership) {
				// 数据所有者标记
				IDataOwnership data = (IDataOwnership) contract.getHost();
				if (data.getDataOwner() == null || data.getDataOwner() == 0) {
					// data.setDataOwner(user.getId());
				}
				if (Strings.isNullOrEmpty(data.getOrganization())) {
					// data.setOrganization(user.getBelong());
				}
			}
		} else {
			// 更新对象
			contract.getHost().setUpdateDate(DateTimes.today());
			contract.getHost().setUpdateTime(DateTimes.time());
			// contract.getHost().setUpdateUserSign(user.getId());
			contract.getHost().setUpdateActionId(this.getTransaction().getId());
			contract.getHost().setLogInst(contract.getHost().getLogInst() + 1);
		}
	}

	@Override
	protected void revoke(IBOStorageTagContract contract) {

	}

}
