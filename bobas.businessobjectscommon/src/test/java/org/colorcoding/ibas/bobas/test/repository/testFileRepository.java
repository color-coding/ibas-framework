package org.colorcoding.ibas.bobas.test.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.repository.FileRepository;

import junit.framework.TestCase;

public class testFileRepository extends TestCase {

	public void testWriteFile() throws FileNotFoundException {
		String testFile = new File(MyConfiguration.getWorkFolder()).getParent() + File.separator
				+ "bobas.businessobjectscommon-0.1.1.jar";
		FileRepository fileRepository = new FileRepository();
		FileData fileData = new FileData();
		FileInputStream fileStream = new FileInputStream(testFile);
		fileData.setStream(fileStream);
		IOperationResult<FileData> operationResult = fileRepository.write(fileData);
		System.out.println(operationResult.getMessage());
		assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
	}
}
