package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.colorcoding.ibas.bobas.common.ICriteria;
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
	protected static final String MSG_REPOSITORY_DELETE_FILE = "repository: deleted file [%s].";

	@Override
	public IOperationResult<FileData> save(FileData fileData) {
		try {
			OperationResult<FileData> operationResult = new OperationResult<>();
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
		File ouFile = new File(nFileData.getLocation());
		if (!ouFile.exists()) {
			ouFile.getParentFile().mkdirs();
		}
		OutputStream outputStream = new FileOutputStream(ouFile);
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

	@Override
	public IOperationResult<FileData> delete(ICriteria criteria) {
		try {
			if (criteria == null || criteria.getConditions().isEmpty()) {
				// 没有条件，不允许删除
				return new OperationResult<>();
			}
			IOperationResult<FileData> opRsltFetch = this.fetch(criteria);
			if (opRsltFetch.getError() != null) {
				throw opRsltFetch.getError();
			}
			OperationResult<FileData> operationResult = new OperationResult<>();
			for (FileData item : opRsltFetch.getResultObjects()) {
				File file = new File(item.getLocation());
				if (file.exists()) {
					file.delete();
					Logger.log(MSG_REPOSITORY_DELETE_FILE, file.getPath());
					operationResult.addResultObjects(item);
				}
			}
			return operationResult;
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}
}
