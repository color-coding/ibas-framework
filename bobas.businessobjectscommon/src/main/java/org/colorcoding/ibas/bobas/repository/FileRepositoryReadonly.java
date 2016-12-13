package org.colorcoding.ibas.bobas.repository;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Conditions;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IConditions;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DataConvert;
import org.colorcoding.ibas.bobas.expressions.FileJudgmentLinks;
import org.colorcoding.ibas.bobas.expressions.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 文件仓库只读
 * 
 * @author Niuren.Zhu
 *
 */
public class FileRepositoryReadonly implements IFileRepositoryReadonly {

    /**
     * 检索条件项目：文件夹。如：documents，条件仅可等于，其他忽略。
     */
    public static final String CRITERIA_CONDITION_ALIAS_FOLDER = "FileFolder";
    /**
     * 检索条件项目：包含子文件夹。如： emYesNo.Yes，条件仅可等于，其他忽略。
     */
    public static final String CRITERIA_CONDITION_ALIAS_INCLUDE_SUBFOLDER = "IncludeSubfolder";

    /**
     * 检索条件项目：文件名称。如：ibas.*.jar，条件仅可等于，其他忽略。
     */
    public static final String CRITERIA_CONDITION_ALIAS_FILE_NAME = FileJudgmentLinks.CRITERIA_CONDITION_ALIAS_FILE_NAME;

    /**
     * 检索条件项目：最后修改时间（文件时间）。如：1479965348，条件可等于，大小等于。
     */
    public static final String CRITERIA_CONDITION_ALIAS_MODIFIED_TIME = FileJudgmentLinks.CRITERIA_CONDITION_ALIAS_MODIFIED_TIME;

    private String repositoryFolder;

    @Override
    public String getRepositoryFolder() {
        if (this.repositoryFolder == null || this.repositoryFolder.isEmpty()) {
            String workFolder = Configuration.getDataFolder() + File.separator + "filerepository";
            workFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER, workFolder);
            File file = new File(workFolder);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }
            this.repositoryFolder = file.getPath();
        }
        return this.repositoryFolder;
    }

    @Override
    public void setRepositoryFolder(String folder) {
        this.repositoryFolder = folder;
    }

    @Override
    public IOperationResult<FileData> fetch(ICriteria criteria) {
        OperationResult<FileData> operationResult = new OperationResult<>();
        try {
            operationResult.addResultObjects(this.searchFiles(criteria));
            return operationResult;
        } catch (Exception e) {
            return new OperationResult<>(e);
        }
    }

    private FileData[] searchFiles(ICriteria criteria) throws Exception {
        if (criteria == null || criteria.getConditions().size() == 0) {
            throw new RepositoryException(i18n.prop("msg_bobas_invaild_criteria"));
        }
        String workFolder = this.getRepositoryFolder();
        boolean include = false;
        IConditions conditions = new Conditions();
        for (ICondition condition : criteria.clone().getConditions()) {

            if (CRITERIA_CONDITION_ALIAS_FOLDER.equals(condition.getAlias())) {
                // 文件夹条件
                if (condition.getAlias() == null || condition.getAlias().isEmpty())
                    continue;
                workFolder = workFolder + File.separator + condition.getCondVal();
            } else if (CRITERIA_CONDITION_ALIAS_INCLUDE_SUBFOLDER.equals(condition.getAlias())) {
                // 包含子文件夹
                if (condition.getAlias() == null || condition.getAlias().isEmpty())
                    continue;
                emYesNo value = emYesNo.No;
                if (condition.getCondVal().length() > 1)
                    value = emYesNo.valueOf(condition.getCondVal());
                else {
                    value = (emYesNo) DataConvert.toEnumValue(emYesNo.class, condition.getCondVal());
                }
                include = value == emYesNo.Yes ? true : false;
            } else {
                conditions.add(condition);
            }
        }
        // 检查文件夹内文件是否符合条件
        File folder = new File(workFolder);
        if (!folder.isDirectory() || !folder.exists()) {
            throw new RepositoryException(
                    i18n.prop("msg_bobas_not_found_folder", workFolder.replace(this.getRepositoryFolder(), ".")));
        }
        // 查询符合条件的文件
        File[] files = this.searchFiles(folder, include, conditions);
        // 输出文件数据
        ArrayList<FileData> nFileDatas = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                FileData fileData = new FileData();
                fileData.setFileName(file.getName());
                fileData.setLocation(file.getPath());
                nFileDatas.add(fileData);
            }
        }
        return nFileDatas.toArray(new FileData[] {});
    }

    /**
     * 查询文件
     * 
     * @param folder
     *            目录
     * @param include
     *            是否包含子目录
     * @param conditions
     *            条件
     * @return 符合条件的文件数组
     */
    private File[] searchFiles(File folder, boolean include, IConditions conditions) {
        ArrayList<File> files = new ArrayList<>();
        FileJudgmentLinks judgmentLinks = null;
        for (File file : folder.listFiles()) {
            if (file.isDirectory() && include) {
                files.addAll(this.searchFiles(file, include, conditions));
            } else if (file.isFile()) {
                if (judgmentLinks == null) {
                    judgmentLinks = new FileJudgmentLinks();
                    judgmentLinks.parsingConditions(conditions);
                }
                try {
                    boolean match = judgmentLinks.judge(file);
                    if (match) {
                        files.add(file);
                    }
                } catch (JudmentOperationException e) {
                    RuntimeLog.log(e);
                }
            }
        }
        return files.toArray(new File[] {});
    }

}
