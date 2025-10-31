package org.colorcoding.ibas.bobas.test;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.FileItem;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.repository.FileRepository;

import junit.framework.TestCase;

public class TestFileRepository extends TestCase {

	public void testFetchFile() {
		ICriteria criteria = new Criteria();
		// 文件
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(FileRepository.CONDITION_ALIAS_FOLDER);
		condition.setValue("classes");
		// 包含子文件夹
		condition = criteria.getConditions().create();
		condition.setAlias(FileRepository.CONDITION_ALIAS_INCLUDE_SUBFOLDER);
		condition.setValue(emYesNo.YES);
		// 扩展名为.java
		condition = criteria.getConditions().create();
		condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
		condition.setOperation(ConditionOperation.END);
		condition.setValue(".class");
		// 文件名包括BO
		condition = criteria.getConditions().create();
		condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("BO");
		// 修改时间大于2016-11-11
		condition = criteria.getConditions().create();
		condition.setAlias(FileRepository.CONDITION_ALIAS_MODIFIED_TIME);
		condition.setOperation(ConditionOperation.GRATER_THAN);
		condition.setValue(DateTimes.valueOf("2016-11-11").getTime());

		try (FileRepository fileRepository = new FileRepository()) {
			fileRepository.setRepositoryFolder(new File(MyConfiguration.getWorkFolder()).getParent());
			IOperationResult<FileItem> operationResult = fileRepository.fetch(criteria);
			System.out.println(operationResult.toString());
			assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
			for (FileItem fileItem : operationResult.getResultObjects()) {
				System.out.println(fileItem.toString());
			}
			criteria = new Criteria();
			condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
			condition.setValue("BOFactory.class");
			operationResult = fileRepository.fetch(criteria);
			System.out.println(operationResult.toString());
			assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
			for (FileItem fileItem : operationResult.getResultObjects()) {
				System.out.println(fileItem.toString());
			}
		}
	}

	public void testSearchFiles() {
		try (FileRepository fileRepository = new FileRepository()) {
			fileRepository.setRepositoryFolder(System.getProperty("user.dir"));

			Criteria criteria = new Criteria();
			criteria.setResultCount(30);
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CONDITION_ALIAS_INCLUDE_SUBFOLDER);
			condition.setValue(emYesNo.YES);
			condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
			condition.setValue("locale.bobas.properties");
			ISort sort = criteria.getSorts().create();
			sort.setAlias(FileRepository.CONDITION_ALIAS_MODIFIED_TIME);
			sort.setSortType(SortType.DESCENDING);
			File file = null;
			System.out.println("**************************");
			for (FileItem item : fileRepository.fetch(criteria).getResultObjects()) {
				file = new File(item.getPath());
				System.out.println(String.format("%s ,%s", file.getParentFile().getPath(),
						DateTimes.valueOf(file.lastModified()).toString(DateTime.FORMAT_DATETIME)));
			}
			condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CONDITION_ALIAS_MODIFIED_TIME);
			condition.setOperation(ConditionOperation.LESS_THAN);
			condition.setValue(file.lastModified());
			System.out.println("**************************");
			for (FileItem item : fileRepository.fetch(criteria).getResultObjects()) {
				file = new File(item.getPath());
				System.out.println(String.format("%s ,%s", file.getParentFile().getPath(),
						DateTimes.valueOf(file.lastModified()).toString(DateTime.FORMAT_DATETIME)));
			}

			criteria = new Criteria();
			criteria.setResultCount(30);
			condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
			condition.setValue("target/classes/i18n/locale.bobas.properties");
			condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CONDITION_ALIAS_FILE_NAME);
			condition.setValue("target/classes/i18n/locale.bobas_en-US.properties");
			condition.setRelationship(ConditionRelationship.OR);
			System.out.println("**************************");
			for (FileItem item : fileRepository.fetch(criteria).getResultObjects()) {
				file = new File(item.getPath());
				System.out.println(String.format("%s ,%s", file.getPath(),
						DateTimes.valueOf(file.lastModified()).toString(DateTime.FORMAT_DATETIME)));
			}

		}
	}
}
