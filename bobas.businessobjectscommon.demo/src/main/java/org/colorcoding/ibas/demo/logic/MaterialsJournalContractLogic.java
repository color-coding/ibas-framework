package org.colorcoding.ibas.demo.logic;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;
import org.colorcoding.ibas.demo.bo.materialsjournal.IMaterialsJournal;
import org.colorcoding.ibas.demo.bo.materialsjournal.MaterialsJournal;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

@LogicContract(IMaterialsJournalContract.class)
public class MaterialsJournalContractLogic extends BusinessLogic<IMaterialsJournalContract, IMaterialsJournal> {

	@Override
	protected IMaterialsJournal fetchBeAffected(IMaterialsJournalContract contract) {
		ICriteria criteria = Criteria.create();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(MaterialsJournal.PROPERTY_BASEDOCUMENTTYPE.getName());
		condition.setValue(contract.getDocumentType());
		condition = criteria.getConditions().create();
		condition.setAlias(MaterialsJournal.PROPERTY_BASEDOCUMENTENTRY.getName());
		condition.setValue(contract.getDocumentEntry());
		condition = criteria.getConditions().create();
		condition.setAlias(MaterialsJournal.PROPERTY_BASEDOCUMENTLINEID.getName());
		condition.setValue(contract.getDocumentLineId());
		// 先在事务缓存中查询
		IMaterialsJournal journal = this.fetchBeAffected(IMaterialsJournal.class, criteria);
		if (journal == null) {
			// 事务中不存在，数据仓库中查询
			try (BORepositoryTrainingTesting boRepositry = new BORepositoryTrainingTesting()) {
				boRepositry.setTransaction(this.getTransaction());
				IOperationResult<IMaterialsJournal> operationResult = boRepositry.fetchMaterialsJournal(criteria);
				if (operationResult.getError() != null) {
					throw operationResult.getError();
				}
				journal = operationResult.getResultObjects().firstOrDefault();
			} catch (Exception e) {
				throw new BusinessLogicException(e);
			}
		}
		if (journal == null) {
			// 数据仓库中不存在，创建新的
			journal = new MaterialsJournal();
			journal.setBaseDocumentType(contract.getDocumentType());
			journal.setBaseDocumentEntry(contract.getDocumentEntry());
			journal.setBaseDocumentLineId(contract.getDocumentLineId());
		}
		return journal;
	}

	@Override
	protected void impact(IMaterialsJournalContract contract) {
		// 正向逻辑，已标记删除的，撤销并重新赋值
		if (this.getBeAffected().isDeleted()) {
			this.getBeAffected().undelete();
		}
		// 从新赋值
		this.getBeAffected().setItemCode(contract.getItemCode());
		this.getBeAffected().setBaseDocumentType(contract.getDocumentType());
		this.getBeAffected().setBaseDocumentEntry(contract.getDocumentEntry());
		this.getBeAffected().setBaseDocumentLineId(contract.getDocumentLineId());
		this.getBeAffected().setQuantity(contract.getQuantity());
	}

	@Override
	protected void revoke(IMaterialsJournalContract contract) {
		// 反向逻辑，删除数据
		this.getBeAffected().delete();
	}

}
