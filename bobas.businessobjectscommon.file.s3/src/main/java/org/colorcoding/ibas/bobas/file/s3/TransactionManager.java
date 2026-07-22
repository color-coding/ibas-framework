package org.colorcoding.ibas.bobas.file.s3;

import org.colorcoding.ibas.bobas.exception.BasRuntimeException;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.file.FileTransaction;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class TransactionManager extends org.colorcoding.ibas.bobas.file.TransactionManager {

	/**
	 * 创建S3文件事务，未配置存储桶时抛出异常
	 *
	 * @param repository 仓储路径，为空时使用根路径
	 * @return S3文件事务
	 * @throws RuntimeException 未配置存储桶时抛出
	 */
	@Override
	public FileTransaction createTransaction(String repository) {
		String bucket = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER);
		bucket = MyConfiguration.getConfigValue(S3FileTransaction.CONFIG_ITEM_S3_BUCKET, bucket);
		if (Strings.isNullOrEmpty(bucket)) {
			throw new BasRuntimeException(I18N.prop("msg_bobas_s3_not_designated_bucket"));
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
