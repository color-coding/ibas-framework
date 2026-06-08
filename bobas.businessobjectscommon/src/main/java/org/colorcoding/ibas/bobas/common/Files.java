package org.colorcoding.ibas.bobas.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
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

	// ======================== java.nio.file.Files 常用方法封装 ========================

	/**
	 * 判断文件或目录是否存在
	 *
	 * @param path 路径
	 * @return 存在返回true
	 */
	public static boolean exists(String path) {
		return exists(valueOf(path));
	}

	/**
	 * 判断文件或目录是否存在
	 *
	 * @param file 文件
	 * @return 存在返回true
	 */
	public static boolean exists(File file) {
		return java.nio.file.Files.exists(file.toPath(), LinkOption.NOFOLLOW_LINKS);
	}

	/**
	 * 判断是否为目录
	 *
	 * @param path 路径
	 * @return 是目录返回true
	 */
	public static boolean isDirectory(String path) {
		return isDirectory(valueOf(path));
	}

	/**
	 * 判断是否为目录
	 *
	 * @param file 文件
	 * @return 是目录返回true
	 */
	public static boolean isDirectory(File file) {
		return java.nio.file.Files.isDirectory(file.toPath(), LinkOption.NOFOLLOW_LINKS);
	}

	/**
	 * 判断是否为普通文件
	 *
	 * @param path 路径
	 * @return 是普通文件返回true
	 */
	public static boolean isRegularFile(String path) {
		return isRegularFile(valueOf(path));
	}

	/**
	 * 判断是否为普通文件
	 *
	 * @param file 文件
	 * @return 是普通文件返回true
	 */
	public static boolean isRegularFile(File file) {
		return java.nio.file.Files.isRegularFile(file.toPath(), LinkOption.NOFOLLOW_LINKS);
	}

	/**
	 * 判断是否为隐藏文件
	 *
	 * @param path 路径
	 * @return 是隐藏文件返回true
	 */
	public static boolean isHidden(String path) throws IOException {
		return isHidden(valueOf(path));
	}

	/**
	 * 判断是否为隐藏文件
	 *
	 * @param file 文件
	 * @return 是隐藏文件返回true
	 */
	public static boolean isHidden(File file) throws IOException {
		return java.nio.file.Files.isHidden(file.toPath());
	}

	/**
	 * 判断文件是否可读
	 *
	 * @param path 路径
	 * @return 可读返回true
	 */
	public static boolean isReadable(String path) {
		return isReadable(valueOf(path));
	}

	/**
	 * 判断文件是否可读
	 *
	 * @param file 文件
	 * @return 可读返回true
	 */
	public static boolean isReadable(File file) {
		return java.nio.file.Files.isReadable(file.toPath());
	}

	/**
	 * 判断文件是否可写
	 *
	 * @param path 路径
	 * @return 可写返回true
	 */
	public static boolean isWritable(String path) {
		return isWritable(valueOf(path));
	}

	/**
	 * 判断文件是否可写
	 *
	 * @param file 文件
	 * @return 可写返回true
	 */
	public static boolean isWritable(File file) {
		return java.nio.file.Files.isWritable(file.toPath());
	}

	/**
	 * 返回文件大小（字节）
	 *
	 * @param path 路径
	 * @return 文件大小
	 * @throws IOException 读取失败
	 */
	public static long size(String path) throws IOException {
		return size(valueOf(path));
	}

	/**
	 * 返回文件大小（字节）
	 *
	 * @param file 文件
	 * @return 文件大小
	 * @throws IOException 读取失败
	 */
	public static long size(File file) throws IOException {
		return java.nio.file.Files.size(file.toPath());
	}

	/**
	 * 创建文件
	 *
	 * @param path 路径
	 * @return 创建后的文件
	 * @throws IOException 创建失败
	 */
	public static File createFile(String path) throws IOException {
		return createFile(valueOf(path));
	}

	/**
	 * 创建文件
	 *
	 * @param file 文件
	 * @return 创建后的文件
	 * @throws IOException 创建失败
	 */
	public static File createFile(File file) throws IOException {
		File folder = file.getParentFile();
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		Path created = java.nio.file.Files.createFile(file.toPath(), new FileAttribute<?>[0]);
		return created.toFile();
	}

	/**
	 * 创建目录
	 *
	 * @param path 路径
	 * @return 创建后的目录
	 * @throws IOException 创建失败
	 */
	public static File createDirectory(String path) throws IOException {
		return createDirectory(valueOf(path));
	}

	/**
	 * 创建目录
	 *
	 * @param file 文件
	 * @return 创建后的目录
	 * @throws IOException 创建失败
	 */
	public static File createDirectory(File file) throws IOException {
		File folder = file.getParentFile();
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		Path created = java.nio.file.Files.createDirectory(file.toPath(), new FileAttribute<?>[0]);
		return created.toFile();
	}

	/**
	 * 删除文件或目录（目录必须为空）
	 *
	 * @param path 路径
	 * @throws IOException 删除失败
	 */
	public static void delete(String path) throws IOException {
		delete(valueOf(path));
	}

	/**
	 * 删除文件或目录（目录必须为空）
	 *
	 * @param file 文件
	 * @throws IOException 删除失败
	 */
	public static void delete(File file) throws IOException {
		java.nio.file.Files.delete(file.toPath());
	}

	/**
	 * 存在则删除文件或目录（目录必须为空），不存在不报错
	 *
	 * @param path 路径
	 * @return 实际删除了返回true
	 * @throws IOException 删除失败
	 */
	public static boolean deleteIfExists(String path) throws IOException {
		return deleteIfExists(valueOf(path));
	}

	/**
	 * 存在则删除文件或目录（目录必须为空），不存在不报错
	 *
	 * @param file 文件
	 * @return 实际删除了返回true
	 * @throws IOException 删除失败
	 */
	public static boolean deleteIfExists(File file) throws IOException {
		return java.nio.file.Files.deleteIfExists(file.toPath());
	}

	/**
	 * 复制文件
	 *
	 * @param source 源文件
	 * @param target 目标文件
	 * @throws IOException 复制失败
	 */
	public static void copy(String source, String target) throws IOException {
		copy(valueOf(source), valueOf(target));
	}

	/**
	 * 复制文件
	 *
	 * @param source 源文件
	 * @param target 目标文件
	 * @throws IOException 复制失败
	 */
	public static void copy(File source, File target) throws IOException {
		copy(source, target, false);
	}

	/**
	 * 复制文件
	 *
	 * @param source          源文件
	 * @param target          目标文件
	 * @param replaceExisting 是否替换已存在的目标文件
	 * @throws IOException 复制失败
	 */
	public static void copy(String source, String target, boolean replaceExisting) throws IOException {
		copy(valueOf(source), valueOf(target), replaceExisting);
	}

	/**
	 * 复制文件
	 *
	 * @param source          源文件
	 * @param target          目标文件
	 * @param replaceExisting 是否替换已存在的目标文件
	 * @throws IOException 复制失败
	 */
	public static void copy(File source, File target, boolean replaceExisting) throws IOException {
		if (replaceExisting) {
			java.nio.file.Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			java.nio.file.Files.copy(source.toPath(), target.toPath());
		}
	}

	/**
	 * 移动或重命名文件
	 *
	 * @param source 源文件
	 * @param target 目标文件
	 * @throws IOException 移动失败
	 */
	public static void move(String source, String target) throws IOException {
		move(valueOf(source), valueOf(target));
	}

	/**
	 * 移动或重命名文件
	 *
	 * @param source 源文件
	 * @param target 目标文件
	 * @throws IOException 移动失败
	 */
	public static void move(File source, File target) throws IOException {
		move(source, target, false);
	}

	/**
	 * 移动或重命名文件
	 *
	 * @param source          源文件
	 * @param target          目标文件
	 * @param replaceExisting 是否替换已存在的目标文件
	 * @throws IOException 移动失败
	 */
	public static void move(String source, String target, boolean replaceExisting) throws IOException {
		move(valueOf(source), valueOf(target), replaceExisting);
	}

	/**
	 * 移动或重命名文件
	 *
	 * @param source          源文件
	 * @param target          目标文件
	 * @param replaceExisting 是否替换已存在的目标文件
	 * @throws IOException 移动失败
	 */
	public static void move(File source, File target, boolean replaceExisting) throws IOException {
		if (replaceExisting) {
			java.nio.file.Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			java.nio.file.Files.move(source.toPath(), target.toPath());
		}
	}

	/**
	 * 读取文件的所有行
	 *
	 * @param path    路径
	 * @param charset 字符集；null使用UTF-8
	 * @return 行内容列表
	 * @throws IOException 读取失败
	 */
	public static List<String> readAllLines(String path, Charset charset) throws IOException {
		return readAllLines(valueOf(path), null);
	}

	/**
	 * 读取文件的所有行
	 *
	 * @param file    文件
	 * @param charset 字符集；null使用UTF-8
	 * @return 行内容列表
	 * @throws IOException 读取失败
	 */
	public static List<String> readAllLines(File file, Charset charset) throws IOException {
		if (charset == null) {
			charset = Charset.forName("UTF-8");
		}
		return new ArrayList<>(java.nio.file.Files.readAllLines(file.toPath(), charset));
	}

	/**
	 * 读取文件的所有行（UTF-8编码）
	 *
	 * @param path 路径
	 * @return 行内容列表
	 * @throws IOException 读取失败
	 */
	public static List<String> readAllLines(String path) throws IOException {
		return readAllLines(valueOf(path), null);
	}

	/**
	 * 读取文件的所有行（UTF-8编码）
	 *
	 * @param file 文件
	 * @return 行内容列表
	 * @throws IOException 读取失败
	 */
	public static List<String> readAllLines(File file) throws IOException {
		return readAllLines(file, null);
	}

	/**
	 * 将字节数组写入文件
	 *
	 * @param data 字节数组
	 * @param path 路径
	 * @throws IOException 写入失败
	 */
	public static void writeTo(byte[] data, String path) throws IOException {
		writeTo(data, valueOf(path));
	}

	/**
	 * 将字节数组写入文件
	 *
	 * @param data 字节数组
	 * @param file 文件
	 * @throws IOException 写入失败
	 */
	public static void writeTo(byte[] data, File file) throws IOException {
		File folder = file.getParentFile();
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		java.nio.file.Files.write(file.toPath(), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * 将行集合写入文件
	 *
	 * @param lines   行内容集合
	 * @param path    路径
	 * @param charset 字符集；null使用UTF-8
	 * @throws IOException 写入失败
	 */
	public static void writeTo(Collection<String> lines, String path, Charset charset) throws IOException {
		writeTo(lines, valueOf(path), charset);
	}

	/**
	 * 将行集合写入文件
	 *
	 * @param lines   行内容集合
	 * @param file    文件
	 * @param charset 字符集；null使用UTF-8
	 * @throws IOException 写入失败
	 */
	public static void writeTo(Collection<String> lines, File file, Charset charset) throws IOException {
		if (charset == null) {
			charset = Charset.forName("UTF-8");
		}
		File folder = file.getParentFile();
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		java.nio.file.Files.write(file.toPath(), lines, charset, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * 将行集合写入文件（UTF-8编码）
	 *
	 * @param lines 行内容集合
	 * @param path  路径
	 * @throws IOException 写入失败
	 */
	public static void writeTo(Collection<String> lines, String path) throws IOException {
		writeTo(lines, valueOf(path), null);
	}

	/**
	 * 将行集合写入文件（UTF-8编码）
	 *
	 * @param lines 行内容集合
	 * @param file  文件
	 * @throws IOException 写入失败
	 */
	public static void writeTo(Collection<String> lines, File file) throws IOException {
		writeTo(lines, file, null);
	}

	/**
	 * 探测文件的内容类型（MIME类型）
	 *
	 * @param path 路径
	 * @return 内容类型字符串，如"text/plain"；无法判断返回null
	 * @throws IOException 探测失败
	 */
	public static String probeContentType(String path) throws IOException {
		return probeContentType(valueOf(path));
	}

	/**
	 * 探测文件的内容类型（MIME类型）
	 *
	 * @param file 文件
	 * @return 内容类型字符串，如"text/plain"；无法判断返回null
	 * @throws IOException 探测失败
	 */
	public static String probeContentType(File file) throws IOException {
		return java.nio.file.Files.probeContentType(file.toPath());
	}

	/**
	 * 获取文件的输入流
	 *
	 * @param path 路径
	 * @return 输入流
	 * @throws IOException 打开失败
	 */
	public static InputStream newInputStream(String path) throws IOException {
		return newInputStream(valueOf(path));
	}

	/**
	 * 获取文件的输入流
	 *
	 * @param file 文件
	 * @return 输入流
	 * @throws IOException 打开失败
	 */
	public static InputStream newInputStream(File file) throws IOException {
		return java.nio.file.Files.newInputStream(file.toPath());
	}

	/**
	 * 获取文件的输出流
	 *
	 * @param path 路径
	 * @return 输出流
	 * @throws IOException 打开失败
	 */
	public static OutputStream newOutputStream(String path) throws IOException {
		return newOutputStream(valueOf(path));
	}

	/**
	 * 获取文件的输出流
	 *
	 * @param file 文件
	 * @return 输出流
	 * @throws IOException 打开失败
	 */
	public static OutputStream newOutputStream(File file) throws IOException {
		File folder = file.getParentFile();
		if (folder != null && !folder.exists()) {
			folder.mkdirs();
		}
		return java.nio.file.Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	/**
	 * 递归遍历文件树
	 *
	 * @param path 起始路径
	 * @return 所有文件和目录的列表（含起始路径自身）
	 * @throws IOException 遍历失败
	 */
	public static List<File> walk(String path) throws IOException {
		return walk(valueOf(path));
	}

	/**
	 * 递归遍历文件树
	 *
	 * @param file 起始文件或目录
	 * @return 所有文件和目录的列表（含起始路径自身）
	 * @throws IOException 遍历失败
	 */
	public static List<File> walk(File file) throws IOException {
		ArrayList<File> results = new ArrayList<>();
		for (Path p : java.nio.file.Files.walk(file.toPath()).toArray(Path[]::new)) {
			results.add(p.toFile());
		}
		results.trimToSize();
		return results;
	}
}
