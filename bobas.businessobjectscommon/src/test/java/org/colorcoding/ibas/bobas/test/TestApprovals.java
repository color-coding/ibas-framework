package org.colorcoding.ibas.bobas.test;

import java.math.BigDecimal;
import java.util.Iterator;

import org.colorcoding.ibas.bobas.approval.ApprovalException;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessStepCondition;
import org.colorcoding.ibas.bobas.approval.IProcessData;
import org.colorcoding.ibas.bobas.approval.IProcessStepData;
import org.colorcoding.ibas.bobas.approval.ValueMode;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;

import junit.framework.TestCase;

public class TestApprovals extends TestCase {

	public void testProcess() throws ApprovalException {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTimes.today());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(Decimals.valueOf("99.99"));
		SalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(BigDecimal.valueOf(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));
		IUser leader = new ApprovalUser(100);
		IUser manager = new ApprovalUser(200);
		IUser boss = new ApprovalUser(300);
		// 创建审批流程
		ApprovalProcess aProcess = (ApprovalProcess) new ApprovalProcessManager().startProcess(new ProcessData());

		ArrayList<ApprovalProcessStep> aSteps = new ArrayList<ApprovalProcessStep>();
		// 3000以上，5000以下，组长审批
		ApprovalProcessStep aStep = new ApprovalProcessStep();
		aStep.setId(10);
		aStep.setOwner(leader);
		ApprovalProcessStepCondition aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		ApprovalProcessStepCondition bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("5000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(aStep);
		// 5000以上，10000以下经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(20);
		aStep.setOwner(manager);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("5000");
		bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("10000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(aStep);
		// 10000以上，总经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(30);
		aStep.setOwner(boss);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("10000");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition });
		aSteps.add(aStep);
		// 添加审批步骤
		aProcess.setProcessSteps(aSteps.toArray(new org.colorcoding.ibas.bobas.approval.ApprovalProcessStep<?>[] {}));

