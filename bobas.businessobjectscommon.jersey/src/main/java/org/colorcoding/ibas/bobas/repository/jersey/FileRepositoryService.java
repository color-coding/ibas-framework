package org.colorcoding.ibas.bobas.repository.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class FileRepositoryService extends org.colorcoding.ibas.bobas.repository.FileRepositoryService {

	public OperationResult<FileData> save(InputStream fileStream, FormDataContentDisposition fileDisposition,
			String token) {
		try {
			FileData fileData = new FileData();
			fileData.setStream(fileStream);
			fileData.setOriginalName(URLDecoder.decode(fileDisposition.getFileName(), "UTF-8"));
			return new OperationResult<>(super.save(fileData, token));
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	protected String getContentType(FileData fileData) {
		return this.getContentType(fileData.getLocation());
	}

	protected String getContentType(String filePath) {
		try {
			return Files.probeContentType(Paths.get(filePath));
		} catch (IOException e) {
			return null;
		}
	}
}
