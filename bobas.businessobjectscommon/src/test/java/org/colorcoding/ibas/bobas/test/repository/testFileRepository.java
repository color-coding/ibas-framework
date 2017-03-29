package org.colorcoding.ibas.bobas.test.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.repository.FileRepository;

import junit.framework.TestCase;

public class testFileRepository extends TestCase {

    public void testSaveFile() throws FileNotFoundException {
        String testFile = new File(MyConfiguration.getWorkFolder()).getParent() + File.separator
                + "bobas.businessobjectscommon-0.1.1.jar";
        FileRepository fileRepository = new FileRepository();
        FileData fileData = new FileData();
        fileData.setFileName("~tmp");
        FileInputStream fileStream = new FileInputStream(testFile);
        fileData.setStream(fileStream);
        IOperationResult<FileData> operationResult = fileRepository.save(fileData);
        System.out.println(operationResult.getMessage());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
    }

    public void testFetchFile() {
        ICriteria criteria = new Criteria();
        // 文件
        ICondition condition = criteria.getConditions().create();
        condition.setAlias(FileRepository.CRITERIA_CONDITION_ALIAS_FOLDER);
        condition.setValue("classes");
        // 包含子文件夹
        condition = criteria.getConditions().create();
        condition.setAlias(FileRepository.CRITERIA_CONDITION_ALIAS_INCLUDE_SUBFOLDER);
        condition.setValue(emYesNo.YES);
        // 扩展名为.java
        condition = criteria.getConditions().create();
        condition.setAlias(FileRepository.CRITERIA_CONDITION_ALIAS_FILE_NAME);
        condition.setOperation(ConditionOperation.END);
        condition.setValue(".class");
        // 文件名包括BO
        condition = criteria.getConditions().create();
        condition.setAlias(FileRepository.CRITERIA_CONDITION_ALIAS_FILE_NAME);
        condition.setOperation(ConditionOperation.CONTAIN);
        condition.setValue("BO");
        // 修改时间大于2016-11-11
        condition = criteria.getConditions().create();
        condition.setAlias(FileRepository.CRITERIA_CONDITION_ALIAS_MODIFIED_TIME);
        condition.setOperation(ConditionOperation.GRATER_THAN);
        condition.setValue(DateTime.valueOf("2016-11-11").getTime());

        FileRepository fileRepository = new FileRepository();
        fileRepository.setRepositoryFolder(new File(MyConfiguration.getWorkFolder()).getParent());
        IOperationResult<FileData> operationResult = fileRepository.fetch(criteria);
        System.out.println(operationResult.getMessage());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        for (FileData fileData : operationResult.getResultObjects()) {
            System.out.println(fileData.toString());
        }
    }
}
