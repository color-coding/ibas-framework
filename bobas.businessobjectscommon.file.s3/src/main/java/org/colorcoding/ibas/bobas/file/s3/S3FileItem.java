package org.colorcoding.ibas.bobas.file.s3;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.function.Consumer;

import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class S3FileItem extends FileItem {

	private static final long serialVersionUID = 9043075042028907739L;

	URL url;

	public String toUrl() {
		if (url == null) {
			return this.getPath();
		}
		return url.toString();
	}

	Consumer<OutputStream> loader;

	/**
	 * 文件数据写入输出流
	 * 
	 * @param out 输出流
	 * @throws IOException
	 */
	@Override
	public void writeTo(OutputStream outputStream) throws IOException {
		if (loader == null) {
			throw new RuntimeException(I18N.prop("msg_bobas_s3_no_loader_specified"));
		}
		this.loader.accept(outputStream);
	}

	@Override
	protected void finalize() throws Throwable {
		this.loader = null;
		super.finalize();
	}

}
