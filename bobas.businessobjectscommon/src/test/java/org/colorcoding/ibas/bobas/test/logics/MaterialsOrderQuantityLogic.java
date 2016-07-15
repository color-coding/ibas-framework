package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.logics.BusinessLogic;
import org.colorcoding.ibas.bobas.mapping.LogicContract;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

/**
 * 物料订购数量逻辑
 * 
 * @author Niuren.Zhu
 *
 */
@LogicContract(IMaterialsOrderQuantityContract.class)
public class MaterialsOrderQuantityLogic extends BusinessLogic<IMaterialsOrderQuantityContract, IMaterials> {

	@Override
	protected IMaterials fetchBeAffected(IMaterialsOrderQuantityContract contract) {
		ICriteria criteria = Criteria.create();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(Materials.ItemCodeProperty.getName());
		condition.setCondVal(contract.getItemCode());
		BORepositoryTest boRepositry = new BORepositoryTest();
		boRepositry.setRepository(this.getRepository());
		IOperationResult<IMaterials> operationResult = boRepositry.fetchMaterials(criteria);
		return operationResult.getResultObjects().firstOrDefault();

	}

	@Override
	protected void impact(IMaterialsOrderQuantityContract contract) {
		// 增加订购数量
		this.getBeAffected().setOnOrder(this.getBeAffected().getOnOrder().add(contract.getQuantity()));
		RuntimeLog.log("logic: %s's order quantity add %s.", this.getBeAffected(), contract.getQuantity());
	}

	@Override
	protected void revoke(IMaterialsOrderQuantityContract contract) {
		// 减小订购数量
		this.getBeAffected().setOnOrder(this.getBeAffected().getOnOrder().subtract(contract.getQuantity()));
		RuntimeLog.log("logic: %s's order quantity sub %s.", this.getBeAffected(), contract.getQuantity());
	}

}
