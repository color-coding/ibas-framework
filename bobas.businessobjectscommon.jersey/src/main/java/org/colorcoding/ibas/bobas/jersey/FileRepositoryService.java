package org.colorcoding.ibas.bobas.jersey;

import java.io.InputStream;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class FileRepositoryService extends org.colorcoding.ibas.bobas.repository.FileRepositoryService {

	public OperationResult<FileData> save(InputStream fileStream, FormDataContentDisposition fileDisposition,
			String token) {
		try {
			FileData fileData = new FileData();
			fileData.setStream(fileStream);
			// fileData.setFileName(fileDisposition.getFileName());
			return new OperationResult<>(super.save(fileData, token));
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}
}
