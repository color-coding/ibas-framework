package org.colorcoding.ibas.bobas.test.bo;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.Serializer;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

import junit.framework.TestCase;

public class testBusinessObjects extends TestCase {

    public void testComplexBO() throws Exception {
        SalesOrder order = new SalesOrder();

        order.setDocEntry(1);
        order.setCustomerCode("C00001");
        order.setDeliveryDate(DateTime.getToday());
        order.setDocumentStatus(emDocumentStatus.Released);
        order.setDocumentTotal(new Decimal("99.99"));
        order.setDocumentUser(new User());
        order.setTeamUsers(new User[] { new User(), new User() });
        order.setCycle(new Time(1.05, emTimeUnit.hour));
        order.getCycle().setValue(0.9988);

        order.getUserFields().addUserField("U_OrderType", DbFieldType.db_Alphanumeric);
        order.getUserFields().addUserField("U_OrderId", DbFieldType.db_Numeric);
        order.getUserFields().addUserField("U_OrderDate", DbFieldType.db_Date);
        order.getUserFields().addUserField("U_OrderTotal", DbFieldType.db_Decimal);

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

        System.out.println(order.toString());
        assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
        assertEquals("Property [CustomerCode] faild. ", order.getCustomerCode(), "C00001");
        assertTrue("Property [DueDate] faild. ", order.getDeliveryDate().equals(DateTime.getToday()));
        assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.Released);
        assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

        System.out.println(orderItem.toString());
        assertEquals("Property [ItemCode] faild. ", orderItem.getItemCode(), "A00002");
        assertEquals("Property [Quantity] faild. ", orderItem.getQuantity().toString(), "10");

        order.markOld(true);// 全部置为old包括子项
        order.delete();
        assertEquals("order is not mark deleted. ", order.isDeleted(), true);
        assertEquals("order is not mark dirty. ", order.isDirty(), true);
        for (ISalesOrderItem item : order.getSalesOrderItems()) {
            assertEquals("order item is not mark deleted. ", item.isDeleted(), true);
            assertEquals("order item is not mark dirty. ", item.isDirty(), true);
        }
        assertEquals("order user is not mark deleted. ", order.getDocumentUser().isDeleted(), true);
        assertEquals("order user is not mark dirty. ", order.getDocumentUser().isDirty(), true);
        assertEquals("order team user is not mark deleted. ", order.getTeamUsers()[0].isDeleted(), true);
        assertEquals("order team user is not mark dirty. ", order.getTeamUsers()[0].isDirty(), true);

        order.markOld(true);
        assertEquals("order is not mark old. ", order.isDirty(), false);
        for (ISalesOrderItem item : order.getSalesOrderItems()) {
            assertEquals("order item is not mark old. ", item.isDirty(), false);
        }
        assertEquals("order user is not mark dirty. ", order.getDocumentUser().isDirty(), false);
        assertEquals("order team user is not mark dirty. ", order.getTeamUsers()[0].isDirty(), false);
        orderItem = order.getSalesOrderItems().firstOrDefault();
        orderItem.setPrice(299.99);
        assertEquals("order item is not mark dirty. ", orderItem.isDirty(), true);
        assertEquals("order is not mark dirty. ", order.isDirty(), true);

