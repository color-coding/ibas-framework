package org.colorcoding.ibas.demo.jersey.repository.jersey;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

/**
 * 数据服务JSON
 */
@Path("json/")
public class ServiceJson extends BORepositoryTest {

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
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("fetchMaterials")
    public OperationResult<Materials> fetchMaterials(Criteria criteria, @QueryParam("token") String token) {
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
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("saveMaterials")
    public OperationResult<Materials> saveMaterials(Materials bo, @QueryParam("token") String token) {
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
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("fetchSalesOrder")
    public OperationResult<SalesOrder> fetchSalesOrder(Criteria criteria, @QueryParam("token") String token) {
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
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("saveSalesOrder")
    public OperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, @QueryParam("token") String token) {
        return super.saveSalesOrder(bo, token);
    }

    // --------------------------------------------------------------------------------------------//

}
