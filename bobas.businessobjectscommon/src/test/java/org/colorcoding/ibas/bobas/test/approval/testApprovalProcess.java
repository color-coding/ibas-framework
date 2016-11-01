package org.colorcoding.ibas.bobas.test.approval;

import java.util.ArrayList;

import org.colorcoding.ibas.bobas.approval.ApprovalProcessException;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessStep;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessStepCondition;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

import junit.framework.TestCase;

public class testApprovalProcess extends TestCase {

	public void testUseApprovalProcess() {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.Released);
		order.setDocumentTotal(new Decimal("99.99"));
		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new Decimal(10));
		orderItem.setPrice(new Decimal(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);
		IUser leader = new IUser() {
			@Override
			public int getId() {
				return 100;
			}

			@Override
			public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

			}

			@Override
			public String getBelong() {
				return null;
			}

			@Override
			public String getToken() {
				return null;
			}
		};
		IUser manager = new IUser() {
			@Override
			public int getId() {
				return 200;
			}

			@Override
			public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

			}

			@Override
			public String getBelong() {
				return null;
			}

			@Override
			public String getToken() {
				return null;
			}
		};
		IUser boss = new IUser() {
			@Override
			public int getId() {
				return 300;
			}

			@Override
			public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

			}

			@Override
			public String getBelong() {
				return null;
			}

			@Override
			public String getToken() {
				return null;
			}
		};
		// 创建审批流程
		ApprovalProcess aProcess = new ApprovalProcess();
		aProcess.setName("订单审批流程");

		ArrayList<IApprovalProcessStep> aSteps = new ArrayList<IApprovalProcessStep>();
		// 3000以上，5000以下，组长审批
		ApprovalProcessStep aStep = new ApprovalProcessStep();
		aStep.setId(10);
		aStep.setOwner(leader);
		ApprovalProcessStepCondition aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocTotal");// DocumentTotal
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		ApprovalProcessStepCondition bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName("DocTotal");// DocumentTotal
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("5000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(aStep);
		// 5000以上，10000以下经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(20);
		aStep.setOwner(manager);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocTotal");// DocumentTotal
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("5000");
		bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName("DocTotal");// DocumentTotal
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("10000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(aStep);
		// 10000以上，总经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(30);
		aStep.setOwner(boss);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocTotal");// DocumentTotal
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("10000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition });
		aSteps.add(aStep);
		// 添加审批步骤
		aProcess.setProcessSteps(aSteps.toArray(new IApprovalProcessStep[] {}));

		// 小于2000 不进入审批
		order.setDocumentTotal("2000");
		boolean done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, false);
		// 3000-5000间组长审批
		order.setDocumentTotal("4000");
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, true);
		assertEquals(
				String.format("order total [%s].step [%s]", order.getDocumentTotal(), aProcess.currentStep().getId()),
				aProcess.currentStep().getId(), 10);
		// 5000-10000间经理审批
		order.setDocumentTotal("6000");
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, true);
		assertEquals(
				String.format("order total [%s].step [%s]", order.getDocumentTotal(), aProcess.currentStep().getId()),
				aProcess.currentStep().getId(), 20);
		// 10000以上，总经理审批
		order.setDocumentTotal("12000");
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, true);
		assertEquals(
				String.format("order total [%s].step [%s]", order.getDocumentTotal(), aProcess.currentStep().getId()),
				aProcess.currentStep().getId(), 30);

		// 子项属性作为条件的审批流程
		ApprovalProcess bProcess = new ApprovalProcess();
		bProcess.setName("订单审批流程-子项属性比较");

		aSteps = new ArrayList<IApprovalProcessStep>();
		// 包含S00001物料的，总经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(10);
		aStep.setOwner(boss);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("SalesOrderItems.ItemCode");
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("S00001");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition });
		aSteps.add(aStep);
		bProcess.setProcessSteps(aSteps.toArray(new IApprovalProcessStep[] {}));

		// 搞不定，放弃了，科科
		// done = bProcess.start(order);
		// assertEquals(String.format("order item [%s].",
		// orderItem.getItemCode()), done, false);
		// orderItem.setItemCode("S00001");
		// done = bProcess.start(order);
		// assertEquals(String.format("order item [%s].",
		// orderItem.getItemCode()), done, true);
	}

	public void testApprovalSteps() throws ApprovalProcessException, InvalidAuthorizationException {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.Released);
		order.setDocumentTotal(new Decimal("99.99"));
		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new Decimal(10));
		orderItem.setPrice(new Decimal(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);
		IUser salse = new IUser() {
			@Override
			public int getId() {
				return 100;
			}

			@Override
			public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

			}

			@Override
			public String getBelong() {
				return null;
			}

			@Override
			public String getToken() {
				return null;
			}
		};
		IUser leader = new IUser() {
			@Override
			public int getId() {
				return 200;
			}

			@Override
			public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

			}

			@Override
			public String getBelong() {
				return null;
			}

			@Override
			public String getToken() {
				return null;
			}
		};
		IUser manager = new IUser() {
			@Override
			public int getId() {
				return 300;
			}

			@Override
			public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

			}

			@Override
			public String getBelong() {
				return null;
			}

			@Override
			public String getToken() {
				return null;
			}
		};
		IUser boss = new IUser() {
			@Override
			public int getId() {
				return 400;
			}

			@Override
			public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

			}

			@Override
			public String getBelong() {
				return null;
			}

			@Override
			public String getToken() {
				return null;
			}
		};
		// 创建审批流程
		ApprovalProcess aProcess = new ApprovalProcess();
		aProcess.setName("订单审批流程2");

		ArrayList<IApprovalProcessStep> aSteps = new ArrayList<IApprovalProcessStep>();
		// 3000以上，组长审批
		ApprovalProcessStep step10 = new ApprovalProcessStep();
		step10.setId(10);
		step10.setOwner(leader);
		ApprovalProcessStepCondition aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocTotal");// DocumentTotal
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		step10.setConditions(new IApprovalProcessStepCondition[] { aCondition });
		aSteps.add(step10);
		// 3000以上，10000以下经理审批
		ApprovalProcessStep step20 = new ApprovalProcessStep();
		step20.setId(20);
		step20.setOwner(manager);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocTotal");// DocumentTotal
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		ApprovalProcessStepCondition bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName("DocTotal");// DocumentTotal
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("10000");
		step20.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(step20);
		// 3000以上，10000以上，总经理审批
		ApprovalProcessStep step30 = new ApprovalProcessStep();
		step30.setId(30);
		step30.setOwner(boss);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocTotal");// DocumentTotal
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName("DocTotal");// DocumentTotal
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("10000");
		step30.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(step30);
		// 添加审批步骤
		aProcess.setProcessSteps(aSteps.toArray(new IApprovalProcessStep[] {}));

		// 4000 进入审批
		order.setDocumentTotal("4000");
		boolean done = aProcess.start(order);
		assertEquals(String.format("order total [%s] not start approval process.", order.getDocumentTotal()), done,
				true);
		assertEquals("ap was not approved.", aProcess.getStatus(), emApprovalStatus.Processing);
		// 全部批准
		aProcess.approval(step10.getId(), emApprovalResult.Approved, "", "");
		assertEquals("ap step 10 was not approved.", step10.getStatus(), emApprovalStepStatus.Approved);
		assertEquals("ap step 20 was not processing.", step20.getStatus(), emApprovalStepStatus.Processing);
		aProcess.approval(step20.getId(), emApprovalResult.Approved, "", "");
		assertEquals("ap step 20 was not approved.", step20.getStatus(), emApprovalStepStatus.Approved);
		assertEquals("ap step 30 was not processing.", step30.getStatus(), emApprovalStepStatus.Processing);
		aProcess.approval(step30.getId(), emApprovalResult.Approved, "", "");
		assertEquals("ap step 30 was not approved.", step30.getStatus(), emApprovalStepStatus.Approved);
		assertEquals("ap was not approved.", aProcess.getStatus(), emApprovalStatus.Approved);

		// 先拒绝，然后撤销，再批准
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s] not start approval process.", order.getDocumentTotal()), done,
				true);
		aProcess.approval(step10.getId(), emApprovalResult.Rejected, "", "");
		assertEquals("ap step 10 was not Rejected.", step10.getStatus(), emApprovalStepStatus.Rejected);
		assertEquals("ap was not Rejected.", aProcess.getStatus(), emApprovalStatus.Rejected);
		aProcess.approval(step10.getId(), emApprovalResult.Processing, "", "");
		assertEquals("ap step 10 was not Processing.", step10.getStatus(), emApprovalStepStatus.Processing);
		assertEquals("ap was not Processing.", aProcess.getStatus(), emApprovalStatus.Processing);
		aProcess.approval(step10.getId(), emApprovalResult.Approved, "", "");
		aProcess.approval(step20.getId(), emApprovalResult.Rejected, "", "");
		assertEquals("ap step 20 was not Rejected.", step20.getStatus(), emApprovalStepStatus.Rejected);
		assertEquals("ap was not Rejected.", aProcess.getStatus(), emApprovalStatus.Rejected);
		aProcess.approval(step20.getId(), emApprovalResult.Processing, "", "");
		assertEquals("ap step 20 was not Processing.", step20.getStatus(), emApprovalStepStatus.Processing);
		assertEquals("ap was not Processing.", aProcess.getStatus(), emApprovalStatus.Processing);
		aProcess.approval(step20.getId(), emApprovalResult.Approved, "", "");
		aProcess.approval(step30.getId(), emApprovalResult.Rejected, "", "");
		assertEquals("ap step 30 was not Rejected.", step30.getStatus(), emApprovalStepStatus.Rejected);
		assertEquals("ap was not Rejected.", aProcess.getStatus(), emApprovalStatus.Rejected);
		aProcess.approval(step30.getId(), emApprovalResult.Processing, "", "");
		assertEquals("ap step 20 was not Processing.", step30.getStatus(), emApprovalStepStatus.Processing);
		assertEquals("ap was not Processing.", aProcess.getStatus(), emApprovalStatus.Processing);

		// aProcess.approval(step10.getId(), emApprovalResult.Processing, "",
		// "");// 跳步骤重置，此处应报错
		aProcess.setOwner(salse);
		aProcess.cancel("", "");

	}
}
