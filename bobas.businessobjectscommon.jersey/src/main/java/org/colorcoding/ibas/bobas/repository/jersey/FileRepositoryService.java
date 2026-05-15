package org.colorcoding.ibas.bobas.repository.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.file.FileData;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

public class FileRepositoryService extends org.colorcoding.ibas.bobas.repository.FileRepositoryService {

	public FileRepositoryService() {
		this(Strings.VALUE_EMPTY);
	}

	public FileRepositoryService(String fileSign) {
		super(fileSign);
	}

	/**
	 * 保存提交内容为文件，文件名按UTF-8解码，异常包装为OperationResult返回
	 *
	 * @param bodyPart 提交内容
	 * @param token    用户口令
	 * @return 操作结果，包含文件项或错误信息
	 */
	public OperationResult<FileItem> save(FormDataBodyPart bodyPart, String token) {
		try (FileData fileData = new FileData(bodyPart.getValueAs(InputStream.class))) {
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
	 * @return 内容类型字符串，探测失败返回null
	 */
	protected String getContentType(FileData fileData) {
		return this.getContentType(fileData.getLocation());
	}

	/**
	 * 获取文件内容类型
	 * 
	 * @param fileItem 文件项目
	 * @return 内容类型字符串，探测失败返回null
	 */
	protected String getContentType(FileItem fileItem) {
		return this.getContentType(fileItem.getPath());
	}

	/**
	 * 获取文件内容类型
	 * 
	 * @param filePath 文件地址
	 * @return 内容类型字符串，探测失败返回null
	 */
	protected String getContentType(String filePath) {
		try {
			return Files.probeContentType(Paths.get(filePath));
		} catch (IOException e) {
			return null;
		}
	}
}
