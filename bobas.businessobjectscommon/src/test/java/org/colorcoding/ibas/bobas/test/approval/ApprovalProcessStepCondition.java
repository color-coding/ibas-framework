package org.colorcoding.ibas.bobas.test.approval;

import org.colorcoding.ibas.bobas.approval.IApprovalProcessStepCondition;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;

public class ApprovalProcessStepCondition implements IApprovalProcessStepCondition {
	public ApprovalProcessStepCondition() {
		this.setOperation(emConditionOperation.EQUAL);
		this.setRelation(emConditionRelationship.AND);
	}

	private String propertyName;

	@Override
	public String getPropertyName() {
		return this.propertyName;
	}

	public void setPropertyName(String value) {
		this.propertyName = value;
	}

	private emConditionOperation operation;

	@Override
	public emConditionOperation getOperation() {
		return this.operation;
	}

	public void setOperation(emConditionOperation value) {
		this.operation = value;
	}

	private emConditionRelationship relation;

	@Override
	public emConditionRelationship getRelation() {
		return this.relation;
	}

	public void setRelation(emConditionRelationship value) {
		this.relation = value;
	}

	private String conditionValue;

	@Override
	public String getConditionValue() {
		return this.conditionValue;
	}

	public void setConditionValue(String value) {
		this.conditionValue = value;
	}

}
