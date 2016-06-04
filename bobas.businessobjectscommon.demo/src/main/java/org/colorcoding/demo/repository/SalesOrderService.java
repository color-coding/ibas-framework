package org.colorcoding.demo.repository;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.colorcoding.bobas.common.Criteria;
import org.colorcoding.bobas.common.OperationResult;
import org.colorcoding.bobas.data.DateTime;
import org.colorcoding.bobas.data.Decimal;
import org.colorcoding.bobas.data.emDocumentStatus;
import org.colorcoding.bobas.data.measurement.Time;
import org.colorcoding.bobas.data.measurement.emTimeUnit;
import org.colorcoding.bobas.mapping.db.DbFieldType;
import org.colorcoding.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.bobas.test.bo.SalesOrder;
import org.colorcoding.bobas.test.bo.User;

@Path("/")
public class SalesOrderService {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/fetchSalesOrder")
	public OperationResult<SalesOrder> fetchSalesOrder(Criteria criteria, @QueryParam("token") String token) {
		System.out.println(criteria.toString("xml"));
		System.out.println(token);

		OperationResult<SalesOrder> operationResult = new OperationResult<SalesOrder>();
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.Released);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setDocumentUser(new User());
		order.setTeamUsers(new User[] { new User(), new User() });
		order.setCycle(new Time(1.05, emTimeUnit.hour));

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

		order.setDocumentUser(new User());
		// 此处json序列化存在问题，可能是jersey导致
		// order.setTeamUsers(new User[] { new User(), new User() });
		operationResult.addResultObjects(order);
		return operationResult;
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/saveSalesOrder")
	public String saveSalesOrder(SalesOrder bo, @QueryParam("token") String token) {
		System.out.println(bo.toString("xml"));
		return "ok";
	}
}
