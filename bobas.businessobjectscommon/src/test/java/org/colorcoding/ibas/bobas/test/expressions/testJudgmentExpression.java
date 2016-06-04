package org.colorcoding.ibas.bobas.test.expressions;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.expressions.BOJudgmentLinksCondition;
import org.colorcoding.ibas.bobas.expressions.IJudgmentExpression;
import org.colorcoding.ibas.bobas.expressions.JudgmentExpressionBoolean;
import org.colorcoding.ibas.bobas.expressions.JudgmentExpressionDateTime;
import org.colorcoding.ibas.bobas.expressions.JudgmentExpressionDecimal;
import org.colorcoding.ibas.bobas.expressions.JudgmentExpressionInteger;
import org.colorcoding.ibas.bobas.expressions.JudgmentExpressionString;
import org.colorcoding.ibas.bobas.expressions.JudmentOperationException;
import org.colorcoding.ibas.bobas.expressions.JudmentOperations;
import org.colorcoding.ibas.bobas.expressions.NotSupportOperationException;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.util.ArrayList;

import junit.framework.TestCase;

public class testJudgmentExpression extends TestCase {

	public void testJudgmentExpressions() throws NotSupportOperationException {
		IJudgmentExpression judgment;
		// 布尔
		judgment = new JudgmentExpressionBoolean();
		judgment.setLeftValue(true);
		judgment.setRightValue(true);
		judgment.setOperation(JudmentOperations.EQUAL);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setOperation(JudmentOperations.AND);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setRightValue(false);
		judgment.setOperation(JudmentOperations.NOT_EQUAL);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setRightValue(false);
		judgment.setOperation(JudmentOperations.OR);
		assertEquals("expression is not established. ", judgment.result(), true);
		// 日期
		JudgmentExpressionDateTime judgmentDateTime = new JudgmentExpressionDateTime();
		judgmentDateTime.setLeftValue("2016-03-15");
		judgmentDateTime.setRightValue("2016-03-15");
		judgmentDateTime.setOperation(JudmentOperations.EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		judgmentDateTime.setRightValue("2016-03-16");
		judgmentDateTime.setOperation(JudmentOperations.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		judgmentDateTime.setOperation(JudmentOperations.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		// 字符串
		JudgmentExpressionString judgmentString = new JudgmentExpressionString();
		judgmentString.setLeftValue("Aabbcc");
		judgmentString.setRightValue("Aabbcc");
		judgmentString.setOperation(JudmentOperations.EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aabb");
		judgmentString.setOperation(JudmentOperations.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setOperation(JudmentOperations.GRATER_EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setOperation(JudmentOperations.CONTAIN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aa");
		judgmentString.setOperation(JudmentOperations.BEGIN_WITH);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("cc");
		judgmentString.setOperation(JudmentOperations.END_WITH);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("b");
		judgmentString.setOperation(JudmentOperations.CONTAIN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		// 数值
		JudgmentExpressionInteger judgmentInteger = new JudgmentExpressionInteger();
		judgmentInteger.setLeftValue(100);
		judgmentInteger.setRightValue(100);
		judgmentInteger.setOperation(JudmentOperations.EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		judgmentInteger.setRightValue(101);
		judgmentInteger.setOperation(JudmentOperations.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		judgmentInteger.setOperation(JudmentOperations.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		// 十进制数值
		JudgmentExpressionDecimal judgmentDecimal = new JudgmentExpressionDecimal();
		judgmentDecimal.setLeftValue("1000.999");
		judgmentDecimal.setRightValue("1000.999");
		judgmentDecimal.setOperation(JudmentOperations.EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);
		judgmentDecimal.setRightValue("1000.99901");
		judgmentDecimal.setOperation(JudmentOperations.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);
		judgmentDecimal.setOperation(JudmentOperations.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);

	}

	public void testJudgmentLinks() throws JudmentOperationException {
		// 查询条件
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpenNum(1);
		condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
		condition.setCondVal(emDocumentStatus.Planned);
		condition = criteria.getConditions().create();
		condition.setBracketCloseNum(1);
		condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
		condition.setCondVal(emDocumentStatus.Finished);
		condition.setRelationship(ConditionRelationship.cr_OR);
		// and (("DocDueDate" = "2016-03-15" or "DocDueDate" = "2016-03-17")
		// and (("DocTotal" >= 10000 and "DocTotal" >= 10000)))
		condition = criteria.getConditions().create();
		condition.setBracketOpenNum(2);
		condition.setAlias(SalesOrder.DeliveryDateProperty.getName());
		condition.setCondVal("2016-03-15");
		condition.setRelationship(ConditionRelationship.cr_AND);
		condition = criteria.getConditions().create();
		condition.setBracketCloseNum(1);
		condition.setAlias(SalesOrder.DeliveryDateProperty.getName());
		condition.setCondVal("2016-03-17");
		condition.setRelationship(ConditionRelationship.cr_OR);
		condition = criteria.getConditions().create();
		condition.setBracketOpenNum(2);
		condition.setBracketCloseNum(0);
		condition.setAlias(SalesOrder.DocumentTotalProperty.getName());
		condition.setCondVal(10000);
		condition.setOperation(ConditionOperation.co_GRATER_EQUAL);
		condition.setRelationship(ConditionRelationship.cr_AND);
		condition = criteria.getConditions().create();
		condition.setBracketCloseNum(3);
		condition.setAlias(SalesOrder.DocumentTotalProperty.getName());
		condition.setCondVal(10000);
		condition.setOperation(ConditionOperation.co_GRATER_EQUAL);
		condition.setRelationship(ConditionRelationship.cr_AND);
		// and "CustomerCode" = "C00001" and "DataOwner" <> 1
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.CustomerCodeProperty.getName());
		condition.setCondVal("C00001");
		condition.setRelationship(ConditionRelationship.cr_AND);
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.DataOwnerProperty.getName());
		condition.setCondVal(1);
		condition.setOperation(ConditionOperation.co_NOT_EQUAL);
		condition.setRelationship(ConditionRelationship.cr_AND);
		// 数据
		ArrayList<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentStatus(emDocumentStatus.Finished);
		salesOrder.setDeliveryDate(DateTime.valueOf("2016-03-15"));
		salesOrder.setDocumentTotal(20000);
		salesOrder.setCustomerCode("C00001");
		salesOrder.setDataOwner(2);
		salesOrders.add(salesOrder);

		salesOrder = salesOrder.clone();
		salesOrder.setDocEntry(2);
		salesOrder.setDeliveryDate(DateTime.valueOf("2016-03-16"));
		salesOrders.add(salesOrder);

		salesOrder = salesOrder.clone();
		salesOrder.setDocEntry(3);
		salesOrder.setDeliveryDate(DateTime.valueOf("2016-03-17"));
		salesOrder.setDocumentTotal(30000);
		salesOrders.add(salesOrder);

		salesOrder = salesOrder.clone();
		salesOrder.setDocEntry(4);
		salesOrder.setDocumentTotal(200);
		salesOrders.add(salesOrder);

		salesOrder = salesOrder.clone();
		salesOrder.setDocEntry(5);
		salesOrder.setDocumentStatus(emDocumentStatus.Planned);
		salesOrders.add(salesOrder);

		// OK Order Docentry： 1，3，
		BOJudgmentLinksCondition judgmentLinks = new BOJudgmentLinksCondition();
		judgmentLinks.parsingConditions(criteria.getConditions());
		for (SalesOrder item : salesOrders) {
			boolean ok = judgmentLinks.judge(item);
			System.err.println(String.format("judged bo:%s is %s", item.toString(), ok));
		}
	}
}
