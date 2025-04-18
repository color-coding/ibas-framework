package org.colorcoding.ibas.demo.service.jersey;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileItem;
import org.colorcoding.ibas.bobas.repository.FileRepository;
import org.colorcoding.ibas.bobas.repository.jersey.FileRepositoryService;
import org.colorcoding.ibas.demo.MyConfiguration;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("files")
public class ServiceFile extends FileRepositoryService {

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationResult<FileItem> upload(FormDataMultiPart formData,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.save(formData.getField("file"), MyConfiguration.optToken(authorization, token));
	}

	@POST
	@Path("fetch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationResult<FileItem> fetch(Criteria criteria, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token) {
		return super.fetch(criteria, MyConfiguration.optToken(authorization, token));
	}

	@POST
	@Path("download")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void download(Criteria criteria, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token, @Context HttpServletResponse response) {
		try {
			if (criteria == null || criteria.getConditions().isEmpty()) {
				throw new WebApplicationException(400);
			}
			if (criteria.getResultCount() <= 0) {
				criteria.setResultCount(1);
			}
			token = MyConfiguration.optToken(authorization, token);
			// 获取文件
			IOperationResult<FileItem> operationResult = this.fetch(criteria, token);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			if (operationResult.getResultObjects().isEmpty()) {
				throw new WebApplicationException(404);
			}
			for (FileItem fileItem : operationResult.getResultObjects()) {
				// 设置文件名
				response.setHeader("Content-Disposition", String.format("attachment;filename=%s", fileItem.getName()));
				// 设置内容类型
				response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				// 写入响应输出流
				fileItem.writeTo(response.getOutputStream());
				// 提交输出流
				response.getOutputStream().flush();
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("{resource}")
	public void resource(@PathParam("resource") String resource, @QueryParam("token") String token,
			@Context HttpServletResponse response) {
		try {
			Criteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
			condition.setValue(resource);
			// 获取文件
			OperationResult<FileItem> operationResult = this.fetch(criteria, token);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			if (operationResult.getResultObjects().isEmpty()) {
				throw new WebApplicationException(404);
			}
			for (FileItem fileItem : operationResult.getResultObjects()) {
				// 设置文件名
				response.setHeader("Content-Disposition", String.format("attachment;filename=%s", fileItem.getName()));
				// 设置内容类型
				response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				// 写入响应输出流
				fileItem.writeTo(response.getOutputStream());
				// 提交输出流
				response.getOutputStream().flush();
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
}
