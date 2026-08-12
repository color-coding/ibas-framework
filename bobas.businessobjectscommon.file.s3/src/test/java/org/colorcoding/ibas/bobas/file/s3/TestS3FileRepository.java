package org.colorcoding.ibas.bobas.file.s3;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.file.FileData;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.repository.FileRepository;

import junit.framework.TestCase;

/**
 * S3文件仓库功能测试
 *
 * 测试范围： 1. S3FileItem 属性与方法（toUrl/writeTo/类型判断/url和loader包级访问） 2.
 * TransactionManager 创建事务（配置检查/异常处理/repository参数传递） 3. S3FileTransaction
 * 配置常量与客户端初始化（重复初始化异常） 4. FileData 构造与属性（File/FileItem/手动设置） 5.
 * 集成测试：保存/搜索/删除文件（需S3配置）
 */
public class TestS3FileRepository extends TestCase {

	// ==================== 1. S3FileItem 单元测试 ====================

	/**
	 * 测试S3FileItem基本属性 覆盖：name/path/file/folder
	 */
	public void testS3FileItemProperties() {
		S3FileItem item = new S3FileItem();
		item.setName("test.txt");
		item.setPath("folder/test.txt");
		item.setFile(true);

		assertEquals("Name should match. ", "test.txt", item.getName());
		assertEquals("Path should match. ", "folder/test.txt", item.getPath());
		assertTrue("Should be file. ", item.isFile());
		assertFalse("Should not be folder. ", item.isFolder());
	}

	/**
	 * 测试S3FileItem的toUrl方法 覆盖：url为null时返回path、url不为null时返回URL字符串
	 */
	public void testS3FileItemToUrl() {
		S3FileItem item = new S3FileItem();
		item.setPath("folder/test.txt");

		// url为null时，toUrl返回path
		assertNull("URL should be null initially. ", item.url);
		assertEquals("toUrl should return path when url is null. ", "folder/test.txt", item.toUrl());

		// 设置url后，toUrl返回URL字符串
		try {
			item.url = new URL("https://bucket.s3.amazonaws.com/folder/test.txt");
			assertEquals("toUrl should return URL string. ", "https://bucket.s3.amazonaws.com/folder/test.txt",
					item.toUrl());
		} catch (Exception e) {
			System.out.println("URL construction skipped: " + e.getMessage());
		}
	}

