package org.colorcoding.ibas.demo.service.jersey;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerXml;
import org.colorcoding.ibas.demo.MyConfiguration;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.bo.materialsjournal.MaterialsJournal;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;
import org.colorcoding.ibas.demo.bo.user.User;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

/**
 * 数据服务XML
 */
@Path("xml")
public class ServiceXml extends BORepositoryTrainingTesting {

	@GET
	@Path("schema")
	@Consumes(MediaType.APPLICATION_XML)
	public void schema(@QueryParam("boCode") String boCode, @Context HttpServletResponse response) {
		try {
			// 获取对象类并加载
			for (Class<?> item : BOFactory.loadClasses("org.colorcoding.ibas.demo.bo")) {
				Class.forName(item.getName());
			}
			// 通过Code获取对象类型
			Class<?> boClass = BOFactory.classOf(boCode);
			ISerializer serializer = new SerializerXml();
			try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
				serializer.schema(boClass, writer);
				writer.writeTo(response.getOutputStream());
				response.getOutputStream().flush();
			}
		} catch (Exception e) {
			Logger.log(e);
			throw new WebApplicationException(e);
		}
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-物料主数据
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("fetchMaterials")
	public OperationResult<Materials> fetchMaterials(Criteria criteria,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.fetchMaterials(criteria, MyConfiguration.optToken(authorization, token));
	}

	/**
	 * 保存-物料主数据
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("saveMaterials")
	public OperationResult<Materials> saveMaterials(Materials bo, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token) {
		return super.saveMaterials(bo, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-销售订单
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("fetchSalesOrder")
	public OperationResult<SalesOrder> fetchSalesOrder(Criteria criteria,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.fetchSalesOrder(criteria, MyConfiguration.optToken(authorization, token));
	}

	/**
	 * 保存-销售订单
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("saveSalesOrder")
	public OperationResult<SalesOrder> saveSalesOrder(SalesOrder bo, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token) {
		return super.saveSalesOrder(bo, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-用户
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("fetchUser")
	public OperationResult<User> fetchUser(Criteria criteria, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token) {
		return super.fetchUser(criteria, MyConfiguration.optToken(authorization, token));
	}

	/**
	 * 保存-用户
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("saveUser")
	public OperationResult<User> saveUser(User bo, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token) {
		return super.saveUser(bo, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//
	/**
	 * 查询-仓库日记账
	 * 
	 * @param criteria 查询
	 * @param token    口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("fetchMaterialsJournal")
	public OperationResult<MaterialsJournal> fetchMaterialsJournal(Criteria criteria,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.fetchMaterialsJournal(criteria, MyConfiguration.optToken(authorization, token));
	}

	/**
	 * 保存-仓库日记账
	 * 
	 * @param bo    对象实例
	 * @param token 口令
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("saveMaterialsJournal")
	public OperationResult<MaterialsJournal> saveMaterialsJournal(MaterialsJournal bo,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.saveMaterialsJournal(bo, MyConfiguration.optToken(authorization, token));
	}

	// --------------------------------------------------------------------------------------------//
}
