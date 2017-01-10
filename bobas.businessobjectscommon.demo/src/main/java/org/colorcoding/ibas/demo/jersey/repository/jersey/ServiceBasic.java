package org.colorcoding.ibas.demo.jersey.repository.jersey;

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
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.repository.BORepository4DbReadonly;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

@Path("")
public class ServiceBasic {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("hello")
	public String sayHello() {
		return "Hello Jersey";
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
		System.out.println(criteria.toString("xml"));
		System.out.println(i18n.prop("msg_bobas_operation_successful"));
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
		condition.setBracketOpenNum(1);
		condition.setAlias("DocumentStatus");
		condition.setCondVal(emDocumentStatus.Planned);
		condition = criteria.getConditions().create();
		condition.setBracketCloseNum(1);
		condition.setAlias("DocumentStatus");
		condition.setCondVal(emDocumentStatus.Released);
		condition.setRelationship(ConditionRelationship.cr_OR);
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias("DocEntry");
		sort.setSortType(SortType.st_Descending);
		sort = criteria.getSorts().create();
		sort.setAlias("CustomerCode");
		sort.setSortType(SortType.st_Ascending);
		// 子项查询
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias("ItemCode");
		condition.setOperation(ConditionOperation.co_CONTAIN);
		condition.setCondVal("T000");

		return criteria;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("result")
	public OperationResult<Criteria> result() {
		OperationResult<Criteria> operationResult = new OperationResult<Criteria>();
		operationResult.addResultObjects(this.create());
		operationResult.addResultObjects(this.create());
		return operationResult;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchSalesOrder")
	public OperationResult<SalesOrder> fetchSalesOrder(Criteria criteria, @QueryParam("token") String token) {
		System.out.println(String.format("fetch by %s.", token));
		System.out.println(criteria.toString("xml"));

		OperationResult<SalesOrder> operationResult = new OperationResult<SalesOrder>();
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.Released);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setCycle(new Time(1.05, emTimeUnit.hour));

		order.getUserFields().addUserField("U_OrderType", DbFieldType.db_Alphanumeric);
		order.getUserFields().addUserField("U_OrderId", DbFieldType.db_Numeric);
		order.getUserFields().addUserField("U_OrderDate", DbFieldType.db_Date);
		order.getUserFields().addUserField("U_OrderTotal", DbFieldType.db_Decimal);
		order.getUserFields().setValue("U_OrderType", "S0000");
		order.getUserFields().setValue("U_OrderId", 5768);
		order.getUserFields().setValue("U_OrderDate", DateTime.getToday());
		order.getUserFields().setValue("U_OrderTotal", new Decimal("999.888"));
		order.setCanceled(emYesNo.Yes);
		order.setDocumentStatus(emDocumentStatus.Closed);
		order.setStatus(emBOStatus.Closed);
		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new Decimal(10));
		orderItem.setPrice(new Decimal(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);

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
		System.out.println(bo.toString("xml"));
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
			IBORepository4DbReadonly boRepository = new BORepository4DbReadonly();
			// boRepository.connectDb("mssql", "localhost", "ibas_demo", "sa",
			// "1q2w3e");
			ISqlQuery sqlQuery = new SqlQuery();
			sqlQuery.setQueryString("select * from cc_tt_ordr");
			operationResult.copy(boRepository.query(sqlQuery));
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}
}
