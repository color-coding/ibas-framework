package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.expressions.IPropertyValueOperator;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;

public class ApprovalDataJudgmentLinksEx extends ApprovalDataJudgmentLinks {

	private IBORepository4DbReadonly repository;

	public final IBORepository4DbReadonly getRepository() {
		return repository;
	}

	public final void setRepository(IBORepository4DbReadonly repository) {
		this.repository = repository;
	}

	@Override
	public IPropertyValueOperator createPropertyValueOperator(PropertyValueMode mode) {
		if (mode == PropertyValueMode.SQL_SCRIPT) {
			// 数据库脚本比较
			return new SQLScriptValueOperator(this.getRepository());
		}
		return super.createPropertyValueOperator(mode);
	}
}
