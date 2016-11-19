package org.colorcoding.ibas.bobas.db.hana.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.colorcoding.ibas.bobas.db.hana.DbAdapter;

import junit.framework.TestCase;

public class testDbAdapter extends TestCase {

	String password = "AVAtech2015!";

	String dbName = "ibas_demo_1042790962";

	public void testforDbAdapter() {
		DbAdapter dbAdapter = new DbAdapter();
		try {
			Connection dbConn = dbAdapter.createConnection("ibas-dev-hana:30015", dbName, "SYSTEM", password, "");

			Statement stmt = dbConn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CURRENT_SCHEMA FROM DUMMY");
			while (rs.next()) {
				System.out.println(rs.getString(1));
				break;
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println(String.format("row count:%s | colunm count:%s ", rs.getRow(), rsmd.getColumnCount()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testHanaConnection() {
		String driverName = "com.sap.db.jdbc.Driver";
		String dbURL = String.format("jdbc:sap://ibas-dev-hana:30015/?currentschema=\"%s\"", dbName);
		String userName = "SYSTEM";
		String userPwd = password;
		try {
			Class.forName(driverName);
			Connection dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("连接成功");
			Statement stmt = dbConn.createStatement();
			System.out.println(String.format("QueryTimeout:%s", stmt.getQueryTimeout()));
			ResultSet rs = stmt.executeQuery("select * from CC_TT_USER;");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println(String.format("row count:%s | colunm count:%s ", rs.getRow(), rsmd.getColumnCount()));
			stmt.addBatch("update CC_TT_USER set \"UserName\" = \"UserCode\";");
			stmt.addBatch("update CC_TT_USER set \"UserName\" = \"UserCode\";");

			// stmt.addBatch("select * from CC_TT_USER;");// 返回结果集，不可用
			stmt.addBatch("CALL CC_SP_TRANSACTION_NOTIFICATION('','',0,'','');");
			stmt.addBatch("update CC_TT_USER set \"UserName\" = \"UserCode\";");
			int[] count = stmt.executeBatch();
			for (int i : count) {
				System.out.println(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
