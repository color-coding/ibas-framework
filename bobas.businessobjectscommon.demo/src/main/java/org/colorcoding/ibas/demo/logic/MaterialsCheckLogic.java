package org.colorcoding.ibas.demo.logic;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.demo.bo.materials.IMaterials;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

@LogicContract(IMaterialsCheckContract.class)
public class MaterialsCheckLogic extends BusinessLogic<IMaterialsCheckContract, IMaterials> {

	@Override
	protected IMaterials fetchBeAffected(IMaterialsCheckContract contract) {
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
	protected void impact(IMaterialsCheckContract contract) {
		if (Strings.isNullOrEmpty(contract.getItemDescription())) {
			contract.setItemDescription(this.getBeAffected().getItemDescription());
		}
	}

	@Override
	protected void revoke(IMaterialsCheckContract contract) {
	}

}
