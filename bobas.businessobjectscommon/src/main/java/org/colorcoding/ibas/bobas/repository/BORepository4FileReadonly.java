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
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.core.BORepositoryBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.expressions.ExpressionFactory;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinks;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

public class BORepository4FileReadonly extends BORepositoryBase implements IBORepository4FileReadonly {

	private String repositoryFolder;

	@Override
	public String getRepositoryFolder() {
		if (this.repositoryFolder == null || this.repositoryFolder.equals("")) {
			String workFolder = Configuration.getWorkFolder() + File.separator + "borepository";
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
	public IOperationResult<?> fetchCopy(IBusinessObjectBase bo) {
		return this.fetch(bo.getCriteria(), bo.getClass());
	}

	/**
	 * 获取对象副本，并填充子项值
	 */
	@Override
	public IOperationResult<?> fetchCopyEx(IBusinessObjectBase bo) {
		return this.fetchEx(bo.getCriteria(), bo.getClass());
	}

	@Override
	public IOperationResult<?> fetch(ICriteria criteria, Class<? extends IBusinessObjectBase> boType) {
		return this.fetchEx(criteria, boType);// 查找全部属性
	}

	@Override
	public IOperationResult<?> fetchEx(ICriteria criteria, Class<? extends IBusinessObjectBase> boType) {
		OperationResult<?> operationResult = new OperationResult<Object>();
		try {
			BOFile[] boFiles = this.myFetchEx(criteria, boType);
			for (int i = 0; i < boFiles.length; i++) {
				BOFile boFile = boFiles[i];
				operationResult.addResultObjects(boFile.getBO());
				operationResult.addInformations(new OperationInformation(BOFile.OF_BO_FILE_PATH, boFile.getFilePath()));
			}
		} catch (Exception e) {
			operationResult.setError(e);
			RuntimeLog.log(e);
		}
		return operationResult;
	}

	BOFile[] myFetchEx(ICriteria criteria, Class<? extends IBusinessObjectBase> boType)
			throws RepositoryException, JAXBException, BOFactoryException {
		if (criteria.getBusinessObjectCode() == null || criteria.getBusinessObjectCode().equals("")) {
			criteria.setBusinessObjectCode(this.getBOFactory().getBOCode(boType));
		}
		String boFolder = this.getRepositoryFolder() + File.separator + criteria.getBusinessObjectCode().toLowerCase();
		File file = new File(boFolder);
		if (!file.isDirectory() || !file.exists()) {
			throw new RepositoryException(
					i18n.prop("msg_bobas_invaild_bo_repository_folder", criteria.getBusinessObjectCode()));
		}
		JAXBContext context = JAXBContext.newInstance(boType);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		JudgmentLinks judgmentLinks = null;
		if (criteria.getConditions().size() > 0) {
			// 要求有条件
			judgmentLinks = ExpressionFactory.create().createJudgmentLinks(criteria.getConditions());
		}
		File[] files = file.listFiles();
		ArrayList<BOFile> boFiles = new ArrayList<BOFile>();
		for (int i = 0; i < files.length; i++) {
			file = files[i];
			if (!file.isFile()) {
				continue;
			}
			if (boFiles.size() >= criteria.getResultCount() && criteria.getResultCount() >= 0) {
				// 数据已够
				break;
			}
			try {
				FileInputStream fileStream = new FileInputStream(file);
				IBusinessObjectBase nBO = (IBusinessObjectBase) unmarshaller.unmarshal(fileStream);
				if (judgmentLinks == null || judgmentLinks.judge(nBO)) {
					boFiles.add(new BOFile(file.getPath().replace(this.getRepositoryFolder(), ""), nBO));
				}
			} catch (Exception e) {
				RuntimeLog.log(e);
			}
		}
		return boFiles.toArray(new BOFile[] {});
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