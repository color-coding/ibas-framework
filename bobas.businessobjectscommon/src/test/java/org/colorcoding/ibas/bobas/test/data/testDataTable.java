
package org.colorcoding.ibas.bobas.test.data;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.IDataTableColumn;
import org.colorcoding.ibas.bobas.data.IDataTableRow;

import junit.framework.TestCase;

public class testDataTable extends TestCase {

	public void testUsing() throws JAXBException {

		IDataTable table = new DataTable();
		table.setName("Test");
		IDataTableColumn column_1 = table.getColumns().create();
		column_1.setName("col_0");
		column_1.setDataType(String.class);
		IDataTableColumn column_2 = table.getColumns().create("col_1", Integer.class);
		IDataTableColumn column_3 = table.getColumns().create("col_2", DateTime.class);
		IDataTableColumn column_4 = table.getColumns().create("col_3", Decimal.class);

		IDataTableRow row = table.getRows().create();
		row.setValue(0, "第一行");
		row.setValue(1, 0);
		row.setValue(2, DateTime.getNow());
		row.setValue(3, Decimal.valueOf("0.99"));
		row = table.getRows().create();
		row.setValue("col_0", "第二行");
		row.setValue("col_1", 9);
		row.setValue("col_2", DateTime.getNow());
		row.setValue("col_3", Decimal.valueOf("1.99"));
		row = table.getRows().create();
		row.setValue(column_1, "第三行");
		row.setValue(column_2, 19);
		row.setValue(column_3, DateTime.getNow());
		row.setValue(column_4, Decimal.valueOf("1.99"));

		// 测试业务对象序列化
		JAXBContext context = JAXBContext.newInstance(table.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
		StringWriter writer = new StringWriter();
		marshaller.marshal(table, writer);
		String oldXML = writer.toString();
		System.out.println("序列化输出：");
		System.out.println(oldXML);
		

		System.out.println("toString xml：");
		System.out.println(table.toString("xml"));
		

		System.out.println("toString json：");
		System.out.println(table.toString("json"));
		
		
	}

}