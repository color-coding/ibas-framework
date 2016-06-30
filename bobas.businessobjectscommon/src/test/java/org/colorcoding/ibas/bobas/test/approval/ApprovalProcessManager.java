package org.colorcoding.ibas.bobas.test.approval;

import org.colorcoding.ibas.bobas.approval.IApprovalProcess;

public class ApprovalProcessManager extends org.colorcoding.ibas.bobas.approval.ApprovalProcessManager {

	@Override
	protected IApprovalProcess createApprovalProcess(String boCode) {
		return new ApprovalProcess();
	}

	@Override
	protected IApprovalProcess loadApprovalProcess(String boKey) {
		return null;
	}

}
