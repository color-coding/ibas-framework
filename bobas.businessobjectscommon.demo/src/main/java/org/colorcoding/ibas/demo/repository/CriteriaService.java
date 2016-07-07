package org.colorcoding.ibas.demo.repository;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.i18n.i18n;

@Path("/")
public class CriteriaService {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String sayHello() {
		return "Hello Jersey";
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/fetch")
	public String fetch(Criteria criteria) {
		System.out.println(criteria.toString("xml"));
		System.out.println(i18n.prop("msg_bobas_operation_successful"));
		return "ok.";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create")
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
	@Path("/result")
	public OperationResult<Criteria> result() {
		OperationResult<Criteria> operationResult = new OperationResult<Criteria>();
		operationResult.addResultObjects(this.create());
		operationResult.addResultObjects(this.create());
		return operationResult;
	}
}
