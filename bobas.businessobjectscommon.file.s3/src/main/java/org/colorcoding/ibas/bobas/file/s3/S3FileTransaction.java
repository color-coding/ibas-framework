package org.colorcoding.ibas.bobas.file.s3;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.UUID;
import java.util.function.Consumer;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.expression.FileItemJudgmentLink;
import org.colorcoding.ibas.bobas.file.FileData;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.file.FileTransaction;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.repository.RepositoryException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3FileTransaction extends FileTransaction {
	/**
	 * 配置项目-存储区域
	 */
	public final static String CONFIG_ITEM_FILE_REPOSITORY_FOLDER = MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER;
	/**
	 * 配置项目-存储访问端点
	 */
	public final static String CONFIG_ITEM_FILE_REPOSITORY_ENDPOINT = "FileRepositoryEndpoint";
	/**
	 * 配置项目-存储访问口令
	 */
	public final static String CONFIG_ITEM_FILE_REPOSITORY_USER = "FileRepositoryUser";
	/**
	 * 配置项目-存储访问密钥
	 */
	public final static String CONFIG_ITEM_FILE_REPOSITORY_PASSWORD = "FileRepositoryPassword";
	/**
	 * 配置项目-存储桶
	 */
	public final static String CONFIG_ITEM_S3_BUCKET = "S3Bucket";
	/**
	 * 配置项目-存储区域
	 */
	public final static String CONFIG_ITEM_S3_REGION = "S3Region";
	/**
	 * 配置项目-存储按路径访问
	 */
	public final static String CONFIG_ITEM_S3_PATH_STYLE_ACCESS = "S3PathStyleAccess";
	/**
	 * 路径隔离符
	 */
	public final static String PATH_SEPARATOR = Strings.VALUE_SLASH;

	private S3Client s3Client;

	public synchronized S3Client getS3Client() {
		if (this.s3Client == null) {
			synchronized (this) {
				if (this.s3Client == null) {
					try {
						this.initClient();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return s3Client;
	}

	public synchronized void setS3Client(S3Client s3Client) {
		this.s3Client = s3Client;
	}

	private String bucket;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void initClient(String endpoint, String accessKey, String secretKey, String region)
			throws RepositoryException {
		if (this.s3Client != null) {
			throw new RepositoryException(I18N.prop("msg_bobas_s3_client_already_exists"));
		}
		Region oRegion = Strings.isNullOrEmpty(region) ? Region.AWS_CN_GLOBAL : Region.of(region);
		Boolean pathStyleAccess = MyConfiguration.getConfigValue(CONFIG_ITEM_S3_PATH_STYLE_ACCESS, false);

		this.s3Client = S3Client.builder().endpointOverride(URI.create(endpoint)).region(oRegion)
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.httpClient(UrlConnectionHttpClient.create())
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(pathStyleAccess).build())
				.build();
	}

	public void initClient(String endpoint, String accessKey, String secretKey) throws RepositoryException {
		this.initClient(endpoint, accessKey, secretKey);
	}

	public void initClient() throws RepositoryException {
		String endpoint = MyConfiguration.getConfigValue(CONFIG_ITEM_FILE_REPOSITORY_ENDPOINT),
				accessKey = MyConfiguration.getConfigValue(CONFIG_ITEM_FILE_REPOSITORY_USER),
				secretKey = MyConfiguration.getConfigValue(CONFIG_ITEM_FILE_REPOSITORY_PASSWORD),
				region = MyConfiguration.getConfigValue(CONFIG_ITEM_S3_REGION);
		this.initClient(endpoint, accessKey, secretKey, region);
	}

	@Override
	public void close() throws Exception {
		super.close();
		if (this.s3Client != null) {
			this.s3Client.close();
		}
	}

	@Override
	protected FileItem save(FileData fileData) throws Exception {
		if (fileData == null || fileData.getStream() == null) {
			return null;
		}
		// 形成新文件名，保留扩展名
		if (Strings.isNullOrEmpty(fileData.getName())) {
			String tmpValue = Files.extensionOf(fileData.getOriginalName());
			fileData.setName(Strings.isNullOrEmpty(tmpValue) ? UUID.randomUUID().toString()
					: Strings.concat(UUID.randomUUID().toString(), Strings.VALUE_DOT, tmpValue.toLowerCase()));
		}
		if (!Strings.isNullOrEmpty(this.getRepositoryFolder())) {
			fileData.setLocation(Strings.concat(this.getRepositoryFolder(), PATH_SEPARATOR, fileData.getName()));
		} else {
			fileData.setLocation(fileData.getName());
		}
		PutObjectRequest request = PutObjectRequest.builder().bucket(this.getBucket()).key(fileData.getLocation())
				.build();
		this.getS3Client().putObject(request, RequestBody.fromBytes(Files.readAllBytes(fileData.getStream())));

		S3FileItem fileItem = new S3FileItem();
		fileItem.setFile(true);
		fileItem.setName(fileData.getName());
		fileItem.setPath(fileData.getLocation());
		fileItem.url = this.getS3Client().utilities()
				.getUrl(GetUrlRequest.builder().bucket(this.getBucket()).key(fileItem.getPath()).build());
		fileItem.loader = new Consumer<OutputStream>() {

			@Override
			public void accept(OutputStream outputStream) {
				try {
					GetObjectRequest request = GetObjectRequest.builder().bucket(S3FileTransaction.this.getBucket())
							.key(fileItem.getPath()).build();
					Files.writeTo(S3FileTransaction.this.getS3Client().getObject(request), outputStream);
				} catch (AwsServiceException | SdkClientException | IOException e) {
					throw new RuntimeException(e);
				}
			}

		};
		return fileItem;
	}

	@Override
	protected boolean delete(FileItem fileItem) throws Exception {
		try {
			// 先判断是否存在
			HeadObjectRequest headRequest = HeadObjectRequest.builder().bucket(this.getBucket()).key(fileItem.getPath())
					.build();
			this.getS3Client().headObject(headRequest);
			// 删除文件
			DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(this.getBucket()).key(fileItem.getPath())
					.build();
			this.getS3Client().deleteObject(request);
			if (fileItem instanceof S3FileItem) {
				((S3FileItem) fileItem).loader = null;
			}
			return true;
		} catch (NoSuchKeyException e) {
			return false;
		}
	}

	@Override
	protected List<FileItem> searchFiles(ICriteria criteria) throws Exception {
		if (criteria == null) {
			criteria = new Criteria();
		}
		List<ICondition> conditions;
		String fileName = Strings.VALUE_EMPTY;
		String workFolder = this.getRepositoryFolder();
		// 修正查询条件
		for (ICondition condition : criteria.getConditions()) {
			// 修改路径符号
			if (Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_PATH, condition.getAlias())
					|| Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_NAME, condition.getAlias())
					|| Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_FOLDER, condition.getAlias())) {
				if (!Strings.isNullOrEmpty(condition.getValue())) {
					condition.setValue(Files.pathOf(condition.getValue()).replace(File.separator, PATH_SEPARATOR));
					if (Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_FOLDER, condition.getAlias())) {
						if (!Strings.endsWith(condition.getValue(), PATH_SEPARATOR)) {
							condition.setValue(condition.getValue() + PATH_SEPARATOR);
						}
					}
				}
			}
		}
		// 是否包含子文件夹
		boolean include = false;
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_INCLUDE_SUBFOLDER.equals(c.getAlias()));
		for (ICondition condition : conditions) {
			emYesNo value = emYesNo.NO;
			if (condition.getValue().length() > 1)
				value = emYesNo.valueOf(condition.getValue());
			else {
				value = (emYesNo) Enums.valueOf(emYesNo.class, condition.getValue());
			}
			include = value == emYesNo.YES ? true : false;
			condition.setOperation(ConditionOperation.NONE);
		}
		// 是否指定工作目录
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_FILE_FOLDER.equals(c.getAlias()));
		if (conditions.size() == 1) {
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					// 指定查询文件夹，则更改工作目录
					workFolder = Strings.concat(workFolder, PATH_SEPARATOR, condition.getValue());
					condition.setOperation(ConditionOperation.NONE);
				}
			}
		} else if (conditions.size() > 0) {
			include = true;
			for (ICondition condition : conditions) {
				// 包含子目录的等于改为开始于
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					condition.setOperation(ConditionOperation.START);
				}
			}
		}
		// 是否包含文件路径
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_FILE_PATH.equals(c.getAlias()));
		if (conditions.size() > 0) {
			include = true;
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					if (Strings.indexOf(condition.getValue(), PATH_SEPARATOR) > 0) {
						condition.setOperation(ConditionOperation.END);
					} else {
						condition.setOperation(ConditionOperation.CONTAIN);
					}
				}
			}
		}
		// 是否指定的文件含目录
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_FILE_NAME.equals(c.getAlias()));
		if (conditions.size() == 1) {
			// 文件名查询，条件仅一个时
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					// 指定文件路径，则更改工作目录
					if (Strings.indexOf(condition.getValue(), PATH_SEPARATOR) > 0) {
						workFolder = Strings.concat(workFolder, PATH_SEPARATOR,
								condition.getValue().substring(0, condition.getValue().lastIndexOf(PATH_SEPARATOR)));
						condition.setValue(
								condition.getValue().substring(condition.getValue().lastIndexOf(PATH_SEPARATOR) + 1));
						fileName = condition.getValue();
					} else {
						fileName = condition.getValue();
					}
				}
			}
		} else {
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					if (Strings.indexOf(condition.getValue(), PATH_SEPARATOR) > 0) {
						include = true;
						// 有路径，则改成路径比较
						condition.setAlias(CONDITION_ALIAS_FILE_PATH);
						condition.setOperation(ConditionOperation.END);
					}
				}
			}
		}
		// 重建查询
		Criteria nCriteria = new Criteria();
		nCriteria.setResultCount(criteria.getResultCount());
		for (int i = 0; i < criteria.getConditions().size(); i++) {
			ICondition condition = criteria.getConditions().get(i);
			if (condition.getOperation() == ConditionOperation.NONE) {
				if (i > 0 && condition.getBracketClose() > 0) {
					// 闭括号后移
					criteria.getConditions().get(i - 1).setBracketClose(condition.getBracketClose());
				}
				if (i < criteria.getConditions().size() - 1 && condition.getBracketOpen() > 0) {
					// 开括号前移
					criteria.getConditions().get(i + 1).setBracketOpen(condition.getBracketOpen());
				}
				continue;
			}
			nCriteria.getConditions().add(condition);
		}
		for (ISort sort : criteria.getSorts()) {
			nCriteria.getSorts().add(sort);
		}
		// 格式化变量
		workFolder = Files.pathOf(workFolder).replace(File.separator, PATH_SEPARATOR);
		if (Strings.startsWith(workFolder, PATH_SEPARATOR)) {
			workFolder = workFolder.substring(1);
		}
		if (!Strings.isNullOrEmpty(workFolder) && !Strings.endsWith(workFolder, PATH_SEPARATOR)) {
			workFolder = Strings.concat(workFolder, PATH_SEPARATOR);
		}
		// 指定了文件名称，则直接查
		if (!Strings.isNullOrEmpty(fileName)) {
			workFolder = Files.pathOf(workFolder, fileName).replace(File.separator, PATH_SEPARATOR);
			include = true;
		}

		// 查询符合条件的文件
		ArrayList<FileItem> fileItems = new ArrayList<>();
		ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(this.getBucket()).prefix(workFolder)
				// 是否查询子目录
				.delimiter(!include ? PATH_SEPARATOR : Strings.VALUE_EMPTY).build();

		FileItemJudgmentLink judgmentLinks = new FileItemJudgmentLink();
		judgmentLinks.parsingConditions(nCriteria.getConditions());

		java.util.List<S3Object> contents = this.getS3Client().listObjectsV2(request).contents();
		if (contents == null) {
			return fileItems;
		}
		if (MyConfiguration.isDebugMode()) {
			Logger.log(MessageLevel.DEBUG, "s3client: filter [%s%s] got [%s] files.", workFolder,
					Strings.VALUE_ASTERISK, contents.size());
		}
		for (S3Object s3Object : contents) {
			S3FileItem fileItem = new S3FileItem();
			fileItem.setFile(true);
			fileItem.setPath(s3Object.key());
			fileItem.setName(Files.nameOf(fileItem.getPath()));
			fileItem.setModifyTime(DateTimes.valueOf(s3Object.lastModified().toEpochMilli()));
			if (!judgmentLinks.judge(fileItem)) {
				continue;
			}
			fileItem.url = this.getS3Client().utilities()
					.getUrl(GetUrlRequest.builder().bucket(this.getBucket()).key(fileItem.getPath()).build());
			fileItem.loader = new Consumer<OutputStream>() {

				@Override
				public void accept(OutputStream outputStream) {
					try {
						GetObjectRequest request = GetObjectRequest.builder().bucket(S3FileTransaction.this.getBucket())
								.key(fileItem.getPath()).build();
						Files.writeTo(S3FileTransaction.this.getS3Client().getObject(request), outputStream);
					} catch (AwsServiceException | SdkClientException | IOException e) {
						throw new RuntimeException(e);
					}
				}

			};
			fileItems.add(fileItem);

			if (criteria.getResultCount() > 0 && fileItems.size() >= criteria.getResultCount()) {
				break;
			}
		}
		return fileItems;
	}

}
