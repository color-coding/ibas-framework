package org.colorcoding.ibas.bobas.db.mssql.test;

import java.util.ArrayList;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessStep;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessStepCondition;
import org.colorcoding.ibas.bobas.approval.UnauthorizedException;
import org.colorcoding.ibas.bobas.approval.ValueMode;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.repository.BORepository4Db;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

import junit.framework.TestCase;

public class testApprovalProcess extends TestCase {

	public void testUseApprovalProcess() {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
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
		aProcess.setRepository(new BORepository4Db("Master"));
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
		// sql查询比较
		bCondition = new ApprovalProcessStepCondition();
		bCondition.setRelation(emConditionRelationship.AND);
		bCondition.setPropertyName("DocTotal");// DocumentTotal
		bCondition.setOperation(emConditionOperation.GRATER_EQUAL);
		bCondition.setConditionValueMode(ValueMode.SQL_SCRIPT);
		bCondition.setConditionValue(
				String.format("select 0 from %s where CardCode = '${CardCode}'", SalesOrder.DB_TABLE_NAME));
		aStep.setConditions(new IApprovalProcessStepCondition[] { aCondition, bCondition });
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

	}

}

class ApprovalProcess extends org.colorcoding.ibas.bobas.approval.ApprovalProcess {

	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	private emApprovalStatus status;

	@Override
	public emApprovalStatus getStatus() {
		return this.status;
	}

	@Override
	protected void setStatus(emApprovalStatus value) {
		this.status = value;
	}

	private DateTime startedTime;

	@Override
	public DateTime getStartedTime() {
		return this.startedTime;
	}

	@Override
	protected void setStartedTime(DateTime value) {
		this.startedTime = value;
	}

	private DateTime finishedTime;

	@Override
	public DateTime getFinishedTime() {
		return this.finishedTime;
	}

	@Override
	protected void setFinishedTime(DateTime value) {
		this.finishedTime = value;
	}

	private IApprovalData approvalData;

	@Override
	public IApprovalData getApprovalData() {
		return this.approvalData;
	}

	@Override
	public void setApprovalData(IApprovalData value) {
		this.approvalData = value;
	}

	private IUser owner;

	@Override
	public IUser getOwner() {
		return this.owner;
	}

	public void setOwner(IUser value) {
		this.owner = value;
	}

	private IApprovalProcessStep[] processSteps;

	@Override
	public IApprovalProcessStep[] getProcessSteps() {
		return this.processSteps;
	}

	public void setProcessSteps(IApprovalProcessStep[] value) {
		this.processSteps = value;
	}

	@Override
	public void checkToSave(IUser user) throws UnauthorizedException {

	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	protected void saveProcess() throws Exception {

	}

}

class ApprovalProcessStep extends org.colorcoding.ibas.bobas.approval.ApprovalProcessStep {

	private int id;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	protected void setId(int value) {
		this.id = value;
	}

	private IApprovalProcessStepCondition[] conditions;

	@Override
	public IApprovalProcessStepCondition[] getConditions() {
		return this.conditions;
	}

	protected void setConditions(IApprovalProcessStepCondition[] value) {
		this.conditions = value;
	}

	private DateTime startedTime;

	@Override
	public DateTime getStartedTime() {
		return this.startedTime;
	}

	@Override
	protected void setStartedTime(DateTime value) {
		this.startedTime = value;
	}

	private DateTime finishedTime;

	@Override
	public DateTime getFinishedTime() {
		return this.finishedTime;
	}

	@Override
	protected void setFinishedTime(DateTime value) {
		this.finishedTime = value;
	}

	private IUser owner;

	@Override
	public IUser getOwner() {
		return this.owner;
	}

	protected void setOwner(IUser value) {
		this.owner = value;
	}

	private String judgment;

	@Override
	public String getJudgment() {
		return this.judgment;
	}

	@Override
	protected void setJudgment(String value) {
		this.judgment = value;
	}

	private emApprovalStepStatus status;

	@Override
	public emApprovalStepStatus getStatus() {
		return this.status;
	}

	@Override
	protected void setStatus(emApprovalStepStatus value) {
		this.status = value;
	}

}

class ApprovalProcessStepCondition implements IApprovalProcessStepCondition {
	public ApprovalProcessStepCondition() {
		this.setOperation(emConditionOperation.EQUAL);
		this.setRelation(emConditionRelationship.AND);
	}

	private ValueMode propertyValueMode = ValueMode.DB_FIELD;

	@Override
	public ValueMode getPropertyValueMode() {
		return this.propertyValueMode;
	}

	public void setPropertyValueMode(ValueMode value) {
		this.propertyValueMode = value;
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

	private ValueMode conditionValueMode = ValueMode.INPUT;

	@Override
	public ValueMode getConditionValueMode() {
		return this.conditionValueMode;
	}

	public void setConditionValueMode(ValueMode value) {
		this.conditionValueMode = value;
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
