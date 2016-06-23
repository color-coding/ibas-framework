package org.colorcoding.ibas.bobas.db.mssql.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.db.DbAdapterFactory;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IDbAdapter;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbConnection;
import org.colorcoding.ibas.bobas.db.IDbDataReader;

import junit.framework.TestCase;

public class testDbConnect extends TestCase {

	public testDbConnect() {

	}

	public void testMsqlConnect() {
		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=ibas_demo;ApplicationName=ibasTest";
		String userName = "sa";
		String userPwd = "1q2w3e";
		try {
			Class.forName(driverName);
			Connection dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("连接成功");
			Statement stmt = dbConn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from cc_tt_user");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println(rsmd.getColumnName(0));
			System.out.println(String.format("row count:%s | colunm count:%s ", rs.getRow(), rsmd.getColumnCount()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDbTransactions() {
		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=ibas_demo;ApplicationName=ibasTest";
		String userName = "sa";
		String userPwd = "1q2w3e";
		try {
			Class.forName(driverName);
			Connection dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			dbConn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			System.out.println("连接成功");
			dbConn.setAutoCommit(false);// 手动事务
			Statement stmt = dbConn.createStatement();
			ResultSet rSet = stmt.executeQuery("SELECT MAX(LineId) FROM CC_TT_RDR1 WHERE DocEntry = 5");
			rSet.next();
			stmt = dbConn.createStatement();
			int before = rSet.getInt(1);
			stmt.executeUpdate(
					"INSERT INTO CC_TT_RDR1 (DocEntry, LineId, VisOrder, Object, DataSource, LogInst, Canceled, Status, LineStatus, CreateDate, CreateTime, UpdateDate, UpdateTime, Creator, Updator, CreateActId, UpdateActId, Ref1, Ref2, Refed, Deleted, TargetType, TrgetEntry, BaseRef, BaseType, BaseEntry, BaseLinNum, ItemCode, Dscription, ManSerNum, ManBtchNum, ManServiceNum, Quantity, ShipDate, OpenQty, Price, Currency, Rate, DiscPrcnt, LineTotal, OpenSum, VendorNum, WhsCode, TreeBasisQty, LineSign, ParentSign, AcctCode, GrossBuyPr, PriceBefDi, UseBaseUn, CodeBars, VatPrcnt, VatGroup, PriceAfVAT, VatSum, GrssProfit, Gtotal, DelivrdQty, LinManClsd, Project, OcrCode1, OcrCode2, OcrCode3, OcrCode4, OcrCode5, BsDocType, BsDocEntry, BsDocLine, GrossBase, PrmtsCode) VALUES (5, 1, 0, N'CC_TT_SALESORDER', NULL, 0, N'N', N'O', N'P', NULL, N'0', NULL, N'0', 0, 0, NULL, NULL, NULL, NULL, N'N', N'N', NULL, 0, NULL, NULL, 0, 0, N'S001', N'������ʿ-��', N'N', N'N', N'N', 2, NULL, 0.0, 99, NULL, 0.0, 0.0, 0.0, 0.0, NULL, NULL, 0.0, NULL, NULL, NULL, 0.0, 0.0, NULL, NULL, 0.0, NULL, 0.0, 0.0, 0.0, 0.0, 0.0, N'N', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, NULL)");
			stmt.executeUpdate(
					"INSERT INTO CC_TT_RDR1 (DocEntry, LineId, VisOrder, Object, DataSource, LogInst, Canceled, Status, LineStatus, CreateDate, CreateTime, UpdateDate, UpdateTime, Creator, Updator, CreateActId, UpdateActId, Ref1, Ref2, Refed, Deleted, TargetType, TrgetEntry, BaseRef, BaseType, BaseEntry, BaseLinNum, ItemCode, Dscription, ManSerNum, ManBtchNum, ManServiceNum, Quantity, ShipDate, OpenQty, Price, Currency, Rate, DiscPrcnt, LineTotal, OpenSum, VendorNum, WhsCode, TreeBasisQty, LineSign, ParentSign, AcctCode, GrossBuyPr, PriceBefDi, UseBaseUn, CodeBars, VatPrcnt, VatGroup, PriceAfVAT, VatSum, GrssProfit, Gtotal, DelivrdQty, LinManClsd, Project, OcrCode1, OcrCode2, OcrCode3, OcrCode4, OcrCode5, BsDocType, BsDocEntry, BsDocLine, GrossBase, PrmtsCode) VALUES (5, 2, 0, N'CC_TT_SALESORDER', NULL, 0, N'N', N'O', N'P', NULL, N'0', NULL, N'0', 0, 0, NULL, NULL, NULL, NULL, N'N', N'N', NULL, 0, NULL, NULL, 0, 0, N'S001', N'������ʿ-��', N'N', N'N', N'N', 2, NULL, 0.0, 99, NULL, 0.0, 0.0, 0.0, 0.0, NULL, NULL, 0.0, NULL, NULL, NULL, 0.0, 0.0, NULL, NULL, 0.0, NULL, 0.0, 0.0, 0.0, 0.0, 0.0, N'N', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, NULL)");

			stmt = dbConn.createStatement();
			rSet = stmt.executeQuery("SELECT MAX(LineId) FROM CC_TT_RDR1 WHERE DocEntry = 5");
			rSet.next();
			int after = rSet.getInt(1);
			System.out.println(String.format("before %s after %s", before, after));
			dbConn.rollback();// 回滚
			dbConn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testToDataTable() throws DbException {
		IDbAdapter dbAdapter = DbAdapterFactory.createAdapter();
		IDbConnection dbConnection = dbAdapter.createDbConnection();
		IDbCommand dbCommand = dbConnection.createCommand();
		IDbDataReader dbDataReader = dbCommand.executeReader("select * from cc_tt_oitm");
		IDataTable dataTable = dbDataReader.toDataTable();

		System.out.println("toString xml");
		System.out.println(dataTable.toString("xml"));
		System.out.println("toString json");
		System.out.println(dataTable.toString("json"));
	}
}
