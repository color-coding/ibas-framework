package org.colorcoding.ibas.bobas.db.hana.test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.colorcoding.ibas.bobas.db.hana.DbAdapter;

import com.sap.db.jdbc.ConnectionSapDBFinalize;

import junit.framework.TestCase;

public class testDbAdapter extends TestCase {

	public void testForDbAdapter() {
		DbAdapter dbAdapter = new DbAdapter();
		try {
			Connection dbConn = dbAdapter.createConnection("122.5.6.90:30015", "IBAS_DEMO", "SYSTEM", "HANAavatech2012",
					"");

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
		String dbURL = "jdbc:sap://122.5.6.90:30015/?currentschema=IBAS_DEMO";
		String userName = "SYSTEM";
		String userPwd = "HANAavatech2012";
		try {
			Class.forName(driverName);
			Connection dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			Field[] fields = dbConn.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				System.out.println(fields[i].getName());
				if (fields[i].getName().equals("_inner")) {
					Field field = fields[i];
					field.setAccessible(true);
					ConnectionSapDBFinalize sapDBFinalize = (ConnectionSapDBFinalize) field.get(dbConn);
					System.out.println(sapDBFinalize.getSchema());
				}
			}
			// DriverManager.getDriver(dbURL)
			System.out.println("连接成功");
			Statement stmt = dbConn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from CC_SYS_USER");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println(String.format("row count:%s | colunm count:%s ", rs.getRow(), rsmd.getColumnCount()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
