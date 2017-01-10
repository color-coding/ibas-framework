package org.colorcoding.ibas.demo.jersey.repository.cxf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.cxf.WebServicePath;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

/**
 * 基于soap方式的webservice
 * 
 * @author Niuren.Zhu
 *
 */
@WebService
@WebServicePath("/soap")
public class ServiceSoap extends BORepositoryTest {

	// --------------------------------------------------------------------------------------------//

	/**
	 * 查询-物料主数据
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	@WebMethod
	public OperationResult<Materials> fetchMaterials(@WebParam(name = "criteria") Criteria criteria,
			@WebParam(name = "token") String token) {
		return super.fetchMaterials(criteria, token);
	}

	/**
	 * 保存-物料主数据
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	@WebMethod
	public OperationResult<Materials> saveMaterials(@WebParam(name = "bo") Materials bo,
			@WebParam(name = "token") String token) {
		return super.saveMaterials(bo, token);
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-销售订单
	 * 
	 * @param criteria
	 *            查询
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	@WebMethod
	public OperationResult<SalesOrder> fetchSalesOrder(@WebParam(name = "criteria") Criteria criteria,
			@WebParam(name = "token") String token) {
		return super.fetchSalesOrder(criteria, token);
	}

	/**
	 * 保存-销售订单
	 * 
	 * @param bo
	 *            对象实例
	 * @param token
	 *            口令
	 * @return 操作结果
	 */
	@WebMethod
	public OperationResult<SalesOrder> saveSalesOrder(@WebParam(name = "bo") SalesOrder bo,
			@WebParam(name = "token") String token) {
		return super.saveSalesOrder(bo, token);
	}

	// --------------------------------------------------------------------------------------------//
}
