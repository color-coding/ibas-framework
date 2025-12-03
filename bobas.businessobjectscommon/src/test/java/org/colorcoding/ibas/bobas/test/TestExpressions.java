package org.colorcoding.ibas.bobas.test;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.expression.BOJudgmentLinkCondition;
import org.colorcoding.ibas.bobas.expression.IJudgmentExpression;
import org.colorcoding.ibas.bobas.expression.JudgmentExpressionBoolean;
import org.colorcoding.ibas.bobas.expression.JudgmentExpressionDateTime;
import org.colorcoding.ibas.bobas.expression.JudgmentExpressionDecimal;
import org.colorcoding.ibas.bobas.expression.JudgmentExpressionInteger;
import org.colorcoding.ibas.bobas.expression.JudgmentExpressionString;
import org.colorcoding.ibas.bobas.expression.JudmentOperation;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;

import junit.framework.TestCase;

public class TestExpressions extends TestCase {

	public void testJudgmentExpressions() {
		IJudgmentExpression judgment;
		// 布尔
		judgment = new JudgmentExpressionBoolean();
		judgment.setLeftValue(true);
		judgment.setRightValue(true);
		judgment.setOperation(JudmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setOperation(JudmentOperation.AND);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setRightValue(false);
		judgment.setOperation(JudmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setRightValue(false);
		judgment.setOperation(JudmentOperation.OR);
		assertEquals("expression is not established. ", judgment.result(), true);
		// 日期
		JudgmentExpressionDateTime judgmentDateTime = new JudgmentExpressionDateTime();
		judgmentDateTime.setLeftValue(DateTimes.valueOf("2016-03-15"));
		judgmentDateTime.setRightValue(DateTimes.valueOf("2016-03-15"));
		judgmentDateTime.setOperation(JudmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		judgmentDateTime.setRightValue(DateTimes.valueOf("2016-03-16"));
		judgmentDateTime.setOperation(JudmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		judgmentDateTime.setOperation(JudmentOperation.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		// 字符串
		JudgmentExpressionString judgmentString = new JudgmentExpressionString();
		judgmentString.setLeftValue("Aabbcc");
		judgmentString.setRightValue("Aabbcc");
		judgmentString.setOperation(JudmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aabb");
		judgmentString.setOperation(JudmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setOperation(JudmentOperation.GRATER_EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setOperation(JudmentOperation.CONTAIN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aa");
		judgmentString.setOperation(JudmentOperation.BEGIN_WITH);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("cc");
		judgmentString.setOperation(JudmentOperation.END_WITH);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("b");
		judgmentString.setOperation(JudmentOperation.CONTAIN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aabbcc, Aabbcd");
		judgmentString.setOperation(JudmentOperation.IN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		// 数值
		JudgmentExpressionInteger judgmentInteger = new JudgmentExpressionInteger();
		judgmentInteger.setLeftValue(100);
		judgmentInteger.setRightValue(100);
		judgmentInteger.setOperation(JudmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		judgmentInteger.setRightValue(101);
		judgmentInteger.setOperation(JudmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		judgmentInteger.setOperation(JudmentOperation.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		// 十进制数值
		JudgmentExpressionDecimal judgmentDecimal = new JudgmentExpressionDecimal();
		judgmentDecimal.setLeftValue(Decimals.valueOf("1000.999"));
		judgmentDecimal.setRightValue(Decimals.valueOf("1000.999"));
		judgmentDecimal.setOperation(JudmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);
		judgmentDecimal.setRightValue(Decimals.valueOf("1000.99901"));
		judgmentDecimal.setOperation(JudmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);
		judgmentDecimal.setOperation(JudmentOperation.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);

	}

	public void testJudgmentLinks() throws JudmentOperationException {
		// 查询条件
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpen(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.PLANNED);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.FINISHED);
		condition.setRelationship(ConditionRelationship.OR);
		// and (("DocDueDate" = "2016-03-15" or "DocDueDate" = "2016-03-17")
		// and (("DocTotal" >= 10000 and "DocTotal" >= 10000)))
		condition = criteria.getConditions().create();
		condition.setBracketOpen(2);
		condition.setAlias(SalesOrder.PROPERTY_DELIVERYDATE.getName());
		condition.setValue("2016-03-15");
		condition.setRelationship(ConditionRelationship.AND);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias(SalesOrder.PROPERTY_DELIVERYDATE.getName());
		condition.setValue("2016-03-17");
		condition.setRelationship(ConditionRelationship.OR);
		condition = criteria.getConditions().create();
		condition.setBracketOpen(2);
		condition.setBracketClose(0);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		condition.setValue(10000);
		condition.setOperation(ConditionOperation.GRATER_EQUAL);
		condition.setRelationship(ConditionRelationship.AND);
		condition = criteria.getConditions().create();
		condition.setBracketClose(3);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		condition.setValue(10000);
		condition.setOperation(ConditionOperation.GRATER_EQUAL);
		condition.setRelationship(ConditionRelationship.AND);
		// and "CustomerCode" = "C00001" and "DataOwner" <> 1
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		condition.setValue("C00001");
		condition.setRelationship(ConditionRelationship.AND);
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DATAOWNER.getName());
		condition.setValue(1);
		condition.setOperation(ConditionOperation.NOT_EQUAL);
		condition.setRelationship(ConditionRelationship.AND);
		// 数据
		ArrayList<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentStatus(emDocumentStatus.FINISHED);
		salesOrder.setDeliveryDate(DateTimes.valueOf("2016-03-15"));
		salesOrder.setDocumentTotal(Decimals.valueOf(20000));
		salesOrder.setCustomerCode("C00001");
		salesOrder.setDataOwner(2);
		salesOrders.add(salesOrder);

		salesOrder = (SalesOrder) salesOrder.clone();
		salesOrder.setDocEntry(2);
		salesOrder.setDeliveryDate(DateTimes.valueOf("2016-03-16"));
		salesOrders.add(salesOrder);

		salesOrder = (SalesOrder) salesOrder.clone();
		salesOrder.setDocEntry(3);
		salesOrder.setDeliveryDate(DateTimes.valueOf("2016-03-17"));
		salesOrder.setDocumentTotal(Decimals.valueOf(30000));
		salesOrders.add(salesOrder);

		salesOrder = (SalesOrder) salesOrder.clone();
		salesOrder.setDocEntry(4);
		salesOrder.setDocumentTotal(Decimals.valueOf(200));
		salesOrders.add(salesOrder);

		salesOrder = (SalesOrder) salesOrder.clone();
		salesOrder.setDocEntry(5);
		salesOrder.setDocumentStatus(emDocumentStatus.PLANNED);
		salesOrders.add(salesOrder);

		// OK Order Docentry： 1，3，
		BOJudgmentLinkCondition judgmentLinks = new BOJudgmentLinkCondition();
		judgmentLinks.parsingConditions(criteria.getConditions());
		for (SalesOrder item : salesOrders) {
			boolean ok = judgmentLinks.judge(item);
			System.out.println(String.format("judged bo: %s is %s", item.toString(), ok));
		}
	}
}