        order.markOld(true);
        orderItem = order.getSalesOrderItems().create();
        assertEquals("order is not mark dirty. ", order.isDirty(), true);
        order.getSalesOrderItems().delete(orderItem);
        boolean removed = !order.getSalesOrderItems().contains(orderItem);
        assertEquals("new order item is not removed. ", removed, true);
        orderItem = order.getSalesOrderItems().firstOrDefault();
        order.getSalesOrderItems().delete(orderItem);
        removed = !order.getSalesOrderItems().contains(orderItem);
        assertEquals("old order item is removed. ", removed, false);
        assertEquals("old order item is not mark deleted. ", orderItem.isDeleted(), true);
        // 测试业务对象序列化
        JAXBContext context = JAXBContext.newInstance(order.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
        StringWriter writer = new StringWriter();
        marshaller.marshal(order, writer);
        String oldXML = writer.toString();
        System.out.println("序列化输出：");
        System.out.println(oldXML);
        // 测试对象反序列化（克隆）
        ISalesOrder order2 = order.clone();
        writer = new StringWriter();
        marshaller.marshal(order2, writer);
        String newXML = writer.toString();
        System.out.println("反序列化输出：");
        System.out.println(newXML);
        assertEquals("marshal and unmarshal not equal", order.getSalesOrderItems().firstOrDefault().getItemCode(),
                order2.getSalesOrderItems().firstOrDefault().getItemCode());
        // 测试操作结果序列化
        OperationResult<SalesOrder> operationResult = new OperationResult<SalesOrder>();
        operationResult.addResultObjects(order);
        context = JAXBContext.newInstance(operationResult.getClass(), SalesOrder.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
        writer = new StringWriter();
        marshaller.marshal(operationResult, writer);
        System.out.println(writer.toString());

        // 比较对象是否一样
        // String[] diStrings = BOUtilities.findDiscrepancies(order, order2);
        // for (String string : diStrings) {
        // System.out.println(string);
        // }
        // assertEquals("clone bo is not same", diStrings.length, 0);

        System.out.println("toString xml：");
        System.out.println(order.toString("xml"));
        // System.out.println("toString json：");
        // System.out.println(order.toString("json"));

        IFieldData fieldData = order.getField("CustomerCode");
        assertEquals("get field CustomerCode faild.", fieldData.getName(), "CustomerCode");
        fieldData = order.getField("DocumentUser.UserCode");
        assertEquals("get field DocumentUser.UserCode faild.", fieldData.getName(), "UserCode");

        orderItem = order.getSalesOrderItems().firstOrDefault(item -> item.getLineId() == 2);
        assertNotNull("collection firstOrDefault faild.", orderItem);
    }

    public void testBOInherit() {
        System.out.println(SalesOrder.class.isAssignableFrom(IBusinessObject.class));
        System.out.println(SalesOrder.class.isAssignableFrom(ISalesOrder.class));
        System.out.println(ISalesOrder.class.isAssignableFrom(IBusinessObject.class));
        System.out.println(SalesOrder.class.isAssignableFrom(BusinessObject.class));

        System.out.println(BusinessObject.class.isAssignableFrom(SalesOrder.class));
        System.out.println(IBusinessObject.class.isAssignableFrom(SalesOrder.class));
        System.out.println(ISalesOrder.class.isAssignableFrom(SalesOrder.class));

        SalesOrder order = new SalesOrder2();

        order.setDocEntry(1);
        order.setCustomerCode("C00001");
        order.setDeliveryDate(DateTime.getToday());
        order.setDocumentStatus(emDocumentStatus.Released);
        order.setDocumentTotal(new Decimal("99.99"));
        order.setDocumentUser(new User());
        order.setTeamUsers(new User[] { new User(), new User() });
        order.setCycle(new Time(1.05, emTimeUnit.hour));
        order.getCycle().setValue(0.9988);

        order.getUserFields().addUserField("U_OrderType", DbFieldType.db_Alphanumeric);
        order.getUserFields().addUserField("U_OrderId", DbFieldType.db_Numeric);
        order.getUserFields().addUserField("U_OrderDate", DbFieldType.db_Date);
        order.getUserFields().addUserField("U_OrderTotal", DbFieldType.db_Decimal);

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

        System.out.println(order.toString());
        assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
        assertEquals("Property [CustomerCode] faild. ", order.getCustomerCode(), "C00001");
        assertTrue("Property [DueDate] faild. ", order.getDeliveryDate().equals(DateTime.getToday()));
        assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.Released);
        assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

        System.out.println(orderItem.toString());
        assertEquals("Property [ItemCode] faild. ", orderItem.getItemCode(), "A00002");
        assertEquals("Property [Quantity] faild. ", orderItem.getQuantity().toString(), "10");

    }

    public void testSerializer() {
        Materials materials = new Materials();
        materials.setCreateDate(DateTime.getToday());
        System.out.println(materials.toString("xml"));
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Materials><ItemCode>A0003</ItemCode><ItemDescription>A0003</ItemDescription><CreateDate>2099-11-11</CreateDate><OnHand>999.99</OnHand></Materials>";
        IBusinessObject bo = (IBusinessObject) Serializer.deserializeString(xml, Materials.class);
        System.out.println(bo.toString("xml"));
        bo = (IBusinessObject) Serializer.deserializeString(Serializer.DATA_TYPE_XML,
                xml.replace("2099-11-11", "2099/11/12"), Materials.class);
        System.out.println(bo.toString("xml"));
        bo = (IBusinessObject) Serializer.deserializeString(Serializer.DATA_TYPE_XML,
                xml.replace("2099-11-11", "2099/1/2"), Materials.class);
        System.out.println(bo.toString("xml"));
    }

