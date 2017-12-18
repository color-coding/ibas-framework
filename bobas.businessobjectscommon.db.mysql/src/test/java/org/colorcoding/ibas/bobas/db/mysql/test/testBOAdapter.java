
package org.colorcoding.ibas.bobas.db.mysql.test;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.ISqlStoredProcedure;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.SqlStoredProcedure;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.ParsingException;
import org.colorcoding.ibas.bobas.db.mysql.BOAdapter;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

import junit.framework.TestCase;

public class testBOAdapter extends TestCase {

	public testBOAdapter() {

	}

	public void testCriteria() throws ParsingException {

		String sqlString = "SELECT * FROM `CC_TT_ORDR` WHERE (`DocStatus` = N'P' OR `DocStatus` = N'F') AND `CardCode` IS NOT NULL AND CAST(`DocEntry` AS CHAR) LIKE N'2%' AND `DocEntry` > 2000 AND `DocEntry` <> CAST(`B1DocEntry` AS SIGNED) ORDER BY `DocEntry` DESC, `CardCode` ASC LIMIT 100";
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
		// AND "CardCode" IS NOT NULL AND "DocEntry" LIKE "2%"
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		condition.setOperation(ConditionOperation.NOT_NULL);
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		condition.setOperation(ConditionOperation.START);
		condition.setValue("2");
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		condition.setOperation(ConditionOperation.GRATER_THAN);
		condition.setValue("2000");
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		condition.setOperation(ConditionOperation.NOT_EQUAL);
		condition.setComparedAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		sort.setSortType(SortType.ASCENDING);

		IBOAdapter4Db boAdapter = new BOAdapter();
		ISqlQuery sqlQuery = boAdapter.parseSqlQuery(criteria, SalesOrder.class);
		System.out.println(sqlString);
		System.out.println(sqlQuery.getQueryString());
		assertEquals(sqlString, sqlQuery.getQueryString());
	}

	public void testInsertUpdateDelete() throws ParsingException {
		ISalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setCycle(new Time(1.05, emTimeUnit.HOUR));

		order.getUserFields().register("U_OrderType", DbFieldType.ALPHANUMERIC);
		order.getUserFields().register("U_OrderId", DbFieldType.NUMERIC);
		order.getUserFields().register("U_OrderDate", DbFieldType.DATE);
		order.getUserFields().register("U_OrderTotal", DbFieldType.DECIMAL);

		order.getUserFields().setValue("U_OrderType", "S0000");
		order.getUserFields().setValue("U_OrderId", 5768);
		order.getUserFields().setValue("U_OrderDate", DateTime.getToday());
		order.getUserFields().setValue("U_OrderTotal", new Decimal("999.888"));

		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");

		IBOAdapter4Db boAdapter = new BOAdapter();
		ISqlQuery sqlQuery = boAdapter.parseInsertScript(order);
		System.out.println(sqlQuery.getQueryString());

		sqlQuery = boAdapter.parseDeleteScript(order);
		System.out.println(sqlQuery.getQueryString());

		for (ISalesOrderItem item : order.getSalesOrderItems()) {
			sqlQuery = boAdapter.parseInsertScript(item);
			System.out.println(sqlQuery.getQueryString());

			sqlQuery = boAdapter.parseDeleteScript(item);
			System.out.println(sqlQuery.getQueryString());
		}
	}

	public void testStoredProcedure() throws ParsingException {
		ISqlStoredProcedure sqlStoredProcedure = new SqlStoredProcedure();
		sqlStoredProcedure.setName("CC_SP_TRANSACTION_NOTIFICATION");
		sqlStoredProcedure.addParameters("", "A");
		sqlStoredProcedure.addParameters("", "B");
		sqlStoredProcedure.addParameters("", 9);
		sqlStoredProcedure.addParameters("", "C");
		sqlStoredProcedure.addParameters("", "D");
		IBOAdapter4Db boAdapter = new BOAdapter();
		ISqlQuery sqlQuery = boAdapter.parseSqlQuery(sqlStoredProcedure);
		System.out.println(sqlQuery.getQueryString());
	}
}