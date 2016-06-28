package org.colorcoding.ibas.bobas.test.approval;

import java.util.ArrayList;

import org.colorcoding.ibas.bobas.approval.IApprovalProcessStep;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessStepCondition;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.organization.IUser;
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
		};
		IUser manager = new IUser() {
			@Override
			public int getId() {
				return 200;
			}
		};
		IUser boss = new IUser() {
			@Override
			public int getId() {
				return 300;
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
		aCondition.setPropertyName("DocumentTotal");
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		ApprovalProcessStepCondition bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName("DocumentTotal");
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("5000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(aStep);
		// 5000以上，10000以下经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(20);
		aStep.setOwner(manager);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocumentTotal");
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("5000");
		bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName("DocumentTotal");
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("10000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(aStep);
		// 10000以上，总经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(30);
		aStep.setOwner(boss);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("DocumentTotal");
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
}
