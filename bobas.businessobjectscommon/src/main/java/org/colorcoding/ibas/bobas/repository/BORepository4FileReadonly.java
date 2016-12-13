package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationInformation;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.core.BORepositoryBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.expressions.ExpressionFactory;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinks;
import org.colorcoding.ibas.bobas.expressions.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

public class BORepository4FileReadonly extends BORepositoryBase implements IBORepository4FileReadonly {

    private String repositoryFolder;

    @Override
    public String getRepositoryFolder() {
        if (this.repositoryFolder == null || this.repositoryFolder.isEmpty()) {
            String workFolder = Configuration.getDataFolder() + File.separator + "borepository";
            workFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_REPOSITORY_4_FILE_FOLDER,
                    workFolder);
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
    public void dispose() throws RepositoryException {

    }

    @Override
    public DateTime getServerTime() {
        return DateTime.getNow();
    }

    /**
     * 获取对象副本，不填充子项值
     */
    @Override
    public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopy(T bo) {
        @SuppressWarnings("unchecked")
        Class<T> boType = (Class<T>) bo.getClass();
        return this.fetch(bo.getCriteria(), boType);
    }

    /**
     * 获取对象副本，并填充子项值
     */
    @Override
    public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopyEx(T bo) {
        @SuppressWarnings("unchecked")
        Class<T> boType = (Class<T>) bo.getClass();
        return this.fetchEx(bo.getCriteria(), boType);
    }

    @Override
    public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType) {
        return this.fetchEx(criteria, boType);// 查找全部属性
    }

    @Override
    public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ICriteria criteria, Class<T> boType) {
        OperationResult<T> operationResult = new OperationResult<>();
        try {
            BOFile[] boFiles = this.myFetchEx(criteria, boType);
            for (int i = 0; i < boFiles.length; i++) {
                BOFile boFile = boFiles[i];
                operationResult.addResultObjects(boFile.getBO());
                operationResult.addInformations(new OperationInformation(BOFile.OF_BO_FILE_PATH, boFile.getFilePath()));
            }
        } catch (Exception e) {
            operationResult.setError(e);
        }
        return operationResult;
    }

    BOFile[] myFetchEx(ICriteria criteria, Class<? extends IBusinessObjectBase> boType)
            throws RepositoryException, JAXBException, BOFactoryException {
        if (criteria.getBusinessObjectCode() == null || criteria.getBusinessObjectCode().isEmpty()) {
            criteria.setBusinessObjectCode(this.getBOFactory().getBOCode(boType));
        }
        String boFolder = this.getRepositoryFolder() + File.separator + criteria.getBusinessObjectCode().toLowerCase();
        File file = new File(boFolder);
        if (!file.exists()) {
            return new BOFile[] {};
        }
        if (!file.isDirectory()) {
            throw new RepositoryException(
                    i18n.prop("msg_bobas_invaild_bo_repository_folder", criteria.getBusinessObjectCode()));
        }
        ArrayList<Class<?>> types = this.getTypes(file);
        types.add(boType);
        JAXBContext context = JAXBContext.newInstance(types.toArray(new Class<?>[] {}));
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JudgmentLinks judgmentLinks = null;
        if (criteria.getConditions().size() > 0) {
            // 要求有条件
            judgmentLinks = ExpressionFactory.create().createBOJudgmentLinks(criteria.getConditions());
        }
        File[] files = file.listFiles();
        ArrayList<BOFile> boFiles = new ArrayList<BOFile>();
        for (int i = 0; i < files.length; i++) {
            file = files[i];
            if (!file.isFile()) {
                continue;
            }
            if (!file.getName().endsWith(".bo")) {
                continue;
            }
            if (boFiles.size() >= criteria.getResultCount() && criteria.getResultCount() >= 0) {
                // 数据已够
                break;
            }
            try {
                FileInputStream fileStream = new FileInputStream(file);
                IBusinessObjectBase nBO = (IBusinessObjectBase) unmarshaller.unmarshal(fileStream);
                if (nBO.getClass().equals(boType)) {
                    if (judgmentLinks == null || judgmentLinks.judge(nBO)) {
                        boFiles.add(new BOFile(file.getPath().replace(this.getRepositoryFolder(), ""), nBO));
                    }
                } else {
                    // 对象类型与查找类型不符，则尝试比较属性
                    // 匹配的属性实例返回
                    for (IBusinessObjectBase item : this.match(nBO, judgmentLinks, boType)) {
                        boFiles.add(new BOFile(file.getPath().replace(this.getRepositoryFolder(), ""), item));
                    }
                }
            } catch (Exception e) {
                RuntimeLog.log(e);
            }
        }
        return boFiles.toArray(new BOFile[] {});
    }

    private IBusinessObjectBase[] match(IBusinessObjectBase bo, JudgmentLinks judgmentLinks, Class<?> type) {
        ArrayList<IBusinessObjectBase> bos = new ArrayList<>();
        if (bo instanceof IManageFields) {
            IManageFields boFields = (IManageFields) bo;
            for (IFieldData field : boFields.getFields()) {
                if (field.getValue() == null) {
                    continue;
                }
                if (type.isInstance(field.getValue())) {
                    try {
                        if (judgmentLinks == null || judgmentLinks.judge(bo)) {
                            bos.add(bo);
                        }
                    } catch (JudmentOperationException e) {
                        RuntimeLog.log(e);
                    }
                } else if (IBusinessObjectBase.class.isInstance(field.getValue())) {
                    bos.addAll((match(bo, judgmentLinks, type)));
                } else if (IBusinessObjectListBase.class.isInstance(field.getValue())) {
                    for (IBusinessObjectBase item : ((IBusinessObjectListBase<?>) field.getValue())) {
                        if (type.isInstance(item)) {
                            try {
                                if (judgmentLinks == null || judgmentLinks.judge(item)) {
                                    bos.add(item);
                                }
                            } catch (JudmentOperationException e) {
                                RuntimeLog.log(e);
                            }
                        } else {
                            bos.addAll((match(item, judgmentLinks, type)));
                        }
                    }
                }
            }
        }
        return bos.toArray(new IBusinessObjectBase[] {});
    }

    /**
     * 获取业务对象目录指定的对象类型
     * 
     * @param boFolder
     * @return
     */
    protected ArrayList<Class<?>> getTypes(File boFolder) {
        ArrayList<Class<?>> types = new ArrayList<>();
        for (File item : boFolder.listFiles()) {
            if (item.isFile()) {
                if (item.getName().endsWith(".type")) {
                    try {
                        types.add(BOFactory.create()
                                .getClass(item.getName().substring(0, item.getName().lastIndexOf("."))));
                    } catch (BOFactoryException e) {
                        RuntimeLog.log(e);
                    }
                }
            }
        }
        return types;
    }

    protected class BOFile {
        public final static String OF_BO_FILE_PATH = "BO_FILE_PATH";

        public BOFile(String path, IBusinessObjectBase bo) {
            this.setFilePath(path);
            this.setBO(bo);
        }

        private String filePath;

        public final String getFilePath() {
            return filePath;
        }

        public final void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        private IBusinessObjectBase bo;

        public final IBusinessObjectBase getBO() {
            return bo;
        }

        public final void setBO(IBusinessObjectBase bo) {
            this.bo = bo;
        }
    }
}