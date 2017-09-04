package org.colorcoding.ibas.demo.repository.jersey;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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
import org.colorcoding.ibas.bobas.configuration.IConfigurationManager;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.repository.jersey.FileRepositoryService;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.ISerializerManager;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("files")
public class ServiceFile extends FileRepositoryService {

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationResult<FileData> upload(@FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition, @QueryParam("token") String token) {
		return super.save(fileStream, fileDisposition, token);
	}

	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] download(@QueryParam("format") String format, @Context HttpServletResponse response) {
		if (format == null || format.isEmpty()) {
			throw new WebApplicationException(502);
		}
		ISerializerManager manager = SerializerFactory.create().createManager();
		ISerializer<?> serializer = manager.create(format);
		if (serializer == null) {
			throw new WebApplicationException(502);
		}
		IConfigurationManager configuration = Configuration.create();
		if (configuration == null) {
			throw new WebApplicationException(502);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		serializer.serialize(configuration, outputStream, true);
		// ResponseBuilder response = Response.ok();
		response.setHeader("content-disposition", "attachment;filename=app.xml");// 为文件命名
		response.addHeader("content-type", "application/xml");
		return outputStream.toByteArray();
	}
}
