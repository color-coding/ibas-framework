package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 文件仓库
 * 
 * @author Niuren.Zhu
 *
 */
public class FileRepository extends FileRepositoryReadonly implements IFileRepository {

	@Override
	public IOperationResult<FileData> save(FileData fileData) {
		OperationResult<FileData> operationResult = new OperationResult<>();
		try {
			operationResult.addResultObjects(this.writeFile(fileData));
			return operationResult;
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 写文件
	 * 
	 * @param fileData
	 *            被写入的文件数据
	 * @return 新的文件数据
	 * @throws Exception
	 */
	private FileData writeFile(FileData fileData) throws Exception {
		if (fileData == null || fileData.getStream() == null) {
			throw new RepositoryException(i18n.prop("msg_bobas_invalid_data"));
		}
		FileData nFileData = new FileData();
		if (fileData.getFileName() != null && !fileData.getFileName().isEmpty())
			nFileData.setFileName(fileData.getFileName() + "_" + UUID.randomUUID().toString());
		else
			nFileData.setFileName(UUID.randomUUID().toString());
		nFileData.setLocation(this.getRepositoryFolder() + File.separator + nFileData.getFileName());
		OutputStream outputStream = new FileOutputStream(nFileData.getLocation());
		try {
			int bytesRead = 0;
			byte[] buffer = new byte[512];
			while ((bytesRead = fileData.getStream().read(buffer, 0, buffer.length)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} finally {
			outputStream.close();
			fileData.getStream().close();
		}
		RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_WRITE_FILE, nFileData.getLocation());
		return nFileData;
	}
}
