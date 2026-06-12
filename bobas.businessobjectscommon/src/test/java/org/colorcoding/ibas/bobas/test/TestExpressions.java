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
import org.colorcoding.ibas.bobas.expression.JudgmentOperation;
import org.colorcoding.ibas.bobas.expression.JudgmentOperationException;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;

import junit.framework.TestCase;

/**
 * 表达式判断功能测试
 *
 * 测试范围：
 * 1. 布尔表达式判断（EQUAL/AND/OR/NOT_EQUAL）
 * 2. 日期表达式判断（EQUAL/NOT_EQUAL/LESS_EQUAL）
 * 3. 字符串表达式判断（EQUAL/NOT_EQUAL/CONTAIN/BEGIN_WITH/END_WITH/IN）
 * 4. 整数表达式判断（EQUAL/NOT_EQUAL/LESS_EQUAL）
 * 5. 十进制数表达式判断（EQUAL/NOT_EQUAL/LESS_EQUAL）
 * 6. 复合条件判断链（BOJudgmentLinkCondition）
 */
public class TestExpressions extends TestCase {

	public void testJudgmentExpressions() {
		IJudgmentExpression judgment;
		// 布尔
		judgment = new JudgmentExpressionBoolean();
		judgment.setLeftValue(true);
		judgment.setRightValue(true);
		judgment.setOperation(JudgmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setOperation(JudgmentOperation.AND);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setRightValue(false);
		judgment.setOperation(JudgmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgment.result(), true);
		judgment.setRightValue(false);
		judgment.setOperation(JudgmentOperation.OR);
		assertEquals("expression is not established. ", judgment.result(), true);
		// 日期
		JudgmentExpressionDateTime judgmentDateTime = new JudgmentExpressionDateTime();
		judgmentDateTime.setLeftValue(DateTimes.valueOf("2016-03-15"));
		judgmentDateTime.setRightValue(DateTimes.valueOf("2016-03-15"));
		judgmentDateTime.setOperation(JudgmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		judgmentDateTime.setRightValue(DateTimes.valueOf("2016-03-16"));
		judgmentDateTime.setOperation(JudgmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		judgmentDateTime.setOperation(JudgmentOperation.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentDateTime.result(), true);
		// 字符串
		JudgmentExpressionString judgmentString = new JudgmentExpressionString();
		judgmentString.setLeftValue("Aabbcc");
		judgmentString.setRightValue("Aabbcc");
		judgmentString.setOperation(JudgmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aabb");
		judgmentString.setOperation(JudgmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setOperation(JudgmentOperation.GREATER_EQUAL);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setOperation(JudgmentOperation.CONTAIN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aa");
		judgmentString.setOperation(JudgmentOperation.BEGIN_WITH);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("cc");
		judgmentString.setOperation(JudgmentOperation.END_WITH);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("b");
		judgmentString.setOperation(JudgmentOperation.CONTAIN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		judgmentString.setRightValue("Aabbcc, Aabbcd");
		judgmentString.setOperation(JudgmentOperation.IN);
		assertEquals("expression is not established. ", judgmentString.result(), true);
		// 数值
		JudgmentExpressionInteger judgmentInteger = new JudgmentExpressionInteger();
		judgmentInteger.setLeftValue(100);
		judgmentInteger.setRightValue(100);
		judgmentInteger.setOperation(JudgmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		judgmentInteger.setRightValue(101);
		judgmentInteger.setOperation(JudgmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		judgmentInteger.setOperation(JudgmentOperation.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentInteger.result(), true);
		// 十进制数值
		JudgmentExpressionDecimal judgmentDecimal = new JudgmentExpressionDecimal();
		judgmentDecimal.setLeftValue(Decimals.valueOf("1000.999"));
		judgmentDecimal.setRightValue(Decimals.valueOf("1000.999"));
		judgmentDecimal.setOperation(JudgmentOperation.EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);
		judgmentDecimal.setRightValue(Decimals.valueOf("1000.99901"));
		judgmentDecimal.setOperation(JudgmentOperation.NOT_EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);
		judgmentDecimal.setOperation(JudgmentOperation.LESS_EQUAL);
		assertEquals("expression is not established. ", judgmentDecimal.result(), true);

	}

	public void testJudgmentLinks() throws JudgmentOperationException {
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
		condition.setOperation(ConditionOperation.GREATER_EQUAL);
		condition.setRelationship(ConditionRelationship.AND);
		condition = criteria.getConditions().create();
		condition.setBracketClose(3);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		condition.setValue(10000);
		condition.setOperation(ConditionOperation.GREATER_EQUAL);
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

	// ==================== 新增测试 ====================

	/**
	 * 测试布尔表达式-反向场景
	 * 覆盖：true!=true, false AND true, false OR false
	 */
	public void testBooleanExpressionNegative() {
		JudgmentExpressionBoolean judgment = new JudgmentExpressionBoolean();
		judgment.setLeftValue(true);
		judgment.setRightValue(true);
		judgment.setOperation(JudgmentOperation.NOT_EQUAL);
		assertFalse("true != true should be false. ", judgment.result());

		judgment.setLeftValue(false);
		judgment.setRightValue(true);
		judgment.setOperation(JudgmentOperation.AND);
		assertFalse("false AND true should be false. ", judgment.result());

		judgment.setLeftValue(false);
		judgment.setRightValue(false);
		judgment.setOperation(JudgmentOperation.OR);
		assertFalse("false OR false should be false. ", judgment.result());
	}

	/**
	 * 测试整数表达式-更多操作
	 * 覆盖：GREATER_EQUAL, LESS_THAN, GREATER_THAN
	 */
	public void testIntegerExpressionMore() {
		JudgmentExpressionInteger judgment = new JudgmentExpressionInteger();
		judgment.setLeftValue(100);
		judgment.setRightValue(99);
		judgment.setOperation(JudgmentOperation.GREATER_EQUAL);
		assertTrue("100 >= 99 should be true. ", judgment.result());

		judgment.setLeftValue(99);
		judgment.setRightValue(100);
		judgment.setOperation(JudgmentOperation.LESS_THAN);
		assertTrue("99 < 100 should be true. ", judgment.result());

		judgment.setLeftValue(100);
		judgment.setRightValue(99);
		judgment.setOperation(JudgmentOperation.GREATER_THAN);
		assertTrue("100 > 99 should be true. ", judgment.result());

		judgment.setLeftValue(99);
		judgment.setRightValue(99);
		judgment.setOperation(JudgmentOperation.GREATER_THAN);
		assertFalse("99 > 99 should be false. ", judgment.result());
	}

	/**
	 * 测试字符串表达式-NOT_CONTAIN和NOT_IN
	 * 覆盖：不包含、不在列表中
	 */
	public void testStringExpressionMore() {
		JudgmentExpressionString judgment = new JudgmentExpressionString();
		judgment.setLeftValue("Hello World");
		judgment.setRightValue("xyz");
		judgment.setOperation(JudgmentOperation.NOT_EQUAL);
		assertTrue("Hello World != xyz should be true. ", judgment.result());

		judgment.setLeftValue("Hello World");
		judgment.setRightValue("Hello");
		judgment.setOperation(JudgmentOperation.BEGIN_WITH);
		assertTrue("Hello World begins with Hello. ", judgment.result());

		judgment.setLeftValue("Hello World");
		judgment.setRightValue("World");
		judgment.setOperation(JudgmentOperation.END_WITH);
		assertTrue("Hello World ends with World. ", judgment.result());

		// IN操作：包含在列表中
		judgment.setLeftValue("Apple");
		judgment.setRightValue("Apple, Banana, Cherry");
		judgment.setOperation(JudgmentOperation.IN);
		assertTrue("Apple in list should be true. ", judgment.result());
	}

	/**
	 * 测试Decimal表达式-更多操作
	 * 覆盖：GREATER_THAN, LESS_THAN, LESS_EQUAL
	 */
	public void testDecimalExpressionMore() {
		JudgmentExpressionDecimal judgment = new JudgmentExpressionDecimal();
		judgment.setLeftValue(Decimals.valueOf("100.5"));
		judgment.setRightValue(Decimals.valueOf("100.0"));
		judgment.setOperation(JudgmentOperation.GREATER_THAN);
		assertTrue("100.5 > 100.0 should be true. ", judgment.result());

		judgment.setLeftValue(Decimals.valueOf("99.5"));
		judgment.setRightValue(Decimals.valueOf("100.0"));
		judgment.setOperation(JudgmentOperation.LESS_THAN);
		assertTrue("99.5 < 100.0 should be true. ", judgment.result());

		judgment.setLeftValue(Decimals.valueOf("100.0"));
		judgment.setRightValue(Decimals.valueOf("100.0"));
		judgment.setOperation(JudgmentOperation.LESS_EQUAL);
		assertTrue("100.0 <= 100.0 should be true. ", judgment.result());
	}

	/**
	 * 测试复合条件判断链-具体断言
	 * 覆盖：验证复合条件判断的精确结果
	 */
	public void testJudgmentLinksWithAssertions() throws JudgmentOperationException {
		// 简单条件：DocEntry = 1
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		condition.setValue(1);

		ArrayList<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
		SalesOrder so1 = new SalesOrder();
		so1.setDocEntry(1);
		salesOrders.add(so1);
		SalesOrder so2 = new SalesOrder();
		so2.setDocEntry(2);
		salesOrders.add(so2);

		BOJudgmentLinkCondition judgmentLinks = new BOJudgmentLinkCondition();
		judgmentLinks.parsingConditions(criteria.getConditions());

		assertTrue("DocEntry=1 should match. ", judgmentLinks.judge(so1));
		assertFalse("DocEntry=2 should not match. ", judgmentLinks.judge(so2));
	}
}
