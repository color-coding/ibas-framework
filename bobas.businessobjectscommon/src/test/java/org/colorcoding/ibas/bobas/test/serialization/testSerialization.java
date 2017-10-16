package org.colorcoding.ibas.bobas.test.serialization;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.ISerializerManager;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;
import org.colorcoding.ibas.bobas.serialization.SerializerXml;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.User;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class testSerialization extends TestCase {

	public void testSerializer() throws SAXException, IOException {
		ISerializerManager manager = SerializerFactory.create().createManager();
		ISerializer<?> serializer = manager.create("xml");
		Materials materials = new Materials();
		materials.setCreateDate(DateTime.getToday());
		System.out.println(materials.toString("xml"));
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Materials><ItemCode>A0003</ItemCode><ItemDescription>A0003</ItemDescription><CreateDate>2099-11-11</CreateDate><OnHand>999.99</OnHand></Materials>";

		IBusinessObject bo = (IBusinessObject) serializer.deserialize(xml, Materials.class);
		System.out.println(bo.toString("xml"));
		bo = (IBusinessObject) serializer.deserialize(xml.replace("2099-11-11", "2099/11/12"), Materials.class);
		System.out.println(bo.toString("xml"));
		bo = (IBusinessObject) serializer.deserialize(xml.replace("2099-11-11", "2099/1/2"), Materials.class);
		System.out.println(bo.toString("xml"));

	}

	public void testXmlSchema() throws JAXBException, IOException, SAXException {
		ISerializer<?> serializer = new SerializerXml();
		File file = new File(MyConfiguration.getWorkFolder() + File.separator + "test.xsd");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream writer = new FileOutputStream(file);
		serializer.getSchema(SalesOrder.class, writer);
		// schema校验
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
		stringBuilder.append("<ns:SalesOrder xmlns:ns=\"http://colorcoding.org/ibas/bobas/test\">\r\n");
		stringBuilder.append("<isDirty>true</isDirty>\r\n");
		stringBuilder.append("<isDeleted>false</isDeleted>\r\n");
		stringBuilder.append("<isNew>true</isNew>\r\n");
		stringBuilder.append("<UserFields>\r\n");
		stringBuilder.append("<UserField>\r\n");
		stringBuilder.append("<Name>U_OrderType</Name>\r\n");
		stringBuilder.append("<Value>S0000</Value>\r\n");
		stringBuilder.append("<ValueType>ALPHANUMERIC</ValueType>\r\n");
		stringBuilder.append("</UserField>\r\n");
		stringBuilder.append("<UserField>\r\n");
		stringBuilder.append("<Name>U_OrderId</Name>\r\n");
		stringBuilder.append("<Value>5768</Value>\r\n");
		stringBuilder.append("<ValueType>NUMERIC</ValueType>\r\n");
		stringBuilder.append("</UserField>\r\n");
		stringBuilder.append("<UserField>\r\n");
		stringBuilder.append("<Name>U_OrderDate</Name>\r\n");
		stringBuilder.append("<Value>2017-03-09</Value>\r\n");
		stringBuilder.append("<ValueType>DATE</ValueType>\r\n");
		stringBuilder.append("</UserField>\r\n");
		stringBuilder.append("<UserField>\r\n");
		stringBuilder.append("<Name>U_OrderTotal</Name>\r\n");
		stringBuilder.append("<Value>999.888</Value>\r\n");
		stringBuilder.append("<ValueType>DECIMAL</ValueType>\r\n");
		stringBuilder.append("</UserField>\r\n");
		stringBuilder.append("</UserFields>\r\n");
		stringBuilder.append("<ApprovalStatus>UNAFFECTED</ApprovalStatus>\r\n");
		stringBuilder.append("<Canceled>NO</Canceled>\r\n");
		stringBuilder.append("<CreateDate/>\r\n");
		stringBuilder.append("<CreateTime>0</CreateTime>\r\n");
		stringBuilder.append("<CreateUserSign>0</CreateUserSign>\r\n");
		stringBuilder.append("<CustomerCode>C00001</CustomerCode>\r\n");
		stringBuilder.append("<Cycle>\r\n");
		stringBuilder.append("<Value>0.998801</Value>\r\n");
		stringBuilder.append("<Unit>HOUR</Unit>\r\n");
		stringBuilder.append("</Cycle>\r\n");
		stringBuilder.append("<DataOwner>0</DataOwner>\r\n");
		stringBuilder.append("<DeliveryDate>2017-03-09T00:00:00</DeliveryDate>\r\n");
		stringBuilder.append("<DocEntry>1</DocEntry>\r\n");
		stringBuilder.append("<DocNum>0</DocNum>\r\n");
		stringBuilder.append("<DocumentDate>2017-03-09T00:00:00</DocumentDate>\r\n");
		stringBuilder.append("<DocumentRate>0.000000</DocumentRate>\r\n");
		stringBuilder.append("<DocumentStatus>RELEASED</DocumentStatus>\r\n");
		stringBuilder.append("<DocumentTotal>99.990000</DocumentTotal>\r\n");
		stringBuilder.append("<DocumentUser>\r\n");
		stringBuilder.append("<isDirty>true</isDirty>\r\n");
		stringBuilder.append("<isDeleted>false</isDeleted>\r\n");
		stringBuilder.append("<isNew>true</isNew>\r\n");
		stringBuilder.append("<Activated>YES</Activated>\r\n");
		stringBuilder.append("<ApprovalStatus>UNAFFECTED</ApprovalStatus>\r\n");
		stringBuilder.append("<CreateDate/>\r\n");
		stringBuilder.append("<CreateTime>0</CreateTime>\r\n");
		stringBuilder.append("<CreateUserSign>0</CreateUserSign>\r\n");
		stringBuilder.append("<DataOwner>0</DataOwner>\r\n");
		stringBuilder.append("<LogInst>0</LogInst>\r\n");
		stringBuilder.append("<ObjectCode>CC_TT_USER</ObjectCode>\r\n");
		stringBuilder.append("<ObjectKey>0</ObjectKey>\r\n");
		stringBuilder.append("<Series>0</Series>\r\n");
		stringBuilder.append("<SupperUser>NO</SupperUser>\r\n");
		stringBuilder.append("<UpdateDate/>\r\n");
		stringBuilder.append("<UpdateTime>0</UpdateTime>\r\n");
		stringBuilder.append("<UpdateUserSign>0</UpdateUserSign>\r\n");
		stringBuilder.append("</DocumentUser>\r\n");
		stringBuilder.append("<Handwritten>NO</Handwritten>\r\n");
		stringBuilder.append("<Instance>0</Instance>\r\n");
		stringBuilder.append("<LogInst>0</LogInst>\r\n");
		stringBuilder.append("<ObjectCode>CC_TT_SALESORDER</ObjectCode>\r\n");
		stringBuilder.append("<Period>0</Period>\r\n");
		stringBuilder.append("<PostingDate>2017-03-09T00:00:00</PostingDate>\r\n");
		stringBuilder.append("<Referenced>NO</Referenced>\r\n");
		stringBuilder.append("<SalesOrderItems>\r\n");
		stringBuilder.append("<SalesOrderItem>\r\n");
		stringBuilder.append("<isDirty>true</isDirty>\r\n");
		stringBuilder.append("<isDeleted>false</isDeleted>\r\n");
		stringBuilder.append("<isNew>true</isNew>\r\n");
		stringBuilder.append("<Canceled>NO</Canceled>\r\n");
		stringBuilder.append("<CreateDate/>\r\n");
		stringBuilder.append("<CreateTime>0</CreateTime>\r\n");
		stringBuilder.append("<CreateUserSign>0</CreateUserSign>\r\n");
		stringBuilder.append("<DeliveryDate/>\r\n");
		stringBuilder.append("<DocEntry>1</DocEntry>\r\n");
		stringBuilder.append("<ItemCode>A00001</ItemCode>\r\n");
		stringBuilder.append("<LineId>1</LineId>\r\n");
		stringBuilder.append("<LineStatus>RELEASED</LineStatus>\r\n");
		stringBuilder.append("<LineTotal>0.000000</LineTotal>\r\n");
		stringBuilder.append("<LogInst>0</LogInst>\r\n");
		stringBuilder.append("<ObjectCode>CC_TT_SALESORDER</ObjectCode>\r\n");
		stringBuilder.append("<OpenQuantity>0.000000</OpenQuantity>\r\n");
		stringBuilder.append("<Price>99.990000</Price>\r\n");
		stringBuilder.append("<Quantity>10.000000</Quantity>\r\n");
		stringBuilder.append("<Referenced>NO</Referenced>\r\n");
		stringBuilder.append("<Status>OPEN</Status>\r\n");
		stringBuilder.append("<UpdateDate/>\r\n");
		stringBuilder.append("<UpdateTime>0</UpdateTime>\r\n");
		stringBuilder.append("<UpdateUserSign>0</UpdateUserSign>\r\n");
		stringBuilder.append("<VisOrder>0</VisOrder>\r\n");
		stringBuilder.append("</SalesOrderItem>\r\n");
		stringBuilder.append("<SalesOrderItem>\r\n");
		stringBuilder.append("<isDirty>true</isDirty>\r\n");
		stringBuilder.append("<isDeleted>false</isDeleted>\r\n");
		stringBuilder.append("<isNew>true</isNew>\r\n");
		stringBuilder.append("<Canceled>NO</Canceled>\r\n");
		stringBuilder.append("<CreateDate/>\r\n");
		stringBuilder.append("<CreateTime>0</CreateTime>\r\n");
		stringBuilder.append("<CreateUserSign>0</CreateUserSign>\r\n");
		stringBuilder.append("<DeliveryDate/>\r\n");
		stringBuilder.append("<DocEntry>1</DocEntry>\r\n");
		stringBuilder.append("<ItemCode>A00002</ItemCode>\r\n");
		stringBuilder.append("<LineId>2</LineId>\r\n");
		stringBuilder.append("<LineStatus>RELEASED</LineStatus>\r\n");
		stringBuilder.append("<LineTotal>0.000000</LineTotal>\r\n");
		stringBuilder.append("<LogInst>0</LogInst>\r\n");
		stringBuilder.append("<ObjectCode>CC_TT_SALESORDER</ObjectCode>\r\n");
		stringBuilder.append("<OpenQuantity>0.000000</OpenQuantity>\r\n");
		stringBuilder.append("<Price>199.990001</Price>\r\n");
		stringBuilder.append("<Quantity>10.000000</Quantity>\r\n");
		stringBuilder.append("<Referenced>NO</Referenced>\r\n");
		stringBuilder.append("<Status>OPEN</Status>\r\n");
		stringBuilder.append("<UpdateDate/>\r\n");
		stringBuilder.append("<UpdateTime>0</UpdateTime>\r\n");
		stringBuilder.append("<UpdateUserSign>0</UpdateUserSign>\r\n");
		stringBuilder.append("<VisOrder>0</VisOrder>\r\n");
		stringBuilder.append("</SalesOrderItem>\r\n");
		stringBuilder.append("</SalesOrderItems>\r\n");
		stringBuilder.append("<Series>0</Series>\r\n");
		stringBuilder.append("<Status>OPEN</Status>\r\n");
		stringBuilder.append("<TeamUsers>\r\n");
		stringBuilder.append("<User>\r\n");
		stringBuilder.append("<isDirty>true</isDirty>\r\n");
		stringBuilder.append("<isDeleted>false</isDeleted>\r\n");
		stringBuilder.append("<isNew>true</isNew>\r\n");
		stringBuilder.append("<Activated>YES</Activated>\r\n");
		stringBuilder.append("<ApprovalStatus>UNAFFECTED</ApprovalStatus>\r\n");
		stringBuilder.append("<CreateDate/>\r\n");
		stringBuilder.append("<CreateTime>0</CreateTime>\r\n");
		stringBuilder.append("<CreateUserSign>0</CreateUserSign>\r\n");
		stringBuilder.append("<DataOwner>0</DataOwner>\r\n");
		stringBuilder.append("<LogInst>0</LogInst>\r\n");
		stringBuilder.append("<ObjectCode>CC_TT_USER</ObjectCode>\r\n");
		stringBuilder.append("<ObjectKey>0</ObjectKey>\r\n");
		stringBuilder.append("<Series>0</Series>\r\n");
		stringBuilder.append("<SupperUser>NO</SupperUser>\r\n");
		stringBuilder.append("<UpdateDate/>\r\n");
		stringBuilder.append("<UpdateTime>0</UpdateTime>\r\n");
		stringBuilder.append("<UpdateUserSign>0</UpdateUserSign>\r\n");
		stringBuilder.append("</User>\r\n");
		stringBuilder.append("<User>\r\n");
		stringBuilder.append("<isDirty>true</isDirty>\r\n");
		stringBuilder.append("<isDeleted>false</isDeleted>\r\n");
		stringBuilder.append("<isNew>true</isNew>\r\n");
		stringBuilder.append("<Activated>YES</Activated>\r\n");
		stringBuilder.append("<ApprovalStatus>UNAFFECTED</ApprovalStatus>\r\n");
		stringBuilder.append("<CreateDate/>\r\n");
		stringBuilder.append("<CreateTime>0</CreateTime>\r\n");
		stringBuilder.append("<CreateUserSign>0</CreateUserSign>\r\n");
		stringBuilder.append("<DataOwner>0</DataOwner>\r\n");
		stringBuilder.append("<LogInst>0</LogInst>\r\n");
		stringBuilder.append("<ObjectCode>CC_TT_USER</ObjectCode>\r\n");
		stringBuilder.append("<ObjectKey>0</ObjectKey>\r\n");
		stringBuilder.append("<Series>0</Series>\r\n");
		stringBuilder.append("<SupperUser>NO</SupperUser>\r\n");
		stringBuilder.append("<UpdateDate/>\r\n");
		stringBuilder.append("<UpdateTime>0</UpdateTime>\r\n");
		stringBuilder.append("<UpdateUserSign>0</UpdateUserSign>\r\n");
		stringBuilder.append("</User>\r\n");
		stringBuilder.append("</TeamUsers>\r\n");
		stringBuilder.append("<TestDate>2017-03-09T17:25:55.577+08:00</TestDate>\r\n");
		stringBuilder.append("<Transfered>NO</Transfered>\r\n");
		stringBuilder.append("<UpdateDate/>\r\n");
		stringBuilder.append("<UpdateTime>0</UpdateTime>\r\n");
		stringBuilder.append("<UpdateUserSign>0</UpdateUserSign>\r\n");
		stringBuilder.append("<UserSign>0</UserSign>\r\n");
		stringBuilder.append("</ns:SalesOrder>\r\n");
		// System.err.println(stringBuilder.toString());
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		// 包装待验证的xml字符串为Reader
		Reader xmlReader = new BufferedReader(new StringReader(stringBuilder.toString()));
		// 保障Schema xsd字符串为Reader
		FileReader xsdReader = new FileReader(file);
		Source xsdSource = new StreamSource(xsdReader);
		// 解析作为Schema的指定源并以Schema形式返回它
		Schema schema = factory.newSchema(xsdSource);
		// 根据Schema检查xml文档的处理器,创建此 Schema的新 Validator
		Validator validator = schema.newValidator();
		// 构造待验证xml Source
		Source xmlSource = new StreamSource(xmlReader);
		// 执行验证
		validator.validate(xmlSource);

	}

	public void testXmlSchemaEx() throws SAXException, IOException, ValidateException {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setDocumentUser(new User());
		order.setTeamUsers(new User[] { new User(), new User() });
		order.setCycle(new Time(1.05, emTimeUnit.HOUR));
		order.getCycle().setValue(0.9988);
		order.getUserFields().addUserField("U_OrderType", DbFieldType.ALPHANUMERIC);
		order.getUserFields().addUserField("U_OrderId", DbFieldType.NUMERIC);
		order.getUserFields().addUserField("U_OrderDate", DbFieldType.DATE);
		order.getUserFields().addUserField("U_OrderTotal", DbFieldType.DECIMAL);
		order.getUserFields().setValue("U_OrderType", "S0000");
		order.getUserFields().setValue("U_OrderId", 5768);
		order.getUserFields().setValue("U_OrderDate", DateTime.getToday());
		order.getUserFields().setValue("U_OrderTotal", new Decimal("999.888"));
		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new Decimal(10));
		orderItem.setPrice(new Decimal(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);
		String xml = order.toString("xml");
		System.out.println(xml);
		ISerializer<?> serializer = new SerializerXml();
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.getSchema(SalesOrder.class, writer);
		System.out.println(writer.toString());
		serializer.validate(SalesOrder.class, xml);
	}

}
