package org.colorcoding.ibas.bobas.file.s3;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.file.FileTransaction;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class TransactionManager extends org.colorcoding.ibas.bobas.file.TransactionManager {

	@Override
	public FileTransaction createTransaction(String repository) {
		String bucket = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER);
		bucket = MyConfiguration.getConfigValue(S3FileTransaction.CONFIG_ITEM_S3_BUCKET, bucket);
		if (Strings.isNullOrEmpty(bucket)) {
			throw new RuntimeException(I18N.prop("msg_bobas_s3_not_designated_bucket"));
		}
		if (!Strings.isNullOrEmpty(repository)) {
			repository = Files.pathOf(repository).replace(File.separator, S3FileTransaction.PATH_SEPARATOR);
		}
		if (Strings.isNullOrEmpty(repository)) {
			repository = Strings.VALUE_EMPTY;
		}

		S3FileTransaction fileTransaction = new S3FileTransaction();
		fileTransaction.setGroupingFiles(false);
		fileTransaction.setBucket(bucket);
		fileTransaction.setRepositoryFolder(repository);
		return fileTransaction;
	}

}
