package org.colorcoding.ibas.bobas.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class Files {

	private Files() {
	}

	/**
	 * 返回文件
	 * 
	 * @param first 部分路径
	 * @param more  更多部分路径
	 * @return
	 */
	public static File valueOf(String first, String... more) {
		return Paths.get(first, more).normalize().toFile();
	}

	/**
	 * 掩饰路径
	 * 
	 * @param path       路径
	 * @param maskFolder 掩饰路径（隐藏部分）
	 * @return
	 */
	public static String maskingPath(File file, String maskFolder) {
		if (file == null) {
			return Strings.VALUE_EMPTY;
		}
		String path = file.getPath();
		if (!Strings.isNullOrEmpty(maskFolder)) {
			if (Strings.isWith(path, maskFolder, null)) {
				path = path.substring(maskFolder.length());
				if (path.startsWith(File.separator)) {
					path = path.substring(1);
				}
			}
		}
		if (file.isDirectory()) {
			if (!path.endsWith(File.separator)) {
				path = path + File.separator;
			}
		}
		return path;
	}

	public static void writeTo(File file, OutputStream outputStream) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
	}
}
