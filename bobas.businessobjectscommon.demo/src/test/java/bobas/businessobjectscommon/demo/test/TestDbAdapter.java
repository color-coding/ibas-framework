package bobas.businessobjectscommon.demo.test;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.db.DbAdapter;
import org.colorcoding.ibas.bobas.db.SqlStatement;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;

import junit.framework.TestCase;

public class TestDbAdapter extends TestCase {

	public void testInsertSql() {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(2025000001);
		order.setCustomerCode("'C00001");
		order.setCustomerName("宇宙无敌影业");
		order.setDocumentStatus(emDocumentStatus.FINISHED);
		order.setDeliveryDate(DateTimes.valueOf("2025-06-01"));
		order.setDocumentTotal(Decimals.valueOf("199.99"));
		order.setReference1("1. 返现；\r\n2. 加急");

		DbAdapter adapter;
		SqlStatement sqlStatement;

		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		sqlStatement = new SqlStatement(adapter.parsingInsert(order));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(SalesOrder.class)) {
			sqlStatement.setObject(propertyInfo.getIndex(), order.getProperty(propertyInfo));
		}
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));
		System.out.println();

		adapter = new org.colorcoding.ibas.bobas.db.mysql.DbAdapter();
		sqlStatement = new SqlStatement(adapter.parsingInsert(order));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(SalesOrder.class)) {
			sqlStatement.setObject(propertyInfo.getIndex(), order.getProperty(propertyInfo));
		}
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));
		System.out.println();

	}

	public void testSelectSql() {
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
		condition.setValue(emDocumentStatus.RELEASED);
		condition.setRelationship(ConditionRelationship.OR);
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		condition.setOperation(ConditionOperation.GRATER_EQUAL);
		condition.setValue(100);
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTTOTAL.getName());
		condition.setOperation(ConditionOperation.LESS_THAN);
		condition.setValue(Decimals.valueOf("199.99"));
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		condition.setOperation(ConditionOperation.NOT_NULL);
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_CUSTOMERNAME.getName());
		condition.setOperation(ConditionOperation.START);
		condition.setValue("C'");
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DELIVERYDATE.getName());
		condition.setOperation(ConditionOperation.EQUAL);
		condition.setValue(DateTimes.today());
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DELIVERYDATE.getName());
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("05");
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		sort.setSortType(SortType.ASCENDING);

		DbAdapter adapter;
		SqlStatement sqlStatement;

		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		criteria = adapter.convert(criteria, SalesOrder.class);
		sqlStatement = new SqlStatement(adapter.parsingSelect(SalesOrder.class, criteria));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		sqlStatement.setObject(criteria.getConditions());
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));

		adapter = new org.colorcoding.ibas.bobas.db.mysql.DbAdapter();
		criteria = adapter.convert(criteria, SalesOrder.class);
		sqlStatement = new SqlStatement(adapter.parsingSelect(SalesOrder.class, criteria));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		sqlStatement.setObject(criteria.getConditions());
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));
	}
}
