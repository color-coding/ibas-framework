package org.colorcoding.ibas.bobas.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

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
	 * 返回文件夹路径
	 * 
	 * @param path 待分析地址
	 * @return
	 */
	public static String pathOf(String path) {
		File file = valueOf(path);
		if (file.isFile()) {
			return file.getParent();
		} else if (file.isDirectory()) {
			return file.getPath();
		}
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		// 反斜杠替换正斜杠
		String tmpPath = Strings.replace(path, Strings.VALUE_BACKSLASH, Strings.VALUE_SLASH);
		if (tmpPath.lastIndexOf(Strings.VALUE_DOT) > 0 && tmpPath.lastIndexOf(Strings.VALUE_SLASH) > 0) {
			return path.substring(0, tmpPath.lastIndexOf(Strings.VALUE_SLASH));
		} else if (tmpPath.lastIndexOf(Strings.VALUE_SLASH) > 0) {
			return path;
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 返回文件名
	 * 
	 * @param path 待分析地址
	 * @return
	 */
	public static String nameOf(String path) {
		File file = valueOf(path);
		if (file.isFile()) {
			return file.getName();
		} else if (file.isDirectory()) {
			return file.getName();
		}
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		// 反斜杠替换正斜杠
		path = Strings.replace(path, Strings.VALUE_BACKSLASH, Strings.VALUE_SLASH);
		if (path.lastIndexOf(Strings.VALUE_SLASH) > 0) {
			return path.substring(path.lastIndexOf(Strings.VALUE_SLASH) + 1);
		}
		return path;
	}

	/**
	 * 返回文件扩展名
	 * 
	 * @param path 待分析地址
	 * @return
	 */
	public static String extensionOf(String path) {
		String name = nameOf(path);
		if (Strings.indexOf(name, Strings.VALUE_DOT) > 0) {
			return path.substring(path.lastIndexOf(Strings.VALUE_DOT) + 1);
		}
		return Strings.VALUE_EMPTY;
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

	/**
	 * 列出文件
	 * 
	 * @param path   路径
	 * @param prefix 文件名前缀（空值，跳过比较）
	 * @param suffix 文件名后缀（空值，跳过比较）
	 * @return
	 */
	public static List<File> listFiles(String path, String prefix, String suffix) {
		return listFiles(valueOf(path), prefix, suffix);
	}

	/**
	 * 列出文件
	 * 
	 * @param file   文件或文件夹
	 * @param prefix 文件名前缀（空值，跳过比较）
	 * @param suffix 文件名后缀（空值，跳过比较）
	 * @return
	 */
	public static List<File> listFiles(File file, String prefix, String suffix) {
		if (file.isFile()) {
			if (!Strings.isNullOrEmpty(prefix)) {
				if (Strings.startsWith(file.getName(), prefix, true)) {
					return ArrayList.create(file);
				}
			}
			if (!Strings.isNullOrEmpty(suffix)) {
				if (Strings.endsWith(file.getName(), suffix, true)) {
					return ArrayList.create(file);
				}
			}
			if (Strings.isWith(file.getName(), prefix, suffix)) {
				return ArrayList.create(file);
			}
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				ArrayList<File> results = new ArrayList<>(files.length);
				for (File item : files) {
					if (!Strings.isNullOrEmpty(prefix)) {
						if (Strings.startsWith(item.getName(), prefix, true)) {
							results.add(item);
						}
					}
					if (!Strings.isNullOrEmpty(suffix)) {
						if (Strings.endsWith(item.getName(), suffix, true)) {
							results.add(item);
						}
					}
					if (Strings.isWith(item.getName(), prefix, suffix)) {
						results.add(item);
					}
				}
				results.trimToSize();
				return results;
			}
		}
		return ArrayList.create();
	}

	/**
	 * 文件内容写到输出流
	 * 
	 * @param file         文件
	 * @param outputStream 输出流
	 * @throws IOException
	 */
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
