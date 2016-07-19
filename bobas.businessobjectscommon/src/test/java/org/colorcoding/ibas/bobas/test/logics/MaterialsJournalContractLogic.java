package org.colorcoding.ibas.bobas.test.logics;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.logics.BusinessLogic;
import org.colorcoding.ibas.bobas.logics.BusinessLogicsException;
import org.colorcoding.ibas.bobas.mapping.LogicContract;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

@LogicContract(IMaterialsJournalContract.class)
public class MaterialsJournalContractLogic extends BusinessLogic<IMaterialsJournalContract, MaterialsQuantityJournal> {

	@Override
	protected MaterialsQuantityJournal fetchBeAffected(IMaterialsJournalContract contract) {
		ICriteria criteria = Criteria.create();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(MaterialsQuantityJournal.BaseDocumentTypeProperty.getName());
		condition.setCondVal(contract.getDocumentType());
		condition = criteria.getConditions().create();
		condition.setAlias(MaterialsQuantityJournal.BaseDocumentEntryProperty.getName());
		condition.setCondVal(contract.getDocumentEntry());
		condition = criteria.getConditions().create();
		condition.setAlias(MaterialsQuantityJournal.BaseDocumentLineIdProperty.getName());
		condition.setCondVal(contract.getDocumentLineId());
		// 先在事务缓存中查询
		MaterialsQuantityJournal journal = this.fetchBeAffected(criteria, MaterialsQuantityJournal.class);
		if (journal == null) {
			// 事务中不存在，数据仓库中查询
			BORepositoryTest boRepositry = new BORepositoryTest();
			boRepositry.setRepository(this.getRepository());
			IOperationResult<MaterialsQuantityJournal> operationResult = boRepositry
					.fetchMaterialsQuantityJournal(criteria);
			if (operationResult.getError() != null) {
				throw new BusinessLogicsException(operationResult.getError());
			}
			if (operationResult.getResultCode() != 0) {
				throw new BusinessLogicsException(operationResult.getMessage());
			}
			journal = operationResult.getResultObjects().firstOrDefault();
		}
		if (journal == null) {
			// 数据仓库中不存在，创建新的
			journal = new MaterialsQuantityJournal();
		}
		return journal;
	}

	@Override
	protected void impact(IMaterialsJournalContract contract) {
		// 正向逻辑，已标记删除的，撤销并重新赋值
		if (this.getBeAffected().isDeleted()) {
			this.getBeAffected().markUnDeleted();
		}
		// 从新赋值
		this.getBeAffected().setItemCode(contract.getItemCode());
		this.getBeAffected().setBaseDocumentType(contract.getDocumentType());
		this.getBeAffected().setBaseDocumentEntry(contract.getDocumentEntry());
		this.getBeAffected().setBaseDocumentLineId(contract.getDocumentLineId());
		this.getBeAffected().setQuantity(new Decimal(contract.getQuantity()));
		RuntimeLog.log("logic: create journal type:%s entry:%s line:%s.", this.getBeAffected().getBaseDocumentType(),
				this.getBeAffected().getBaseDocumentEntry(), this.getBeAffected().getBaseDocumentLineId());
	}

	@Override
	protected void revoke(IMaterialsJournalContract contract) {
		// 反向逻辑，删除数据
		this.getBeAffected().delete();
		RuntimeLog.log("logic: remove journal type:%s entry:%s line:%s.", this.getBeAffected().getBaseDocumentType(),
				this.getBeAffected().getBaseDocumentEntry(), this.getBeAffected().getBaseDocumentLineId());

	}

}
