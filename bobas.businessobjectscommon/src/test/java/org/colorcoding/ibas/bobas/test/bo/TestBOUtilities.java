package org.colorcoding.ibas.bobas.test.bo;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.bo.BOException;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.ISqlStoredProcedure;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.DataTableColumn;
import org.colorcoding.ibas.bobas.data.DataTableRow;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.SingleValue;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;
import org.colorcoding.ibas.bobas.db.IDbConnection;
import org.colorcoding.ibas.bobas.expression.SQLScriptValueOperator;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;

import junit.framework.TestCase;

public class TestBOUtilities extends TestCase {

	public void testGetBOPathValue() throws BOException {
		SalesOrder order = new SalesOrder();

		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new BigDecimal("99.99"));
		order.setDocumentUser(new User());
		order.setTeamUsers(new User[] { new User(), new User() });
		order.setCycle(new Time(1.05, emTimeUnit.HOUR));
		order.getCycle().setValue(0.9988);

		order.getUserFields().register("U_OrderType", DbFieldType.ALPHANUMERIC);
		order.getUserFields().register("U_OrderId", DbFieldType.NUMERIC);
		order.getUserFields().register("U_OrderDate", DbFieldType.DATE);
		order.getUserFields().register("U_OrderTotal", DbFieldType.DECIMAL);

		order.getUserFields().get("U_OrderType").setValue("S0000");
		order.getUserFields().get("U_OrderId").setValue(5768);
		order.getUserFields().get("U_OrderDate").setValue(DateTime.getToday());
		order.getUserFields().get("U_OrderTotal").setValue(new BigDecimal("999.888"));

		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new BigDecimal(10));
		orderItem.setPrice(BigDecimal.valueOf(99.99));
		((SalesOrderItem) orderItem).getUserFields().register("U_LineType", DbFieldType.ALPHANUMERIC);
		((SalesOrderItem) orderItem).getUserFields().get("U_LineType").setValue("L0000");

		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);

		String path1 = "CustomerCode";
		String path2 = "SalesOrderItems.ItemCode";
		String path3 = "U_OrderType";
		String path4 = "SalesOrderItems.U_LineType";

		Object value1 = BOUtilities.getPropertyValue(order, path1);
		assertEquals(String.format("%s not equals.", path1), value1, order.getCustomerCode());

		Object value3 = BOUtilities.getPropertyValue(order, path3);
		assertEquals(String.format("%s not equals.", path3), value3,
				order.getUserFields().get("U_OrderType").getValue());

		orderItem = order.getSalesOrderItems().get(0);

		Object value2 = BOUtilities.getPropertyValue(order, path2, 0);
		assertEquals(String.format("%s not equals.", path2), value2, orderItem.getItemCode());

		Object value4 = BOUtilities.getPropertyValue(order, path4, 0);
		assertEquals(String.format("%s not equals.", path4), value4,
				((SalesOrderItem) orderItem).getUserFields().get("U_LineType").getValue());

		orderItem = order.getSalesOrderItems().get(1);

		value2 = BOUtilities.getPropertyValue(order, path2, 1);
		assertEquals(String.format("%s not equals.", path2), value2, orderItem.getItemCode());

		SQLScriptValueOperator operator = new SQLScriptValueOperator(new IBORepository4DbReadonly() {

			@Override
			public IUser getCurrentUser() {
				return null;
			}

			@Override
			public void setCurrentUser(IUser user) {

			}

			@Override
			public void dispose() throws RepositoryException {
			}

			@Override
			public DateTime getServerTime() {
				return null;
			}

			@Override
			public String getTransactionId() {
				return null;
			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType) {
				return null;
			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ICriteria criteria, Class<T> boType) {
				return null;
			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopy(T bo) {
				return null;
			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopyEx(T bo) {
				return null;
			}

			@Override
			public IDbConnection getDbConnection() throws DbException {
				return null;
			}

			@Override
			public void setDbConnection(IDbConnection connection) {

			}

			@Override
			public IBOAdapter getBOAdapter() throws DbException {
				return null;
			}

			@Override
			public void setBOAdapter(IBOAdapter boAdapter) {

			}

			@Override
			public void connectDb(String dbType, String dbServer, String dbName, String dbUser, String dbPassword)
					throws DbException {

			}

			@Override
			public void connectDb(String dbServer, String dbName, String dbUser, String dbPassword) throws DbException {

			}

			@Override
			public boolean openDbConnection() throws DbException {
				return false;
			}

			@Override
			public void closeDbConnection() throws DbException {

			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ISqlQuery sqlQuery, Class<T> boType) {
				return null;
			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ISqlQuery sqlQuery, Class<T> boType) {
				return null;
			}

			@Override
			public IOperationResult<SingleValue> fetch(ISqlQuery sqlQuery) {
				return null;
			}

			@Override
			public IOperationResult<IDataTable> query(ISqlQuery sqlQuery) {
				System.out.println(sqlQuery.toString());
				DataTable dataTable = new DataTable();
				dataTable.getColumns().add(new DataTableColumn());
				dataTable.getColumns().get(0).setDataType(String.class);
				dataTable.getRows().add(new DataTableRow());
				dataTable.getRows().get(0).setValue(0, "OK");
				return new OperationResult<IDataTable>().addResultObjects(dataTable);
			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ISqlStoredProcedure sp, Class<T> boType) {

				return null;
			}

			@Override
			public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ISqlStoredProcedure sp,
					Class<T> boType) {
				return null;
			}

			@Override
			public IOperationResult<SingleValue> fetch(ISqlStoredProcedure sp) {
				return null;
			}

		});
		orderItem = order.getSalesOrderItems().create();
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A0000'2");
		operator.setPropertyName("select 1 where CHARINDEX('LM4029D','${SalesOrderItems.ItemCode}') > 0");
		operator.setValue(order);
		System.out.println(operator.getValue());
	}
}
