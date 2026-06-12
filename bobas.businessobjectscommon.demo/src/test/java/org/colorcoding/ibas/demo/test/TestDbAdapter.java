package org.colorcoding.ibas.demo.test;

import java.sql.SQLException;

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
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DataType;
import org.colorcoding.ibas.bobas.db.SqlPreparedStatement;
import org.colorcoding.ibas.bobas.db.SqlStoredProcedure;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;

import junit.framework.TestCase;

/**
 * 数据库适配器（DbAdapter）SQL语句生成测试
 *
 * 测试范围： 1. INSERT语句生成（MSSQL/MySQL） 2. DELETE语句生成（MSSQL） 3. UPDATE语句生成（MSSQL） 4.
 * SELECT语句生成（MSSQL/MySQL，含条件/排序） 5. 存储过程语句生成（MSSQL/MySQL，含命名参数、不同类型值）
 */
public class TestDbAdapter extends TestCase {

	/**
	 * 创建标准测试用SalesOrder 包含：主键、客户信息、状态、日期、金额、特殊字符引用
	 */
	private SalesOrder createTestOrder() {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(2025000001);
		order.setCustomerCode("'C00001");
		order.setCustomerName("宇宙无敌影业");
		order.setDocumentStatus(emDocumentStatus.FINISHED);
		order.setDeliveryDate(DateTimes.valueOf("2025-06-01"));
		order.setDocumentTotal(Decimals.valueOf("199.99"));
		order.setReference1("1. 返现；\r\n2. 加急");
		return order;
	}

	/**
	 * 创建标准测试用Criteria 包含：括号条件、多种操作符、排序
	 */
	private ICriteria createTestCriteria() {
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
		condition.setOperation(ConditionOperation.GREATER_EQUAL);
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
		return criteria;
	}

	// ==================== 1. INSERT语句生成 ====================