		// 小于2000 不进入审批
		order.setDocumentTotal(Decimals.valueOf("2000"));
		boolean done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, false);
		// 3000-5000间组长审批
		order.setDocumentTotal(Decimals.valueOf("4000"));
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, true);
		assertEquals(
				String.format("order total [%s].step [%s]", order.getDocumentTotal(), aProcess.currentStep().getId()),
				aProcess.currentStep().getId(), 10);
		// 5000-10000间经理审批
		order.setDocumentTotal(Decimals.valueOf("6000"));
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, true);
		assertEquals(
				String.format("order total [%s].step [%s]", order.getDocumentTotal(), aProcess.currentStep().getId()),
				aProcess.currentStep().getId(), 20);
		// 10000以上，总经理审批
		order.setDocumentTotal(Decimals.valueOf("12000"));
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s].", order.getDocumentTotal()), done, true);
		assertEquals(
				String.format("order total [%s].step [%s]", order.getDocumentTotal(), aProcess.currentStep().getId()),
				aProcess.currentStep().getId(), 30);

		// 子项属性作为条件的审批流程
		ApprovalProcess bProcess = (ApprovalProcess) new ApprovalProcessManager().startProcess(new ProcessData());

		aSteps = new ArrayList<ApprovalProcessStep>();
		// 包含S00001物料的，总经理审批
		aStep = new ApprovalProcessStep();
		aStep.setId(10);
		aStep.setOwner(boss);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName("SalesOrderItems.ItemCode");
		aCondition.setOperation(emConditionOperation.EQUAL);
		aCondition.setConditionValue("S00001");
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition });
		aSteps.add(aStep);
		bProcess.setProcessSteps(aSteps.toArray(new org.colorcoding.ibas.bobas.approval.ApprovalProcessStep<?>[] {}));

		// 先不进审批，后进入审批
		done = bProcess.start(order);
		assertEquals(String.format("order item [%s].", orderItem.getItemCode()), done, false);
		orderItem.setItemCode("S00001");
		done = bProcess.start(order);
		assertEquals(String.format("order item [%s].", orderItem.getItemCode()), done, true);
	}

	public void testSteps() throws ApprovalException {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTimes.today());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(Decimals.valueOf("99.99"));
		SalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(BigDecimal.valueOf(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));
		IUser sales = new ApprovalUser(100);
		IUser leader = new ApprovalUser(200);
		IUser manager = new ApprovalUser(300);
		IUser boss = new ApprovalUser(400);
		// 创建审批流程
		ApprovalProcess aProcess = (ApprovalProcess) new ApprovalProcessManager().startProcess(new ProcessData());

		ArrayList<ApprovalProcessStep> aSteps = new ArrayList<ApprovalProcessStep>();
		// 3000以上，组长审批
		ApprovalProcessStep step10 = new ApprovalProcessStep();
		step10.setId(10);
		step10.setOwner(leader);
		ApprovalProcessStepCondition aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		// 属性与属性比较
		ApprovalProcessStepCondition bCondition = new ApprovalProcessStepCondition();
		bCondition.setPropertyValueMode(ValueMode.DB_FIELD);
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getAnnotation(DbField.class).name());
		bCondition.setOperation(emConditionOperation.EQUAL);
		bCondition.setConditionValueMode(ValueMode.DB_FIELD);
		bCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getAnnotation(DbField.class).name());
		step10.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(step10);
		// 3000以上，10000以下经理审批
		ApprovalProcessStep step20 = new ApprovalProcessStep();
		step20.setId(20);
		step20.setOwner(manager);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("10000");
		step20.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(step20);
		// 3000以上，10000以上，总经理审批
		ApprovalProcessStep step30 = new ApprovalProcessStep();
		step30.setId(30);
		step30.setOwner(boss);
		aCondition = new ApprovalProcessStepCondition();
		aCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		aCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		aCondition.setConditionValue("3000");
		bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		bCondition.setOperation(emConditionOperation.LESS_THAN);
		bCondition.setConditionValue("10000");
		step30.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
		aSteps.add(step30);
		// 添加审批步骤
		aProcess.setProcessSteps(aSteps.toArray(new ApprovalProcessStep[] {}));

		// 4000 进入审批
		order.setDocumentTotal(Decimals.valueOf("4000"));
		boolean done = aProcess.start(order);
		assertEquals(String.format("order total [%s] not start approval process.", order.getDocumentTotal()), done,
				true);
		assertEquals("ap was not approved.", aProcess.getStatus(), emApprovalStatus.PROCESSING);
		// 全部批准
		aProcess.approval(step10.getId(), emApprovalResult.APPROVED, "", "");
		assertEquals("ap step 10 was not approved.", step10.getStatus(), emApprovalStepStatus.APPROVED);
		assertEquals("ap step 20 was not processing.", step20.getStatus(), emApprovalStepStatus.PROCESSING);
		aProcess.approval(step20.getId(), emApprovalResult.APPROVED, "", "");
		assertEquals("ap step 20 was not approved.", step20.getStatus(), emApprovalStepStatus.APPROVED);
		assertEquals("ap step 30 was not processing.", step30.getStatus(), emApprovalStepStatus.PROCESSING);
		aProcess.approval(step30.getId(), emApprovalResult.APPROVED, "", "");
		assertEquals("ap step 30 was not approved.", step30.getStatus(), emApprovalStepStatus.APPROVED);
		assertEquals("ap was not approved.", aProcess.getStatus(), emApprovalStatus.APPROVED);

		// 先拒绝，然后撤销，再批准
		done = aProcess.start(order);
		assertEquals(String.format("order total [%s] not start approval process.", order.getDocumentTotal()), done,
				true);
		aProcess.approval(step10.getId(), emApprovalResult.REJECTED, "", "");
		assertEquals("ap step 10 was not Rejected.", step10.getStatus(), emApprovalStepStatus.REJECTED);
		assertEquals("ap was not Rejected.", aProcess.getStatus(), emApprovalStatus.REJECTED);
		aProcess.approval(step10.getId(), emApprovalResult.PROCESSING, "", "");
		assertEquals("ap step 10 was not Processing.", step10.getStatus(), emApprovalStepStatus.PROCESSING);
		assertEquals("ap was not Processing.", aProcess.getStatus(), emApprovalStatus.PROCESSING);
		aProcess.approval(step10.getId(), emApprovalResult.APPROVED, "", "");
		aProcess.approval(step20.getId(), emApprovalResult.REJECTED, "", "");
		assertEquals("ap step 20 was not Rejected.", step20.getStatus(), emApprovalStepStatus.REJECTED);
		assertEquals("ap was not Rejected.", aProcess.getStatus(), emApprovalStatus.REJECTED);
		aProcess.approval(step20.getId(), emApprovalResult.PROCESSING, "", "");
		assertEquals("ap step 20 was not Processing.", step20.getStatus(), emApprovalStepStatus.PROCESSING);
		assertEquals("ap was not Processing.", aProcess.getStatus(), emApprovalStatus.PROCESSING);
		aProcess.approval(step20.getId(), emApprovalResult.APPROVED, "", "");
		aProcess.approval(step30.getId(), emApprovalResult.REJECTED, "", "");
		assertEquals("ap step 30 was not Rejected.", step30.getStatus(), emApprovalStepStatus.REJECTED);
		assertEquals("ap was not Rejected.", aProcess.getStatus(), emApprovalStatus.REJECTED);
		aProcess.approval(step30.getId(), emApprovalResult.PROCESSING, "", "");
		assertEquals("ap step 20 was not Processing.", step30.getStatus(), emApprovalStepStatus.PROCESSING);
		assertEquals("ap was not Processing.", aProcess.getStatus(), emApprovalStatus.PROCESSING);

		// aProcess.approval(step10.getId(), emApprovalResult.Processing, "",
		// "");// 跳步骤重置，此处应报错
		aProcess.setOwner(sales);
		aProcess.cancel("", "");

	}
}