	/**
	 * 测试S3FileItem的writeTo方法-无loader 覆盖：loader未设置时抛出RuntimeException
	 */
	public void testS3FileItemWriteToWithoutLoader() {
		S3FileItem item = new S3FileItem();
		item.setPath("test.txt");

		assertNull("Loader should be null initially. ", item.loader);
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			item.writeTo(output);
			fail("Should throw RuntimeException when loader is not set.");
		} catch (RuntimeException e) {
			assertNotNull("Exception message should not be null. ", e.getMessage());
		} catch (IOException e) {
			fail("Should throw RuntimeException, not IOException.");
		}
	}

	/**
	 * 测试S3FileItem的writeTo方法-有loader 覆盖：设置loader后可以正常写入
	 */
	public void testS3FileItemWriteToWithLoader() throws IOException {
		S3FileItem item = new S3FileItem();
		item.setPath("test.txt");

		final String testContent = "Hello, S3!";
		item.loader = outputStream -> {
			try {
				outputStream.write(testContent.getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		item.writeTo(output);
		assertEquals("Written content should match. ", testContent, output.toString());
	}

	/**
	 * 测试S3FileItem的loader生命周期 覆盖：设置loader→使用→清除
	 */
	public void testS3FileItemLoaderLifecycle() throws IOException {
		S3FileItem item = new S3FileItem();
		item.setPath("test.txt");

		item.loader = outputStream -> {
			try {
				outputStream.write("data".getBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
		assertNotNull("Loader should be set. ", item.loader);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		item.writeTo(output);
		assertEquals("Content should match. ", "data", output.toString());

		// 模拟finalize清理
		item.loader = null;
		assertNull("Loader should be null after cleanup. ", item.loader);
	}

	/**
	 * 测试S3FileItem的类型判断组合 覆盖：isFile/isFolder不同组合
	 */
	public void testS3FileItemTypeFlags() {
		S3FileItem fileItem = new S3FileItem();
		fileItem.setFile(true);
		assertTrue("Should be file. ", fileItem.isFile());
		assertFalse("Should not be folder. ", fileItem.isFolder());

		S3FileItem folderItem = new S3FileItem();
		folderItem.setFolder(true);
		assertFalse("Should not be file. ", folderItem.isFile());
		assertTrue("Should be folder. ", folderItem.isFolder());
	}

	/**
	 * 测试S3FileItem继承FileItem的relativePath 覆盖：设置maskFolder后的相对路径计算
	 */
	public void testS3FileItemRelativePath() {
		S3FileItem item = new S3FileItem();
		item.setPath("/data/bucket/folder/test.txt");
		item.setMaskFolder("/data/bucket");

		String relativePath = item.relativePath();
		assertNotNull("Relative path should not be null. ", relativePath);
		assertTrue("Relative path should contain folder/test.txt. ", relativePath.contains("folder/test.txt"));
	}

	// ==================== 2. TransactionManager 单元测试 ====================

	/**
	 * 测试TransactionManager-未配置存储桶时异常 覆盖：无S3Bucket配置时抛出RuntimeException
	 */
	public void testTransactionManagerNoBucket() {
		TransactionManager manager = new TransactionManager();

		String originalBucket = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER);
		String originalS3Bucket = MyConfiguration.getConfigValue(S3FileTransaction.CONFIG_ITEM_S3_BUCKET);

		System.out.println("CONFIG_ITEM_FILE_REPOSITORY_FOLDER: " + originalBucket);
		System.out.println("CONFIG_ITEM_S3_BUCKET: " + originalS3Bucket);

		if (Strings.isNullOrEmpty(originalBucket) && Strings.isNullOrEmpty(originalS3Bucket)) {
			try {
				manager.createTransaction(null);
				fail("Should throw RuntimeException when bucket is not configured.");
			} catch (RuntimeException e) {
				System.out.println("Expected exception: " + e.getMessage());
			}
		} else {
			System.out.println("S3 bucket is configured, skipping no-bucket test.");
		}
	}

	/**
	 * 测试TransactionManager-创建S3FileTransaction 覆盖：有配置时创建的事务类型和属性
	 */
	public void testTransactionManagerCreate() {
		String bucket = MyConfiguration.getConfigValue(S3FileTransaction.CONFIG_ITEM_S3_BUCKET,
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER));

		if (!Strings.isNullOrEmpty(bucket)) {
			TransactionManager manager = new TransactionManager();
			try {
				Object transaction = manager.createTransaction(null);
				assertNotNull("Transaction should not be null. ", transaction);
				assertTrue("Transaction should be S3FileTransaction. ", transaction instanceof S3FileTransaction);

				S3FileTransaction s3Transaction = (S3FileTransaction) transaction;
				assertEquals("Bucket should match config. ", bucket, s3Transaction.getBucket());
				assertFalse("Should not group files. ", s3Transaction.isGroupingFiles());
				s3Transaction.close();
			} catch (Exception e) {
				System.out.println("S3 connection error (expected without S3 service): " + e.getMessage());
			}
		} else {
			System.out.println("S3 bucket not configured, skipping create test.");
		}
	}

	/**
	 * 测试TransactionManager-指定仓储路径 覆盖：repository参数传递到事务的repositoryFolder，路径分隔符转换
	 */
	public void testTransactionManagerWithRepository() {
		String bucket = MyConfiguration.getConfigValue(S3FileTransaction.CONFIG_ITEM_S3_BUCKET,
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER));

		if (!Strings.isNullOrEmpty(bucket)) {
			TransactionManager manager = new TransactionManager();
			try {
				S3FileTransaction transaction = (S3FileTransaction) manager.createTransaction("my-folder");
				assertNotNull("Transaction should not be null. ", transaction);
				String repoFolder = transaction.getRepositoryFolder();
				assertNotNull("Repository folder should not be null. ", repoFolder);
				assertTrue("Repository folder should contain my-folder. ", repoFolder.contains("my-folder"));
				assertFalse("Repository folder should use / not \\. ", repoFolder.contains("\\"));
				transaction.close();
			} catch (Exception e) {
				System.out.println("S3 connection error: " + e.getMessage());
			}
		} else {
			System.out.println("S3 bucket not configured, skipping repository test.");
		}
	}

	// ==================== 3. S3FileTransaction 常量与客户端测试 ====================

	/**
	 * 测试S3FileTransaction配置常量 覆盖：所有配置项常量值定义正确
	 */
	public void testS3FileTransactionConstants() {
		assertEquals("Path separator should be '/'. ", Strings.VALUE_SLASH, S3FileTransaction.PATH_SEPARATOR);
		assertEquals("CONFIG_ITEM_FILE_REPOSITORY_FOLDER. ", MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER,
				S3FileTransaction.CONFIG_ITEM_FILE_REPOSITORY_FOLDER);
		assertEquals("Endpoint config key. ", "FileRepositoryEndpoint",
				S3FileTransaction.CONFIG_ITEM_FILE_REPOSITORY_ENDPOINT);
		assertEquals("User config key. ", "FileRepositoryUser", S3FileTransaction.CONFIG_ITEM_FILE_REPOSITORY_USER);
		assertEquals("Password config key. ", "FileRepositoryPassword",
				S3FileTransaction.CONFIG_ITEM_FILE_REPOSITORY_PASSWORD);
		assertEquals("Bucket config key. ", "S3Bucket", S3FileTransaction.CONFIG_ITEM_S3_BUCKET);
		assertEquals("Region config key. ", "S3Region", S3FileTransaction.CONFIG_ITEM_S3_REGION);
		assertEquals("Path style access config key. ", "S3PathStyleAccess",
				S3FileTransaction.CONFIG_ITEM_S3_PATH_STYLE_ACCESS);
	}

	/**
	 * 测试S3FileTransaction重复初始化客户端 覆盖：已存在的客户端再次初始化应抛RepositoryException
	 */
	public void testS3FileTransactionDoubleInit() {
		S3FileTransaction transaction = new S3FileTransaction();

		transaction.setS3Client(software.amazon.awssdk.services.s3.S3Client.builder()
				.region(software.amazon.awssdk.regions.Region.AWS_CN_GLOBAL).build());

		try {
			transaction.initClient("http://localhost", "key", "secret", "us-east-1");
			fail("Should throw exception when initializing an already existing client.");
		} catch (Exception e) {
			assertNotNull("Exception message should not be null. ", e.getMessage());
			System.out.println("Expected exception: " + e.getMessage());
		} finally {
			try {
				transaction.close();
			} catch (Exception e) {
				/* ignore */ }
		}
	}

	/**
	 * 测试S3FileTransaction setBucket/getBucket 覆盖：存储桶属性设置与获取
	 * 
	 * @throws Exception
	 */
	public void testS3FileTransactionBucketProperty() throws Exception {
		try (S3FileTransaction transaction = new S3FileTransaction()) {
			assertNull("Bucket should be null initially. ", transaction.getBucket());

			transaction.setBucket("my-test-bucket");
			assertEquals("Bucket should match. ", "my-test-bucket", transaction.getBucket());

			transaction.setBucket("another-bucket");
			assertEquals("Bucket should be updated. ", "another-bucket", transaction.getBucket());

		}
	}

	/**
	 * 测试S3FileTransaction setS3Client/getS3Client 覆盖：客户端设置与获取
	 */
	public void testS3FileTransactionClientProperty() {
		S3FileTransaction transaction = new S3FileTransaction();

		software.amazon.awssdk.services.s3.S3Client mockClient = software.amazon.awssdk.services.s3.S3Client.builder()
				.region(software.amazon.awssdk.regions.Region.AWS_CN_GLOBAL).build();
		transaction.setS3Client(mockClient);
		assertSame("S3Client should be the same instance. ", mockClient, transaction.getS3Client());

		try {
			transaction.close();
		} catch (Exception e) {
			/* ignore */ }
	}

	// ==================== 4. FileData 构造测试 ====================

	/**
	 * 测试FileData从File构造 覆盖：name/location/originalName
	 * 
	 * @throws Exception
	 */
	public void testFileDataFromFile() throws Exception {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("test_s3", ".txt");
			try (FileData fileData = new FileData(tempFile)) {
				assertNotNull("Name should not be null. ", fileData.getName());
				assertTrue("Name should start with test_s3. ", fileData.getName().startsWith("test_s3"));
				assertEquals("OriginalName should match Name. ", fileData.getName(), fileData.getOriginalName());
				assertNotNull("Location should not be null. ", fileData.getLocation());
			} catch (Exception e) {
				throw e;
			}
		} catch (IOException e) {
			fail("Failed to create temp file: " + e.getMessage());
		} finally {
			if (tempFile != null)
				tempFile.delete();
		}
	}

	/**
	 * 测试FileData从FileItem构造 覆盖：从FileItem获取路径构造FileData
	 * 
	 * @throws Exception
	 */
	public void testFileDataFromFileItem() throws Exception {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("test_s3_item", ".txt");
			FileItem fileItem = new FileItem(tempFile);

			try (FileData fileData = new FileData(fileItem)) {
				assertNotNull("Name should not be null. ", fileData.getName());
				assertTrue("Name should start with test_s3_item. ", fileData.getName().startsWith("test_s3_item"));
			} catch (Exception e) {
				throw e;
			}
		} catch (IOException e) {
			fail("Failed to create temp file: " + e.getMessage());
		} finally {
			if (tempFile != null)
				tempFile.delete();
		}
	}

	/**
	 * 测试FileData手动设置属性 覆盖：setName/setLocation/setOriginalName/toString
	 * 
	 * @throws Exception
	 */
	public void testFileDataManualProperties() throws Exception {
		try (FileData fileData = new FileData()) {
			fileData.setName("custom.txt");
			fileData.setOriginalName("original.txt");
			fileData.setLocation("/path/to/custom.txt");

			assertEquals("Name should match. ", "custom.txt", fileData.getName());
			assertEquals("OriginalName should match. ", "original.txt", fileData.getOriginalName());
			assertEquals("Location should match. ", "/path/to/custom.txt", fileData.getLocation());

			String str = fileData.toString();
			assertNotNull("toString should not be null. ", str);
			assertTrue("toString should contain fileData. ", str.contains("fileData"));
		} catch (Exception e) {
			throw e;
		}
	}

	// ==================== 5. 集成测试（需S3配置） ====================

	/**
	 * 集成测试：保存文件到S3 覆盖：FileData→S3上传→返回FileItem(含URL) 注意：需要有效的S3配置才能通过
	 */
	public void testSaveFile() throws Exception {
		File file = Files.valueOf(MyConfiguration.getWorkFolder(), "..", "..", "app.xml");
		if (!file.exists()) {
			System.out.println("Test file [app.xml] not found, skipping save test.");
			return;
		}
		try (FileRepository fileRepository = new FileRepository()) {
			IOperationResult<FileItem> opRsltFile = fileRepository.save(new FileData(file));
			if (opRsltFile.getError() != null) {
				throw opRsltFile.getError();
			}
			assertNotNull("Operation result should not be null. ", opRsltFile);
			assertEquals("Operation result code should be 0. ", 0, opRsltFile.getResultCode());
			for (FileItem item : opRsltFile.getResultObjects()) {
				assertNotNull("FileItem should not be null. ", item);
				if (item instanceof S3FileItem) {
					System.out.println(Strings.format("saved: %s", ((S3FileItem) item).toUrl()));
				} else {
					System.out.println(Strings.format("saved: %s", item.getPath()));
				}
			}
		}
	}

	/**
	 * 集成测试：搜索S3文件 覆盖：按子文件夹条件搜索、S3FileItem类型判断 注意：需要有效的S3配置才能通过
	 */
	public void testSearchFile() throws Exception {
		Criteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(FileRepository.CONDITION_ALIAS_INCLUDE_SUBFOLDER);
		condition.setValue(emYesNo.YES);
		try (FileRepository fileRepository = new FileRepository()) {
			IOperationResult<FileItem> opRsltFile = fileRepository.fetch(criteria);
			if (opRsltFile.getError() != null) {
				throw opRsltFile.getError();
			}
			assertNotNull("Operation result should not be null. ", opRsltFile);
			assertEquals("Operation result code should be 0. ", 0, opRsltFile.getResultCode());
			for (FileItem item : opRsltFile.getResultObjects()) {
				if (item instanceof S3FileItem) {
					System.out.println(Strings.format("fetched: %s", ((S3FileItem) item).toUrl()));
				} else {
					System.out.println(Strings.format("fetched: %s", item.getPath()));
				}
			}
		}
	}

	/**
	 * 集成测试：删除S3文件 覆盖：按文件名条件删除 注意：需要有效的S3配置才能通过
	 */
	public void testDeleteFile() throws Exception {
		Criteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
		condition.setValue("app.xml");
		try (FileRepository fileRepository = new FileRepository()) {
			IOperationResult<FileItem> opRsltFile = fileRepository.delete(criteria);
			if (opRsltFile.getError() != null) {
				throw opRsltFile.getError();
			}
			assertNotNull("Operation result should not be null. ", opRsltFile);
			assertEquals("Operation result code should be 0. ", 0, opRsltFile.getResultCode());
			for (FileItem item : opRsltFile.getResultObjects()) {
				if (item instanceof S3FileItem) {
					System.out.println(Strings.format("deleted: %s", ((S3FileItem) item).toUrl()));
				} else {
					System.out.println(Strings.format("deleted: %s", item.getPath()));
				}
			}
		}
	}
}
