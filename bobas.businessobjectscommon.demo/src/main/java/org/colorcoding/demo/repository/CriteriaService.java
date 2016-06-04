package org.colorcoding.demo.repository;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.colorcoding.bobas.common.ConditionOperation;
import org.colorcoding.bobas.common.ConditionRelationship;
import org.colorcoding.bobas.common.Criteria;
import org.colorcoding.bobas.common.IChildCriteria;
import org.colorcoding.bobas.common.ICondition;
import org.colorcoding.bobas.common.ISort;
import org.colorcoding.bobas.common.OperationResult;
import org.colorcoding.bobas.common.SortType;
import org.colorcoding.bobas.data.emDocumentStatus;

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
