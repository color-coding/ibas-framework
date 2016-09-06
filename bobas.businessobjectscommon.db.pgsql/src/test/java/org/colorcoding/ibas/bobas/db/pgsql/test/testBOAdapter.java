package org.colorcoding.ibas.bobas.db.pgsql.test;

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
import org.colorcoding.ibas.bobas.db.BOParseException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.pgsql.BOAdapter;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

import junit.framework.TestCase;

/**
 * Unit test for simple Amp.
 */
public class testBOAdapter extends TestCase {

	public testBOAdapter() {

	}

	public void testCriteria() throws BOParseException {

		String sqlString = "SELECT * FROM \"CC_TT_ORDR\" WHERE (\"DocStatus\" = 'P' OR \"DocStatus\" = 'F') AND \"CardCode\" IS NOT NULL AND CAST(\"DocEntry\" AS VARCHAR) LIKE '2%' AND \"DocEntry\" > 2000 AND \"DocEntry\" != CAST(\"B1DocEntry\" AS INTEGER) ORDER BY \"DocEntry\" DESC,\"CardCode\" ASC LIMIT 100";

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
		// AND "CardCode" IS NOT NULL AND "DocEntry" LIKE "2%"
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.CustomerCodeProperty.getName());
		condition.setOperation(ConditionOperation.co_NOT_NULL);
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.DocEntryProperty.getName());
		condition.setOperation(ConditionOperation.co_START);
		condition.setCondVal("2");
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.DocEntryProperty.getName());
		condition.setOperation(ConditionOperation.co_GRATER_THAN);
		condition.setCondVal("2000");
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.DocEntryProperty.getName());
		condition.setOperation(ConditionOperation.co_NOT_EQUAL);
		condition.setComparedAlias(SalesOrder.DocEntryProperty.getName());
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.DocEntryProperty.getName());
		sort.setSortType(SortType.st_Descending);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.CustomerCodeProperty.getName());
		sort.setSortType(SortType.st_Ascending);

		IBOAdapter4Db boAdapter = new BOAdapter();
		ISqlQuery sqlQuery = boAdapter.parseSqlQuery(criteria, SalesOrder.class);
		System.out.println(sqlString);
		System.out.println(sqlQuery.getQueryString());
		assertEquals(sqlString, sqlQuery.getQueryString());
	}

	public void testInsertUpdateDelete() throws BOParseException {
		ISalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.Released);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setCycle(new Time(1.05, emTimeUnit.hour));

		order.getUserFields().addUserField("U_OrderType", DbFieldType.db_Alphanumeric);
		order.getUserFields().addUserField("U_OrderId", DbFieldType.db_Numeric);
		order.getUserFields().addUserField("U_OrderDate", DbFieldType.db_Date);
		order.getUserFields().addUserField("U_OrderTotal", DbFieldType.db_Decimal);

		order.getUserFields().setValue("U_OrderType", "S0000");
		order.getUserFields().setValue("U_OrderId", 5768);
		order.getUserFields().setValue("U_OrderDate", DateTime.getToday());
		order.getUserFields().setValue("U_OrderTotal", new Decimal("999.888"));

		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");

		IBOAdapter4Db boAdapter = new BOAdapter();
		ISqlQuery sqlQuery = boAdapter.parseSqlInsert(order);
		System.out.println(sqlQuery.getQueryString());

		sqlQuery = boAdapter.parseSqlUpdate(order);
		System.out.println(sqlQuery.getQueryString());

		sqlQuery = boAdapter.parseSqlDelete(order);
		System.out.println(sqlQuery.getQueryString());

		for (ISalesOrderItem item : order.getSalesOrderItems()) {
			sqlQuery = boAdapter.parseSqlInsert(item);
			System.out.println(sqlQuery.getQueryString());

			sqlQuery = boAdapter.parseSqlUpdate(item);
			System.out.println(sqlQuery.getQueryString());

			sqlQuery = boAdapter.parseSqlDelete(item);
			System.out.println(sqlQuery.getQueryString());
		}
	}

	public void testStoredProcedure() throws BOParseException {
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
