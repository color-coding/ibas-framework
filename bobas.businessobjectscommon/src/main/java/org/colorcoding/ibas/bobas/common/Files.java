package org.colorcoding.ibas.bobas.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
	 * @return 标准化后的File对象
	 */
	public static File valueOf(String first, String... more) {
		return Paths.get(first, more).normalize().toFile();
	}

	/**
	 * 标准化路径
	 *
	 * @param first 部分路径；空返回空字符串
	 * @param more  更多部分路径
	 * @return 标准化后的路径字符串
	 */
	public static String pathOf(String first, String... more) {
		if (Strings.isNullOrEmpty(first)) {
			return Strings.VALUE_EMPTY;
		}
		Path path = Paths.get(first, more).normalize();
		return path.toString();
	}

	/**
	 * 返回文件夹路径
	 *
	 * @param path 待分析地址；空返回空字符串
	 * @return 文件夹路径
	 */
	public static String folderOf(String path) {
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		File file = valueOf(path);
		if (file.isFile()) {
			return file.getParent();
		} else if (file.isDirectory()) {
			return file.getPath();
		}
		// 反斜杠替换正斜杠
		String tmpPath = pathOf(path).replace(Strings.VALUE_BACKSLASH, Strings.VALUE_SLASH);
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
	 * @param path 待分析地址；空返回空字符串
	 * @return 文件名
	 */
	public static String nameOf(String path) {
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		File file = valueOf(path);
		if (file.isFile()) {
			return file.getName();
		} else if (file.isDirectory()) {
			return file.getName();
		}
		// 反斜杠替换正斜杠
		String tmpPath = pathOf(path).replace(Strings.VALUE_BACKSLASH, Strings.VALUE_SLASH);
		if (tmpPath.lastIndexOf(Strings.VALUE_SLASH) > 0) {
			return path.substring(tmpPath.lastIndexOf(Strings.VALUE_SLASH) + 1);
		}
		return path;
	}

	/**
	 * 返回文件扩展名
	 *
	 * @param path 待分析地址；空返回空字符串
	 * @return 扩展名（不含点号）；无扩展名返回空字符串
	 */
	public static String extensionOf(String path) {
		if (!Strings.isNullOrEmpty(path)) {
			String name = nameOf(path);
			if (Strings.indexOf(name, Strings.VALUE_DOT) > 0) {
				return path.substring(path.lastIndexOf(Strings.VALUE_DOT) + 1);
			}
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 隐藏路径前缀
	 *
	 * @param file       文件；null返回空字符串
	 * @param maskFolder 掩饰路径（隐藏部分）
	 * @return 去除前缀后的路径；文件夹路径以分隔符结尾
	 */
	public static String maskingPath(File file, String maskFolder) {
		if (file == null) {
			return Strings.VALUE_EMPTY;
		}
		String path = maskingPath(file.getPath(), maskFolder);
		if (file.isDirectory()) {
			if (!path.endsWith(File.separator)) {
				path = path + File.separator;
			}
		}
		return path;
	}

	/**
	 * 隐藏路径前缀
	 *
	 * @param path       路径；空返回空字符串
	 * @param maskFolder 掩饰路径（隐藏部分）
	 * @return 去除前缀后的路径
	 */
	public static String maskingPath(String path, String maskFolder) {
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		path = pathOf(path);
		if (!Strings.isNullOrEmpty(maskFolder)) {
			maskFolder = pathOf(maskFolder);
			if (Strings.isWith(path, maskFolder, null)) {
				path = path.substring(maskFolder.length());
				if (path.startsWith(File.separator)) {
					path = path.substring(1);
				}
			}
		}
		return path;
	}

	/**
	 * 列出文件
	 *
	 * @param path   路径
	 * @param prefix 文件名前缀（空值跳过比较）
	 * @param suffix 文件名后缀（空值跳过比较）
	 * @return 匹配的文件列表
	 */
	public static List<File> listFiles(String path, String prefix, String suffix) {
		return listFiles(valueOf(path), prefix, suffix);
	}

	/**
	 * 列出文件
	 *
	 * @param file   文件或文件夹
	 * @param prefix 文件名前缀（空值跳过比较）
	 * @param suffix 文件名后缀（空值跳过比较）
	 * @return 匹配的文件列表
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
			writeTo(inputStream, outputStream);
		}
	}

	/**
	 * 内容写到输出流
	 * 
	 * @param inputStream  文件流
	 * @param outputStream 输出流
	 * @throws IOException
	 */
	public static void writeTo(InputStream inputStream, OutputStream outputStream) throws IOException {
		int bytesRead;
		byte[] buffer = new byte[4096];
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
	}

	/**
	 * 内容写到文件
	 * 
	 * @param inputStream 文件流
	 * @param file        文件
	 * @throws IOException
	 */
	public static void writeTo(InputStream inputStream, File file) throws IOException {
		File folder = file.getParentFile();
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			writeTo(inputStream, outputStream);
		}
	}

	/**
	 * 内容写到文件
	 * 
	 * @param params   属性
	 * @param file     文件
	 * @param comments 注释
	 * @throws IOException
	 */
	public static void writeTo(Properties params, File file, String comments) throws IOException {
		File folder = file.getParentFile();
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		try (OutputStream outputStream = new FileOutputStream(file)) {
			try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, "utf-8")) {
				params.store(writer, Strings.isNullOrEmpty(comments) ? Strings.VALUE_EMPTY : comments);
			}
		}
	}

	/**
	 * 内容写到文件
	 * 
	 * @param params 属性
	 * @param file   文件
	 * @throws IOException
	 */
	public static void writeTo(Properties params, File file) throws IOException {
		writeTo(params, file, null);
	}

	/**
	 * 读取输入流的全部字节
	 *
	 * @param inputStream 输入流
	 * @return 字节数组
	 * @throws IOException 读取失败
	 */
	public static byte[] readAllBytes(InputStream inputStream) throws IOException {
		int bytesRead;
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			byte[] data = new byte[4096];
			while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, bytesRead);
			}
			buffer.flush();
			return buffer.toByteArray();
		}
	}

	/**
	 * 读取文件的全部字节
	 *
	 * @param file 文件
	 * @return 字节数组
	 * @throws IOException 读取失败
	 */
	public static byte[] readAllBytes(File file) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			return readAllBytes(inputStream);
		}
	}
}
