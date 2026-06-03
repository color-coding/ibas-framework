package org.colorcoding.ibas.demo.test;

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
import org.colorcoding.ibas.bobas.db.SqlStatement;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;

import junit.framework.TestCase;

/**
 * 数据库适配器（DbAdapter）SQL语句生成测试
 *
 * 测试范围：
 * 1. INSERT语句生成（MSSQL/MySQL）
 * 2. DELETE语句生成（MSSQL）
 * 3. UPDATE语句生成（MSSQL）
 * 4. SELECT语句生成（MSSQL/MySQL，含条件/排序）
 */
public class TestDbAdapter extends TestCase {

	/**
	 * 创建标准测试用SalesOrder
	 * 包含：主键、客户信息、状态、日期、金额、特殊字符引用
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
	 * 创建标准测试用Criteria
	 * 包含：括号条件、多种操作符、排序
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
		return criteria;
	}

	// ==================== 1. INSERT语句生成 ====================

	/**
	 * 测试INSERT语句生成
	 * 覆盖：MSSQL和MySQL两种适配器的INSERT伪代码与真实SQL
	 */
	public void testInsertSql() {
		SalesOrder order = createTestOrder();

		DbAdapter adapter;
		SqlStatement sqlStatement;

		// MSSQL
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

		// MySQL
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

	// ==================== 2. DELETE语句生成 ====================

	/**
	 * 测试DELETE语句生成
	 * 覆盖：MSSQL适配器的DELETE伪代码与真实SQL，仅映射主键
	 */
	public void testDeleteSql() {
		SalesOrder order = createTestOrder();

		DbAdapter adapter;
		SqlStatement sqlStatement;

		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		sqlStatement = new SqlStatement(adapter.parsingDelete(order));
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
	 * 测试UPDATE语句生成
	 * 覆盖：MSSQL适配器的UPDATE伪代码与真实SQL，非主键字段+主键条件
	 */
	public void testUpdateSql() {
		SalesOrder order = createTestOrder();

		DbAdapter adapter;
		SqlStatement sqlStatement;

		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		sqlStatement = new SqlStatement(adapter.parsingUpdate(order));
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
	 * 测试SELECT语句生成
	 * 覆盖：MSSQL和MySQL两种适配器，含括号条件、多种操作符、排序
	 */
	public void testSelectSql() {
		ICriteria criteria = createTestCriteria();

		DbAdapter adapter;
		SqlStatement sqlStatement;

		// MSSQL
		adapter = new org.colorcoding.ibas.bobas.db.mssql.DbAdapter();
		criteria = adapter.convert(criteria, SalesOrder.class);
		sqlStatement = new SqlStatement(adapter.parsingSelect(SalesOrder.class, criteria));
		System.out.println(adapter.getClass().getName().substring(0, adapter.getClass().getName().lastIndexOf(".")));
		System.out.println("pseudo code:");
		System.out.println(sqlStatement.getContent());
		sqlStatement.setObject(criteria.getConditions());
		System.out.println("real code:");
		System.out.println(adapter.parsing(sqlStatement));

		// MySQL
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
