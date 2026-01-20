package org.colorcoding.ibas.bobas.repository.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.data.FileItem;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

public class FileRepositoryService extends org.colorcoding.ibas.bobas.repository.FileRepositoryService {

	/**
	 * 保存提交内容为文件
	 * 
	 * @param bodyPart 提交内容
	 * @param token    用户口令
	 * @return
	 */
	public OperationResult<FileItem> save(FormDataBodyPart bodyPart, String token) {
		try {
			FileData fileData = new FileData();
			fileData.setStream(bodyPart.getValueAs(InputStream.class));
			fileData.setOriginalName(URLDecoder.decode(bodyPart.getContentDisposition().getFileName(), "UTF-8"));
			return new OperationResult<>(super.save(fileData, token));
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 获取文件内容类型
	 * 
	 * @param fileData 文件数据
	 * @return
	 */
	protected String getContentType(FileData fileData) {
		return this.getContentType(fileData.getLocation());
	}

	/**
	 * 获取文件内容类型
	 * 
	 * @param fileItem 文件项目
	 * @return
	 */
	protected String getContentType(FileItem fileItem) {
		return this.getContentType(fileItem.getPath());
	}

	/**
	 * 获取文件内容类型
	 * 
	 * @param filePath 文件地址
	 * @return
	 */
	protected String getContentType(String filePath) {
		try {
			return Files.probeContentType(Paths.get(filePath));
		} catch (IOException e) {
			return null;
		}
	}
}
