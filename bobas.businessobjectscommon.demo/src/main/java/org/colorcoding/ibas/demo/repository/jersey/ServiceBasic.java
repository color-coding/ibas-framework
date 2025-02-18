package org.colorcoding.ibas.demo.repository.jersey;

import java.math.BigDecimal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.SqlStatement;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

@Path("")
public class ServiceBasic {

	private static int total;
	private int count;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("hello")
	public String sayHello() {
		total++;
		count++;
		return String.format("Hello Jersey, total %s, count %s.", total, count);
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetch")
	public String fetch(Criteria criteria) {
		System.out.println("java.class.path");
		System.out.println(System.getProperty("java.class.path").replace(";", "\n"));
		System.out.println("java.library.path");
		System.out.println(System.getProperty("java.library.path").replace(";", "\n"));
		System.out.println("java.ext.dirs");
		System.out.println(System.getProperty("java.ext.dirs").replace(";", "\n"));
		System.out.println("user.dir");
		System.out.println(System.getProperty("user.dir").replace(";", "\n"));
		System.out.println(Strings.toJsonString(criteria));
		System.out.println(I18N.prop("msg_bobas_operation_successful"));
		return "ok.";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("create")
	public Criteria create() {
		Criteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpen(1);
		condition.setAlias("DocumentStatus");
		condition.setValue(emDocumentStatus.PLANNED);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias("DocumentStatus");
		condition.setValue(emDocumentStatus.RELEASED);
		condition.setRelationship(ConditionRelationship.OR);
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias("DocEntry");
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias("CustomerCode");
		sort.setSortType(SortType.ASCENDING);
		// 子项查询
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias("ItemCode");
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("T000");

		return criteria;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("result")
	public OperationResult<Criteria> result() {
		OperationResult<Criteria> operationResult = new OperationResult<Criteria>();
		operationResult.addResultObjects(this.create());
		operationResult.addResultObjects(this.create());
		operationResult.addInformations("INFO", "I'm Niuren.Zhu.");
		return operationResult;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchSalesOrder")
	public OperationResult<SalesOrder> fetchSalesOrder(Criteria criteria, @QueryParam("token") String token) {
		System.out.println(String.format("fetch by %s.", token));
		System.out.println(Strings.toXmlString(criteria));

		OperationResult<SalesOrder> operationResult = new OperationResult<SalesOrder>();
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTimes.today());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new BigDecimal("99.99"));

		order.getUserFields().register("U_OrderType", String.class);
		order.getUserFields().register("U_OrderId", Integer.class);
		order.getUserFields().register("U_OrderDate", DateTime.class);
		order.getUserFields().register("U_OrderTotal", BigDecimal.class);
		order.getUserFields().get("U_OrderType").setValue("S0000");
		order.getUserFields().get("U_OrderId").setValue(5768);
		order.getUserFields().get("U_OrderDate").setValue(DateTimes.today());
		order.getUserFields().get("U_OrderTotal").setValue(new BigDecimal("999.888"));
		order.setCanceled(emYesNo.YES);
		order.setDocumentStatus(emDocumentStatus.CLOSED);
		order.setStatus(emBOStatus.CLOSED);
		SalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new BigDecimal(10));
		orderItem.setPrice(BigDecimal.valueOf(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));

		// order.setDocumentUser(new User());
		// 此处json序列化存在问题，可能是jersey导致
		// order.setTeamUsers(new User[] { new User(), new User() });
		operationResult.addResultObjects(order);
		return operationResult;
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("saveSalesOrder")
	public String saveSalesOrder(SalesOrder bo, @QueryParam("token") String token) {
		System.out.println(String.format("save by %s.", token));
		System.out.println(Strings.toXmlString(bo));
		return "ok";
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("querySalesOrder")
	public OperationResult<DataTable> querySalesOrder(@QueryParam("token") String token) {
		OperationResult<DataTable> operationResult = new OperationResult<DataTable>();
		try {
			System.out.println(String.format("query by %s.", token));
			BORepositoryTest boRepository = new BORepositoryTest();
			SqlStatement sqlQuery = new SqlStatement();
			sqlQuery.setContent("select * FROM CC_TT_ORDR");
			// operationResult.copy(boRepository.query(sqlQuery));
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}
}
