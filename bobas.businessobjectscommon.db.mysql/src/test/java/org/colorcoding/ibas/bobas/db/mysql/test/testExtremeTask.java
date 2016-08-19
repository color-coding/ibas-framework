package org.colorcoding.ibas.bobas.db.mysql.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.ComputeException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testExtremeTask extends TestCase {

	public void writeBO2DB() throws ComputeException, RepositoryException {
		// 向数据库中写入100万数据
		// 已优化app.xml：禁止重新检索，业务逻辑，审批逻辑
		// 已优化SalesOrder对象：子项主键自己管理；
		BORepositoryTest boRepository = new BORepositoryTest();
		DateTime start = DateTime.getNow();
		boRepository.beginTransaction();
		int count = 10000;
		for (int i = 0; i < count; i++) {
			SalesOrder order = new SalesOrder();
			order.setCustomerName("大量数据写入");
			ISalesOrderItem line = order.getSalesOrderItems().create();
			line.setItemDescription("鬼知道的物料1");
			line.setLineId(0);
			line = order.getSalesOrderItems().create();
			line.setItemDescription("鬼知道的物料2");
			IOperationResult<?> operationResult = boRepository.saveSalesOrder(order);
			if (operationResult.getResultCode() != 0) {
				System.err.println(operationResult.getMessage());
				break;
			}
		}
		boRepository.commitTransaction();
		boRepository.dispose();
		DateTime finish = DateTime.getNow();
		System.out.println(String.format("ibas写入[%s]条数据，从[%s]到[%s]共[%s]秒，平均%s条/秒。", count, start.toString("HH:mm:ss"),
				finish.toString("HH:mm:ss"), DateTime.interval(start, finish, emTimeUnit.second),
				count / DateTime.interval(start, finish, emTimeUnit.second)));
	}

	public void writeSQL2DB() throws ComputeException, ClassNotFoundException, SQLException {
		DateTime start = DateTime.getNow();
		String server = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_SERVER);
		String dbName = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_NAME);
		String userName = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_USER_ID);
		String userPwd = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_USER_PASSWORD);
		String dbURL = String.format("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=UTF-8", server, dbName);
		String driverName = "com.mysql.jdbc.Driver";
		Class.forName(driverName);
		Connection connection = DriverManager.getConnection(dbURL, userName, userPwd);
		connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		connection.setAutoCommit(false);// 手动事务

		int count = 10000;
		for (int i = 0; i < count; i++) {
			Statement stmt = connection.createStatement();
			ResultSet rSet = stmt.executeQuery(
					"SELECT `AutoKey` FROM `CC_SYS_ONNM` WHERE `ObjectCode` = 'CC_TT_SALESORDER' FOR UPDATE");
			rSet.next();
			int key = rSet.getInt(1);
			stmt = connection.createStatement();
			stmt.executeUpdate(
					"UPDATE `CC_SYS_ONNM` SET `AutoKey` = `AutoKey` + 1 WHERE `ObjectCode` = 'CC_TT_SALESORDER'");
			stmt = connection.createStatement();
			stmt.executeUpdate(String.format(
					"INSERT INTO `CC_TT_ORDR` (`DocEntry`, `DocNum`, `Period`, `Instance`, `Series`, `Handwrtten`, `Canceled`, `Object`, `DataSource`, `LogInst`, `UserSign`, `Transfered`, `Status`, `CreateDate`, `CreateTime`, `UpdateDate`, `UpdateTime`, `Creator`, `Updator`, `CreateActId`, `UpdateActId`, `DataOwner`, `TeamMembers`, `OrgCode`, `ApvlStatus`, `DocStatus`, `DocDate`, `DocDueDate`, `TaxDate`, `Ref1`, `Ref2`, `Remarks`, `CardCode`, `CardName`, `DocCur`, `DocRate`, `DocTotal`, `Cycle`, `CycleUnit`) VALUES (%s, 0, 0, 0, 0, N'N', N'N', N'CC_TT_SALESORDER', NULL, 1, 0, N'N', N'O', N'2016-08-18', N'1642', NULL, N'0', -1, 0, N'be70aaa3-2fbd-4148-9abc-c695b2dc423d', NULL, 0, NULL, NULL, N'U', N'R', N'2016-08-18', N'2016-08-18', N'2016-08-18', NULL, NULL, NULL, NULL, N'大量数据写入', NULL, 0.000000, 0.000000, 0.000000, N'h')",
					key));
			stmt.executeUpdate(String.format(
					"INSERT INTO `CC_TT_RDR1` (`DocEntry`, `LineId`, `VisOrder`, `Object`, `DataSource`, `LogInst`, `Canceled`, `Status`, `LineStatus`, `CreateDate`, `CreateTime`, `UpdateDate`, `UpdateTime`, `Creator`, `Updator`, `CreateActId`, `UpdateActId`, `Ref1`, `Ref2`, `ItemCode`, `Dscription`, `Quantity`, `ShipDate`, `OpenQty`, `Price`, `Currency`, `LineTotal`, `WhsCode`) VALUES (%s, 1, 0, N'CC_TT_SALESORDER', NULL, 1, N'N', N'O', N'P', N'2016-08-18', N'1642', NULL, N'0', -1, 0, N'be70aaa3-2fbd-4148-9abc-c695b2dc423d', NULL, NULL, NULL, NULL, N'鬼知道的物料1', 0.000000, NULL, 0.000000, 0.000000, NULL, 0.000000, NULL)",
					key));
			stmt.executeUpdate(String.format(
					"INSERT INTO `CC_TT_RDR1` (`DocEntry`, `LineId`, `VisOrder`, `Object`, `DataSource`, `LogInst`, `Canceled`, `Status`, `LineStatus`, `CreateDate`, `CreateTime`, `UpdateDate`, `UpdateTime`, `Creator`, `Updator`, `CreateActId`, `UpdateActId`, `Ref1`, `Ref2`, `ItemCode`, `Dscription`, `Quantity`, `ShipDate`, `OpenQty`, `Price`, `Currency`, `LineTotal`, `WhsCode`) VALUES (%s, 2, 0, N'CC_TT_SALESORDER', NULL, 1, N'N', N'O', N'P', N'2016-08-18', N'1642', NULL, N'0', -1, 0, N'be70aaa3-2fbd-4148-9abc-c695b2dc423d', NULL, NULL, NULL, NULL, N'鬼知道的物料1', 0.000000, NULL, 0.000000, 0.000000, NULL, 0.000000, NULL)",
					key));
		}
		connection.commit();
		connection.setAutoCommit(true);
		DateTime finish = DateTime.getNow();
		System.out.println(String.format("jdbc写入[%s]条数据，从[%s]到[%s]共[%s]秒，平均%s条/秒。", count, start.toString("HH:mm:ss"),
				finish.toString("HH:mm:ss"), DateTime.interval(start, finish, emTimeUnit.second),
				count / DateTime.interval(start, finish, emTimeUnit.second)));
	}

	public void testWriteDatas() throws Exception {

		String server = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_SERVER);
		String dbName = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_NAME);
		String userName = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_USER_ID);
		String userPwd = MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_USER_PASSWORD);
		String dbURL = String.format("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=UTF-8", server, dbName);
		String driverName = "com.mysql.jdbc.Driver";
		Class.forName(driverName);
		Connection connection = DriverManager.getConnection(dbURL, userName, userPwd);
		System.out.println(connection.getSchema());
		this.writeSQL2DB();
		this.writeBO2DB();

	}

	public void testReadDatas() throws Exception {

		BORepositoryTest boRepository = new BORepositoryTest();
		ICriteria criteria = new Criteria();
		criteria.setResultCount(10000);
		DateTime start = DateTime.getNow();
		IOperationResult<?> operationResult = boRepository.fetchSalesOrder(criteria);
		DateTime finish = DateTime.getNow();
		if (operationResult.getResultCode() != 0) {
			System.err.println(operationResult.getMessage());
		}
		System.out.println(String.format("查询[%s]条数据，从[%s]到[%s]共[%s]秒。", operationResult.getResultObjects().size(),
				start.toString("HH:mm:ss"), finish.toString("HH:mm:ss"),
				DateTime.interval(start, finish, emTimeUnit.second)));
	}
}