	/**
	 * 测试INSERT语句生成 覆盖：MSSQL和MySQL两种适配器的INSERT伪代码与真实SQL
	 * 
	 * @throws SQLException
	 */
	public void testInsertSql() throws SQLException {
		SalesOrder order = createTestOrder();

		DbAdapter adapter;
		SqlPreparedStatement sqlStatement;

		// MSSQL
		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		sqlStatement = new SqlPreparedStatement(adapter.parsingInsert(order));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(SalesOrder.class)) {
			sqlStatement.setObject(propertyInfo.getIndex(), order.getProperty(propertyInfo));
		}
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));
		System.out.println();

		// MySQL
		adapter = new org.colorcoding.ibas.bobas.db.mysql.DbAdapter();
		sqlStatement = new SqlPreparedStatement(adapter.parsingInsert(order));
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

	// ==================== 2. DELETE语句生成 ====================

	/**
	 * 测试DELETE语句生成 覆盖：MSSQL适配器的DELETE伪代码与真实SQL，仅映射主键
	 * 
	 * @throws SQLException
	 */
	public void testDeleteSql() throws SQLException {
		SalesOrder order = createTestOrder();

		DbAdapter adapter;
		SqlPreparedStatement sqlStatement;

		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		sqlStatement = new SqlPreparedStatement(adapter.parsingDelete(order));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(SalesOrder.class).where(c -> c.isPrimaryKey())) {
			sqlStatement.setObject(propertyInfo.getIndex(), order.getProperty(propertyInfo));
		}
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));
		System.out.println();
	}

	// ==================== 3. UPDATE语句生成 ====================

	/**
	 * 测试UPDATE语句生成 覆盖：MSSQL适配器的UPDATE伪代码与真实SQL，非主键字段+主键条件
	 * 
	 * @throws SQLException
	 */
	public void testUpdateSql() throws SQLException {
		SalesOrder order = createTestOrder();

		DbAdapter adapter;
		SqlPreparedStatement sqlStatement;

		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		sqlStatement = new SqlPreparedStatement(adapter.parsingUpdate(order));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		int index = 0;
		for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(SalesOrder.class).where(c -> !c.isPrimaryKey())) {
			// 仅映射数据库属性
			if (propertyInfo.getAnnotation(DbField.class) == null) {
				continue;
			}
			sqlStatement.setObject(index, order.getProperty(propertyInfo));
			index++;
		}
		for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(SalesOrder.class).where(c -> c.isPrimaryKey())) {
			// 仅映射数据库属性
			if (propertyInfo.getAnnotation(DbField.class) == null) {
				continue;
			}
			sqlStatement.setObject(index, order.getProperty(propertyInfo));
			index++;
		}
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));
		System.out.println();
	}

	// ==================== 4. SELECT语句生成 ====================

	/**
	 * 测试SELECT语句生成 覆盖：MSSQL和MySQL两种适配器，含括号条件、多种操作符、排序
	 * 
	 * @throws SQLException
	 */
	public void testSelectSql() throws SQLException {
		ICriteria criteria = createTestCriteria();

		DbAdapter adapter;
		SqlPreparedStatement sqlStatement;

		// MSSQL
		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		criteria = adapter.convert(criteria, SalesOrder.class);
		sqlStatement = new SqlPreparedStatement(adapter.parsingSelect(SalesOrder.class, criteria));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		sqlStatement.setObject(criteria.getConditions());
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));

		// MySQL
		adapter = new org.colorcoding.ibas.bobas.db.mysql.DbAdapter();
		criteria = adapter.convert(criteria, SalesOrder.class);
		sqlStatement = new SqlPreparedStatement(adapter.parsingSelect(SalesOrder.class, criteria));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		sqlStatement.setObject(criteria.getConditions());
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));
	}

	// ==================== 5. 存储过程语句生成 ====================

	/**
	 * 测试存储过程语句生成 覆盖：MSSQL和MySQL两种适配器，含命名参数、多种数据类型（字符串/数值/日期/枚举/空值）
	 */
	public void testStoredProcedureSql() {
		DbAdapter adapter;

		// MSSQL
		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));

		SqlStoredProcedure sp = new SqlStoredProcedure("CC_SP_SALESORDER_NOTIFY");
		sp.setObject("ObjectType", "CC_TT_SALESORDER", DataType.ALPHANUMERIC);
		sp.setObject("TransType", emDocumentStatus.RELEASED, DataType.ALPHANUMERIC);
		sp.setObject("DocEntry", 2025000001, DataType.NUMERIC);
		sp.setObject("DocTotal", Decimals.valueOf("199.99"), DataType.DECIMAL);
		sp.setObject("DocDate", DateTimes.valueOf("2025-06-01"), DataType.DATE);
		sp.setObject("Remarks", null, DataType.ALPHANUMERIC);
		System.out.println("pseudo code:");
		System.out.println(sp.getContent());
		System.out.println("real code:");
		System.out.println(adapter.parsing(sp));
		System.out.println();

		// MySQL
		adapter = new org.colorcoding.ibas.bobas.db.mysql.DbAdapter();
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));

		sp = new SqlStoredProcedure("CC_SP_SALESORDER_NOTIFY");
		sp.setObject("ObjectType", "CC_TT_SALESORDER", DataType.ALPHANUMERIC);
		sp.setObject("TransType", emDocumentStatus.RELEASED, DataType.ALPHANUMERIC);
		sp.setObject("DocEntry", 2025000001, DataType.NUMERIC);
		sp.setObject("DocTotal", Decimals.valueOf("199.99"), DataType.DECIMAL);
		sp.setObject("DocDate", DateTimes.valueOf("2025-06-01"), DataType.DATE);
		sp.setObject("Remarks", null, DataType.ALPHANUMERIC);
		System.out.println("pseudo code:");
		System.out.println(sp.getContent());
		System.out.println("real code:");
		System.out.println(adapter.parsing(sp));
		System.out.println();

		// 含特殊字符的参数值（单引号、中文）
		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		sp = new SqlStoredProcedure("CC_SP_TEST_SPECIAL");
		sp.setObject("Name", "宇宙无敌'影业", DataType.ALPHANUMERIC);
		sp.setObject("Code", "C'001", DataType.ALPHANUMERIC);
		System.out.println("special chars (MSSQL):");
		System.out.println(adapter.parsing(sp));
	}
}