    public void testStatusChanged() {
        SalesOrder order = new SalesOrder();
        order.setDocumentStatus(emDocumentStatus.Released);
        ISalesOrderItem line = order.getSalesOrderItems().create();
        assertEquals("LineStatus value faild.", order.getDocumentStatus(), line.getLineStatus());
        line = order.getSalesOrderItems().create();
        line = order.getSalesOrderItems().create();
        emYesNo changedCanceled = emYesNo.Yes;
        for (ISalesOrderItem item : order.getSalesOrderItems()) {
            item.setCanceled(changedCanceled);
            System.out.println(String.format("changed [%s] canceled [%s], parent [%s] ", item.toString(),
                    item.getCanceled(), order.getCanceled()));
        }
        System.out.println(String.format("finally [%s] canceled [%s]", order.toString(), order.getCanceled()));
        assertEquals("status changed faild.", changedCanceled, order.getCanceled());
        line.setCanceled(emYesNo.No);
        assertEquals("status changed faild.", emYesNo.No, order.getCanceled());
        System.out.println(String.format("finally [%s] status [%s]", order.toString(), order.getCanceled()));
        emBOStatus changedStatus = emBOStatus.Closed;
        for (ISalesOrderItem item : order.getSalesOrderItems()) {
            item.setStatus(changedStatus);
            System.out.println(String.format("changed [%s] status [%s], parent [%s] ", item.toString(),
                    item.getStatus(), order.getStatus()));
        }
        System.out.println(String.format("finally [%s] status [%s]", order.toString(), order.getStatus()));
        assertEquals("status changed faild.", changedStatus, order.getStatus());
        line.setStatus(emBOStatus.Open);
        assertEquals("status changed faild.", emBOStatus.Open, order.getStatus());
        emDocumentStatus changedDocumentStatus = emDocumentStatus.Finished;
        order.setDocumentStatus(changedDocumentStatus);
        System.out.println(
                String.format("changed [%s] documentstatus [%s]", order.toString(), order.getDocumentStatus()));
        for (ISalesOrderItem item : order.getSalesOrderItems()) {
            System.out.println(String.format("changed [%s] documentstatus [%s], parent [%s] ", item.toString(),
                    item.getLineStatus(), order.getDocumentStatus()));
            assertEquals("linestatus changed faild.", changedDocumentStatus, item.getLineStatus());
        }

        order = new SalesOrder();
        order.setDocumentStatus(emDocumentStatus.Planned);
        line = order.getSalesOrderItems().create();
        line = order.getSalesOrderItems().create();
        line = order.getSalesOrderItems().create();
        line.setLineStatus(emDocumentStatus.Finished);
        assertEquals("linestatus changed faild.", emDocumentStatus.Released, order.getDocumentStatus());
        for (ISalesOrderItem item : order.getSalesOrderItems()) {
            System.out.println(String.format("changed [%s] documentstatus [%s], parent [%s] ", item.toString(),
                    item.getLineStatus(), order.getDocumentStatus()));
        }
        for (ISalesOrderItem item : order.getSalesOrderItems()) {
            item.setLineStatus(emDocumentStatus.Closed);
            System.out.println(String.format("changed [%s] documentstatus [%s], parent [%s] ", item.toString(),
                    item.getLineStatus(), order.getDocumentStatus()));
        }
        System.out.println(
                String.format("finally [%s] documentstatus [%s]", order.toString(), order.getDocumentStatus()));
        assertEquals("status changed faild.", emDocumentStatus.Closed, order.getDocumentStatus());
        line.setLineStatus(emDocumentStatus.Planned);
        System.out.println(
                String.format("finally [%s] documentstatus [%s]", order.toString(), order.getDocumentStatus()));
        assertEquals("status changed faild.", emDocumentStatus.Released, order.getDocumentStatus());
        line.setLineStatus(emDocumentStatus.Closed);
        assertEquals("status changed faild.", emDocumentStatus.Closed, order.getDocumentStatus());
        line.setLineStatus(emDocumentStatus.Finished);
        System.out.println(
                String.format("finally [%s] documentstatus [%s]", order.toString(), order.getDocumentStatus()));
        assertEquals("status changed faild.", emDocumentStatus.Finished, order.getDocumentStatus());
    }

