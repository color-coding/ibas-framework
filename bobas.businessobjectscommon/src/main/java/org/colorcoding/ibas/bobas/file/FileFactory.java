package org.colorcoding.ibas.bobas.file;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

public class FileFactory {

	private FileFactory() {
	}

	public synchronized static TransactionManager createManager(String fileSign) {
		TransactionManager manager = new Factory().create(fileSign);
		manager.sign = fileSign;
		return manager;
	}

	private static class Factory extends ConfigurableFactory<TransactionManager> {

		public TransactionManager create(String fileSign) {
			TransactionManager manager = this.create(
					Strings.isNullOrEmpty(fileSign) ? MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_WAY
							: Strings.format("%s|%s", MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_WAY, fileSign),
					TransactionManager.class.getSimpleName());
			return manager;
		}

		@Override
		protected TransactionManager createDefault(String typeName) {
			return new TransactionManager() {

				@Override
				public FileTransaction createTransaction(String repository) {
					LocalFileTransaction transaction = new LocalFileTransaction();
					// 提供完整路径
					if (!Strings.isNullOrEmpty(repository)) {
						File file = Files.valueOf(repository);
						if (file.isDirectory() && file.exists() && file.isAbsolute()) {
							transaction.setRepositoryFolder(file.getPath());
							return transaction;
						}
					}
					// 补充路径
					String workFolder = MyConfiguration
							.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER);
					if (Strings.isNullOrEmpty(workFolder)) {
						workFolder = MyConfiguration.getDataFolder();
					}
					File file = Files.valueOf(workFolder, repository == null ? Strings.VALUE_EMPTY : repository);
					if (!file.exists()) {
						file.mkdirs();
					}
					transaction.setRepositoryFolder(file.getPath());

					return transaction;
				}

			};
		}
	}
}
