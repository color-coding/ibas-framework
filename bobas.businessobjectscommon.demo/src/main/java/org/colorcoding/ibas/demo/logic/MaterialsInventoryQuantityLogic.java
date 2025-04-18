package org.colorcoding.ibas.demo.logic;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.demo.bo.materials.IMaterials;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

@LogicContract(IMaterialsInventoryQuantityContract.class)
public class MaterialsInventoryQuantityLogic extends BusinessLogic<IMaterialsInventoryQuantityContract, IMaterials> {

	@Override
	protected IMaterials fetchBeAffected(IMaterialsInventoryQuantityContract contract) {
		ICriteria criteria = Criteria.create();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(Materials.PROPERTY_ITEMCODE.getName());
		condition.setValue(contract.getItemCode());
		// 先在事务缓存中查询
		IMaterials materials = this.fetchBeAffected(IMaterials.class, criteria);
		if (materials == null) {
			// 事务中不存在
			try (BORepositoryTrainingTesting boRepositry = new BORepositoryTrainingTesting()) {
				boRepositry.setTransaction(this.getTransaction());
				IOperationResult<IMaterials> operationResult = boRepositry.fetchMaterials(criteria);
				if (operationResult.getError() != null) {
					throw operationResult.getError();
				}
				materials = operationResult.getResultObjects().firstOrDefault();
			} catch (Exception e) {
				throw new BusinessLogicException(e);
			}
		}
		if (materials == null) {
			throw new BusinessLogicException(String.format("not found materials [%s].", contract.getItemCode()));
		}
		return materials;
	}

	@Override
	protected void impact(IMaterialsInventoryQuantityContract contract) {
		// 增加订购数量
		this.getBeAffected().setOnHand(this.getBeAffected().getOnHand().add(contract.getQuantity()));
	}

	@Override
	protected void revoke(IMaterialsInventoryQuantityContract contract) {
		// 减小订购数量
		this.getBeAffected().setOnHand(this.getBeAffected().getOnHand().subtract(contract.getQuantity()));
	}

}