class ApprovalProcessManager extends org.colorcoding.ibas.bobas.approval.ApprovalProcessManager {

	@Override
	public ProcessData loadProcessData(IApprovalData apData) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Iterator<ApprovalProcess> createApprovalProcess(String boCode) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ApprovalProcess createApprovalProcess(IProcessData processData) {
		return new ApprovalProcess((ProcessData) processData);
	}

}

class ProcessData extends BusinessObject<ProcessData> implements IProcessData {

	private static final long serialVersionUID = 1L;

	String name = Strings.valueOf(this.hashCode());

	public String getName() {
		return name;
	}

	emApprovalStatus status;

	public emApprovalStatus getStatus() {
		return status;
	}

	public void setStatus(emApprovalStatus status) {
		this.status = status;
	}

	DateTime startedTime;

	public DateTime getStartedTime() {
		return startedTime;
	}

	public void setStartedTime(DateTime startedTime) {
		this.startedTime = startedTime;
	}

	DateTime finishedTime;

	public DateTime getFinishedTime() {
		return finishedTime;
	}

	public void setFinishedTime(DateTime finishedTime) {
		this.finishedTime = finishedTime;
	}

	IApprovalData approvalData;

	public IApprovalData getApprovalData() {
		return approvalData;
	}

	public void setApprovalData(IApprovalData approvalData) {
		this.approvalData = approvalData;
	}

	emYesNo activated;

	@Override
	public emYesNo getActivated() {
		return activated;
	}

	@Override
	public void setActivated(emYesNo value) {
		this.activated = value;
	}

}

class ApprovalUser implements IUser {

	public ApprovalUser(int id) {
		this.id = id;
	}

	private int id;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getBelong() {
		return null;
	}

	@Override
	public String getToken() {
		return null;
	}

	@Override
	public void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {

	}
}