    public void testSerializStatus() {
        // 测试反序列化的状态变化
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        stringBuilder.append("<SalesOrder>");
        stringBuilder.append("<SalesOrderItems>");
        stringBuilder.append("<SalesOrderItem>");
        stringBuilder.append("<DocEntry>");
        stringBuilder.append(99);
        stringBuilder.append("</DocEntry>");
        stringBuilder.append("<LineId>");
        stringBuilder.append(1);
        stringBuilder.append("</LineId>");
        stringBuilder.append("<LineStatus>");
        stringBuilder.append(emDocumentStatus.Closed);
        stringBuilder.append("</LineStatus>");
        stringBuilder.append("<Status>");
        stringBuilder.append(emBOStatus.Closed);
        stringBuilder.append("</Status>");
        stringBuilder.append("<Canceled>");
        stringBuilder.append(emYesNo.Yes);
        stringBuilder.append("</Canceled>");
        stringBuilder.append("</SalesOrderItem>");
        stringBuilder.append("<SalesOrderItem>");
        stringBuilder.append("<DocEntry>");
        stringBuilder.append(99);
        stringBuilder.append("</DocEntry>");
        stringBuilder.append("<LineId>");
        stringBuilder.append(2);
        stringBuilder.append("</LineId>");
        stringBuilder.append("<LineStatus>");
        stringBuilder.append(emDocumentStatus.Closed);
        stringBuilder.append("</LineStatus>");
        stringBuilder.append("<Status>");
        stringBuilder.append(emBOStatus.Closed);
        stringBuilder.append("</Status>");
        stringBuilder.append("<Canceled>");
        stringBuilder.append(emYesNo.Yes);
        stringBuilder.append("</Canceled>");
        stringBuilder.append("</SalesOrderItem>");
        stringBuilder.append("<SalesOrderItem>");
        stringBuilder.append("<DocEntry>");
        stringBuilder.append(99);
        stringBuilder.append("</DocEntry>");
        stringBuilder.append("<LineId>");
        stringBuilder.append(0);
        stringBuilder.append("</LineId>");
        stringBuilder.append("<LineStatus>");
        stringBuilder.append(emDocumentStatus.Closed);
        stringBuilder.append("</LineStatus>");
        stringBuilder.append("<Status>");
        stringBuilder.append(emBOStatus.Closed);
        stringBuilder.append("</Status>");
        stringBuilder.append("<Canceled>");
        stringBuilder.append(emYesNo.Yes);
        stringBuilder.append("</Canceled>");
        stringBuilder.append("</SalesOrderItem>");
        stringBuilder.append("</SalesOrderItems>");
        stringBuilder.append("<DocumentStatus>");
        stringBuilder.append(emDocumentStatus.Closed);
        stringBuilder.append("</DocumentStatus>");
        stringBuilder.append("<Status>");
        stringBuilder.append(emBOStatus.Closed);
        stringBuilder.append("</Status>");
        stringBuilder.append("<Canceled>");
        stringBuilder.append(emYesNo.Yes);
        stringBuilder.append("</Canceled>");
        stringBuilder.append("<DocEntry>");
        stringBuilder.append(99);
        stringBuilder.append("</DocEntry>");
        stringBuilder.append("</SalesOrder>");
        SalesOrder bo = (SalesOrder) Serializer.deserializeString(stringBuilder.toString(), SalesOrder.class);
        System.out.println(Serializer.serializeString(bo, true));

        System.out.println(String.format("DocEntry: %s Status: %s DocumentStatus: %s Canceled: %s", bo.getDocEntry(),
                bo.getStatus(), bo.getDocumentStatus(), bo.getCanceled()));
        for (ISalesOrderItem line : bo.getSalesOrderItems()) {
            System.out.println(String.format("DocEntry: %s LineId: %s Status: %s DocumentStatus: %s Canceled: %s",
                    line.getDocEntry(), line.getLineId(), line.getStatus(), line.getLineStatus(), line.getCanceled()));

        }
    }

    public void TESTGC() throws IOException, InterruptedException {
        Runtime run = Runtime.getRuntime();
        // System.in.read(); // 暂停程序执行
        run.gc();
        System.out.println("time: " + (new Date()));
        // 获取开始时内存使用量
        long startMem = run.totalMemory() - run.freeMemory();
        System.out.println("S memory total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + startMem);
        int j = 0;
        SalesOrder[] orders = new SalesOrder[100000];
        for (int i = 0; i < orders.length; i++) {

            SalesOrder order = new SalesOrder();

            order.setDocEntry(1);
            order.setCustomerCode("C00001");
            order.setDeliveryDate(DateTime.getToday());
            order.setDocumentStatus(emDocumentStatus.Released);
            order.setDocumentTotal(new Decimal("99.99"));
            order.setDocumentUser(new User());
            order.setTeamUsers(new User[] { new User(), new User() });
            order.setCycle(new Time(1.05, emTimeUnit.hour));
            order.getCycle().setValue(0.9988);

            order.getUserFields().addUserField("U_OrderType", DbFieldType.db_Alphanumeric);
            order.getUserFields().addUserField("U_OrderId", DbFieldType.db_Numeric);
            order.getUserFields().addUserField("U_OrderDate", DbFieldType.db_Date);
            order.getUserFields().addUserField("U_OrderTotal", DbFieldType.db_Decimal);

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

            orders[i] = order;
            j++;
            if (j == 100) {
                // Thread.sleep(300);
                j = 0;
            }
        }

        System.out.println("time: " + (new Date()));
        long endMem = run.totalMemory() - run.freeMemory();
        System.out.println("D memory total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + endMem);

        run.gc();
        System.out.println("G memory total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:"
                + (run.totalMemory() - run.freeMemory()));
        orders = null;
        run.gc();
        long clearedMem = run.totalMemory() - run.freeMemory();
        System.out.println("C memory total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:"
                + (run.totalMemory() - run.freeMemory()));
        System.out.println("memory clear:" + (endMem - clearedMem));
        Thread.sleep(10000000);
    }
}
