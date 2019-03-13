package org.colorcoding.ibas.demo.repository.jersey;

import java.io.File;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.repository.jersey.FileRepositoryService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("files")
public class ServiceFile extends FileRepositoryService {

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationResult<FileData> upload(FormDataMultiPart formData, @QueryParam("token") String token) {
		return super.save(formData.getField("file"), token);
	}

	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void download(@QueryParam("token") String token, @Context HttpServletResponse response) {
		try {
			FileData fileData = new FileData();
			fileData.setLocation(Configuration.getStartupFolder() + File.separator + "app.xml");
			// 设置返回头，为文件命名
			response.setHeader("content-disposition", "attachment;filename=app.xml");
			// 设置内容类型
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			// 写入响应输出流
			OutputStream os = response.getOutputStream();
			os.write(fileData.getFileBytes());
			os.flush();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
}
