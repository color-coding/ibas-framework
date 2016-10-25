package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.TrackableBase;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;

/**
 * 审批数据代理
 * 
 * @author Niuren.Zhu
 *
 */
public class ApprovalDataProxy extends TrackableBase implements IApprovalData {

	public ApprovalDataProxy() {
		this.markOld();
	}

	@Override
	public void markOld(boolean forced) {
		this.markOld();
	}

	@Override
	public void markDeleted(boolean forced) {
		this.markDeleted();
	}

	private String objectCode;

	@Override
	public String getObjectCode() {
		return this.objectCode;
	}

	public void setObjectCode(String value) {
		this.objectCode = value;
	}

	private Integer dataOwner;

	@Override
	public Integer getDataOwner() {
		return this.dataOwner;
	}

	public void setDataOwner(Integer value) {
		this.dataOwner = value;
	}

	private emApprovalStatus approvalStatus;

	@Override
	public emApprovalStatus getApprovalStatus() {
		return this.approvalStatus;
	}

	public void setApprovalStatus(emApprovalStatus value) {
		if (this.approvalStatus == value) {
			return;
		}
		this.approvalStatus = value;
		this.markDirty();
	}

	private String identifiers;

	@Override
	public String getIdentifiers() {
		// like "{[CC_TT_SALESORDER].[DocEntry = 1]&[LineId = 2]}"
		return this.identifiers;
	}

	public void setIdentifiers(String value) {
		this.identifiers = value;
	}

	@Override
	public ICriteria getCriteria() {
		return Criteria.create(this.getIdentifiers());
	}

	@Override
	public void clearDeleted() {

	}

	public String toString() {
		if (this.getIdentifiers() != null && !this.getIdentifiers().isEmpty()) {
			return this.getIdentifiers();
		}
		return super.toString();
	}
}
