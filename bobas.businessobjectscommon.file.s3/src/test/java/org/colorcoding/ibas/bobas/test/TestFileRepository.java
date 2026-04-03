package org.colorcoding.ibas.bobas.test;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.file.FileData;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.file.s3.S3FileItem;
import org.colorcoding.ibas.bobas.repository.FileRepository;

import junit.framework.TestCase;

public class TestFileRepository extends TestCase {

	public void testSaveFile() throws Exception {
		File file = Files.valueOf(MyConfiguration.getWorkFolder(), "..", "..", "app.xml");
		try (FileRepository fileRepository = new FileRepository()) {
			IOperationResult<FileItem> opRsltFile = fileRepository.save(new FileData(file));
			if (opRsltFile.getError() != null) {
				throw opRsltFile.getError();
			}
			for (FileItem item : opRsltFile.getResultObjects()) {
				if (item instanceof S3FileItem) {
					System.out.println(Strings.format("saved: %s", ((S3FileItem) item).toUrl()));
				} else {
					System.out.println(Strings.format("saved: %s", item.getPath()));
				}
			}
		}
	}

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
			for (FileItem item : opRsltFile.getResultObjects()) {
				if (item instanceof S3FileItem) {
					System.out.println(Strings.format("fetched: %s", ((S3FileItem) item).toUrl()));
				} else {
					System.out.println(Strings.format("fetched: %s", item.getPath()));
				}
			}
		}
	}

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
