package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.mapping.LogicContract;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

@LogicContract(IMaterialsInventoryQuantityContract.class)
public class MaterialsInventoryQuantityLogic extends BusinessLogic<IMaterialsInventoryQuantityContract, IMaterials> {

	@Override
	protected IMaterials fetchBeAffected(IMaterialsInventoryQuantityContract contract) {
		ICriteria criteria = Criteria.create();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(Materials.PROPERTY_ITEMCODE.getName());
		condition.setValue(contract.getItemCode());
		// 先在事务缓存中查询
		IMaterials materials = this.fetchBeAffected(criteria, IMaterials.class);
		if (materials == null) {
			// 事务中不存在
			BORepositoryTest boRepositry = new BORepositoryTest();
			boRepositry.setRepository(this.getRepository());
			IOperationResult<IMaterials> operationResult = boRepositry.fetchMaterials(criteria);
			if (operationResult.getError() != null) {
				throw new BusinessLogicException(operationResult.getError());
			}
			if (operationResult.getResultCode() != 0) {
				throw new BusinessLogicException(operationResult.getMessage());
			}
			materials = operationResult.getResultObjects().firstOrDefault();
		}
		return materials;
	}

	@Override
	protected void impact(IMaterialsInventoryQuantityContract contract) {
		// 增加订购数量
		this.getBeAffected().setOnHand(this.getBeAffected().getOnHand().add(contract.getQuantity()));
		Logger.log("logic: %s's hand quantity add %s.", this.getBeAffected(), contract.getQuantity());
	}

	@Override
	protected void revoke(IMaterialsInventoryQuantityContract contract) {
		// 减小订购数量
		this.getBeAffected().setOnHand(this.getBeAffected().getOnHand().subtract(contract.getQuantity()));
		Logger.log("logic: %s's hand quantity sub %s.", this.getBeAffected(), contract.getQuantity());
	}

}
