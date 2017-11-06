package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 文件仓库
 * 
 * @author Niuren.Zhu
 *
 */
public class FileRepository extends FileRepositoryReadonly implements IFileRepository {

	protected static final String MSG_REPOSITORY_WRITE_FILE = "repository: writed file [%s].";

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
			throw new RepositoryException(I18N.prop("msg_bobas_invalid_data"));
		}
		File workFolder = new File(this.getRepositoryFolder());
		if (!workFolder.exists()) {
			workFolder.mkdirs();
		}
		FileData nFileData = new FileData();
		nFileData.setOriginalName(fileData.getOriginalName());
		nFileData.setFileName(fileData.getFileName());
		if (nFileData.getFileName() == null || nFileData.getFileName().isEmpty()) {
			String exName = null;
			int index = nFileData.getOriginalName().lastIndexOf(".");
			if (index > 0 && index < nFileData.getOriginalName().length()) {
				exName = nFileData.getOriginalName().substring(index);
			}
			nFileData
					.setFileName(exName != null ? UUID.randomUUID().toString() + exName : UUID.randomUUID().toString());
		}
		nFileData.setLocation(workFolder.getPath() + File.separator + nFileData.getFileName());
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
		Logger.log(MSG_REPOSITORY_WRITE_FILE, nFileData.getOriginalName() == null ? nFileData.getLocation()
				: String.format("%s|%s", nFileData.getOriginalName(), nFileData.getLocation()));
		return nFileData;
	}
}