class ApprovalProcess extends org.colorcoding.ibas.bobas.approval.ApprovalProcess<ProcessData> {

	public ApprovalProcess(ProcessData processData) {
		super(processData);
	}

	private IUser user;

	@Override
	public IUser getOwner() {
		return this.user;
	}

	public void setOwner(IUser user) {
		this.user = user;
	}

	private org.colorcoding.ibas.bobas.approval.ApprovalProcessStep<?>[] processSteps;

	@Override
	public org.colorcoding.ibas.bobas.approval.ApprovalProcessStep<?>[] getProcessSteps() {
		return this.processSteps;
	}

	public void setProcessSteps(org.colorcoding.ibas.bobas.approval.ApprovalProcessStep<?>[] value) {
		this.processSteps = value;
	}
}

class ApprovalProcessStep extends org.colorcoding.ibas.bobas.approval.ApprovalProcessStep<IProcessStepData> {
	public ApprovalProcessStep() {
		super(new ProcessDataStep());
	}

	public ApprovalProcessStep(IProcessStepData stepData) {
		super(stepData);
	}

	private IApprovalProcessStepCondition[] conditions;

	@Override
	public IApprovalProcessStepCondition[] getConditions() {
		return conditions;
	}

	public void setConditions(IApprovalProcessStepCondition[] value) {
		this.conditions = value;
	}

	int id;

	public void setId(int id) {
		((ProcessDataStep) this.getStepData()).setId(id);
	}

	IUser owner;

	public IUser getOwner() {
		return owner;
	}

	public void setOwner(IUser owner) {
		this.owner = owner;
	}

	private static class ProcessDataStep extends BusinessObject<ProcessDataStep> implements IProcessStepData {

		private static final long serialVersionUID = 1L;

		int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		String judgment;

		public String getJudgment() {
			return judgment;
		}

		public void setJudgment(String judgment) {
			this.judgment = judgment;
		}

		emApprovalStepStatus status;

		public emApprovalStepStatus getStatus() {
			return status;
		}

		public void setStatus(emApprovalStepStatus status) {
			this.status = status;
		}

		DateTime startedTime;

		public DateTime getStartedTime() {
			return startedTime;
		}

		public void setStartedTime(DateTime startedTime) {
			this.startedTime = startedTime;
		}

		DateTime finishedTime;

		public DateTime getFinishedTime() {
			return finishedTime;
		}

		public void setFinishedTime(DateTime finishedTime) {
			this.finishedTime = finishedTime;
		}

	}
}

class ApprovalProcessStepCondition implements org.colorcoding.ibas.bobas.approval.IApprovalProcessStepCondition {

	ValueMode propertyValueMode = ValueMode.PROPERTY;

	public ValueMode getPropertyValueMode() {
		return propertyValueMode;
	}

	public void setPropertyValueMode(ValueMode propertyValueMode) {
		this.propertyValueMode = propertyValueMode;
	}

	String propertyName;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	emConditionOperation operation;

	public emConditionOperation getOperation() {
		return operation;
	}

	public void setOperation(emConditionOperation operation) {
		this.operation = operation;
	}

	emConditionRelationship relation;

	public emConditionRelationship getRelation() {
		return relation;
	}

	public void setRelation(emConditionRelationship relation) {
		this.relation = relation;
	}

	ValueMode conditionValueMode = ValueMode.INPUT;

	public ValueMode getConditionValueMode() {
		return conditionValueMode;
	}

	public void setConditionValueMode(ValueMode conditionValueMode) {
		this.conditionValueMode = conditionValueMode;
	}

	String conditionValue;

	public String getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

	int bracketOpen;

	public int getBracketOpen() {
		return bracketOpen;
	}

	public void setBracketOpen(int bracketOpen) {
		this.bracketOpen = bracketOpen;
	}

	int bracketClose;

	public int getBracketClose() {
		return bracketClose;
	}

	public void setBracketClose(int bracketClose) {
		this.bracketClose = bracketClose;
	}

}