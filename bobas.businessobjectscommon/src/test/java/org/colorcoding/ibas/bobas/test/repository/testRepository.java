package org.colorcoding.ibas.bobas.test.repository;

import org.colorcoding.ibas.bobas.common.OperationMessages;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.ownership.PermissionItem;

import junit.framework.TestCase;

public class testRepository extends TestCase {

	public void testPermissions() {
		BORepositoryTest boRepository = new BORepositoryTest();
		OperationResult<PermissionItem> operationResult = boRepository.getPermissions();
		assertEquals("permissions error.", operationResult.getResultCode(), 0);
		for (PermissionItem item : operationResult.getResultObjects()) {
			System.out.println(String.format("%s %s %s", item.getGroup(), item.getName(), item.getValue()));
		}
		OperationMessages operationMessages = boRepository.closeOrders("");
		assertEquals("permissions error.", operationMessages.getResultCode(), 0);
		operationMessages = boRepository.openOrders("");
		assertEquals("permissions error.", operationMessages.getResultCode(), -1);

	}

}
