package org.colorcoding.ibas.bobas.test.approval;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.approval.IApprovalProcess;

public class ApprovalProcessManager extends org.colorcoding.ibas.bobas.approval.ApprovalProcessManager {

	@Override
	protected Iterator<IApprovalProcess> createApprovalProcess(String boCode) {
		return null;
	}

	@Override
	protected IApprovalProcess loadApprovalProcess(String boKey) {
		return null;
	}

}
