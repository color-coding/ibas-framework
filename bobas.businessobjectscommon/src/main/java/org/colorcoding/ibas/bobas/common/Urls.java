package org.colorcoding.ibas.bobas.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * URL/URI常用方法（支持http/https/ftp/file等协议）
 */
public class Urls {

	private Urls() {
	}

	// ==================== 常量 ====================

	/**
	 * 协议前缀：file://
	 */
	public static final String SCHEME_FILE = "file";

	/**
	 * 协议前缀：http://
	 */
	public static final String SCHEME_HTTP = "http";

	/**
	 * 协议前缀：https://
	 */
	public static final String SCHEME_HTTPS = "https";

	/**
	 * 协议前缀：jar://
	 */
	public static final String SCHEME_JAR = "jar";

	// ==================== 构造与转换 ====================

	/**
	 * 构造URL对象
	 *
	 * @param url 字符串形式的URL；空返回null
	 * @return URL对象；格式错误返回null
	 */
	public static URL valueOf(String url) {
		if (Strings.isNullOrEmpty(url)) {
			return null;
		}
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * 构造URI对象
	 *
	 * @param uri 字符串形式的URI；空返回null
	 * @return URI对象；格式错误返回null
	 */
	public static URI toURI(String uri) {
		if (Strings.isNullOrEmpty(uri)) {
			return null;
		}
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	/**
	 * URL字符串转URI对象
	 *
	 * @param url URL字符串；空返回null
	 * @return URI对象；格式错误返回null
	 */
	public static URI toURI(URL url) {
		if (url == null) {
			return null;
		}
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			return null;
		}
	}

	/**
	 * File转URL字符串（file://格式）
	 *
	 * @param file 文件；null返回空字符串
	 * @return file://格式的URL字符串
	 */
	public static String toUrl(File file) {
		if (file == null) {
			return Strings.VALUE_EMPTY;
		}
		return file.toURI().toString();
	}

	/**
	 * 路径字符串转URL字符串（自动识别是否需要file://前缀）
	 *
	 * @param path 路径字符串；空返回空字符串
	 * @return URL格式字符串
	 */
	public static String toUrl(String path) {
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		// 已经是URL格式
		if (hasScheme(path)) {
			return path;
		}
		// 当作文件路径处理
		return new File(path).toURI().toString();
	}

	// ==================== 解析 ====================

	/**
	 * 获取协议（scheme），如"http"、"https"、"file"
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 协议名称；无法解析返回空字符串
	 */
	public static String getScheme(String url) {
		if (Strings.isNullOrEmpty(url)) {
			return Strings.VALUE_EMPTY;
		}
		URI uri = toURI(url);
		if (uri != null && uri.getScheme() != null) {
			return uri.getScheme().toLowerCase();
		}
		// 尝试手动提取scheme（如file:///path）
		int colonIndex = url.indexOf(':');
		if (colonIndex > 0) {
			String scheme = url.substring(0, colonIndex);
			// 简单校验：scheme只含字母
			if (scheme.matches("[a-zA-Z]+")) {
				return scheme.toLowerCase();
			}
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 获取主机名
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 主机名；file协议返回空字符串
	 */
	public static String getHost(String url) {
		URI uri = toURI(url);
		if (uri != null && uri.getHost() != null) {
			return uri.getHost();
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 获取端口号
	 *
	 * @param url URL字符串
	 * @return 端口号；未指定或无法解析返回-1
	 */
	public static int getPort(String url) {
		URI uri = toURI(url);
		if (uri != null) {
			return uri.getPort();
		}
		return -1;
	}

	/**
	 * 获取路径部分（不含query和fragment）
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 路径部分
	 */
	public static String getPath(String url) {
		URI uri = toURI(url);
		if (uri != null && uri.getPath() != null) {
			return uri.getPath();
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 获取查询字符串（不含?）
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 查询字符串
	 */
	public static String getQuery(String url) {
		URI uri = toURI(url);
		if (uri != null && uri.getQuery() != null) {
			return uri.getQuery();
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 获取片段标识（不含#）
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 片段标识
	 */
	public static String getFragment(String url) {
		URI uri = toURI(url);
		if (uri != null && uri.getFragment() != null) {
			return uri.getFragment();
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 从URL中提取文件名
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 文件名部分
	 */
	public static String getFileName(String url) {
		String path = getPath(url);
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		// 去除末尾斜杠
		if (path.endsWith(Strings.VALUE_SLASH)) {
			path = path.substring(0, path.length() - 1);
		}
		int lastSlash = path.lastIndexOf('/');
		if (lastSlash >= 0) {
			return path.substring(lastSlash + 1);
		}
		return path;
	}

	/**
	 * 从URL中提取文件扩展名
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 扩展名（不含点号）；无扩展名返回空字符串
	 */
	public static String getFileExtension(String url) {
		String name = getFileName(url);
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex > 0) {
			return name.substring(dotIndex + 1);
		}
		return Strings.VALUE_EMPTY;
	}

	// ==================== 判断 ====================

	/**
	 * 判断URL是否包含协议前缀（scheme://）
	 *
	 * @param url URL字符串；空返回false
	 * @return 包含协议返回true
	 */
	public static boolean hasScheme(String url) {
		if (Strings.isNullOrEmpty(url)) {
			return false;
		}
		int colonIndex = url.indexOf(':');
		if (colonIndex <= 0) {
			return false;
		}
		String scheme = url.substring(0, colonIndex);
		return scheme.matches("[a-zA-Z][a-zA-Z0-9+-.]*");
	}

	/**
	 * 判断是否为file协议
	 *
	 * @param url URL字符串；空返回false
	 * @return file协议返回true
	 */
	public static boolean isFile(String url) {
		return Strings.equalsIgnoreCase(SCHEME_FILE, getScheme(url));
	}

	/**
	 * 判断是否为http协议
	 *
	 * @param url URL字符串；空返回false
	 * @return http协议返回true
	 */
	public static boolean isHttp(String url) {
		return Strings.equalsIgnoreCase(SCHEME_HTTP, getScheme(url));
	}

	/**
	 * 判断是否为https协议
	 *
	 * @param url URL字符串；空返回false
	 * @return https协议返回true
	 */
	public static boolean isHttps(String url) {
		return Strings.equalsIgnoreCase(SCHEME_HTTPS, getScheme(url));
	}

	/**
	 * 判断是否为http或https协议
	 *
	 * @param url URL字符串；空返回false
	 * @return http/https协议返回true
	 */
	public static boolean isHttpOrHttps(String url) {
		return isHttp(url) || isHttps(url);
	}

	/**
	 * 判断是否为jar协议
	 *
	 * @param url URL字符串；空返回false
	 * @return jar协议返回true
	 */
	public static boolean isJar(String url) {
		return Strings.equalsIgnoreCase(SCHEME_JAR, getScheme(url));
	}

	/**
	 * 判断URL字符串格式是否合法
	 *
	 * @param url URL字符串；空返回false
	 * @return 合法返回true
	 */
	public static boolean isValid(String url) {
		return valueOf(url) != null;
	}

	// ==================== file:// 专用 ====================

	/**
	 * 将file://URL转为File对象
	 *
	 * @param url file://格式的URL字符串；空返回null
	 * @return File对象；非file协议返回null
	 */
	public static File toFile(String url) {
		if (Strings.isNullOrEmpty(url)) {
			return null;
		}
		// 已经是file://格式
		if (isFile(url)) {
			URI uri = toURI(url);
			if (uri != null) {
				return new File(uri);
			}
			// URI解析失败，尝试去除file://前缀
			String path = url.substring("file:".length());
			if (path.startsWith("///")) {
				path = path.substring(2); // 保留一个/给绝对路径
			} else if (path.startsWith("//")) {
				path = path.substring(1);
			}
			return new File(path);
		}
		// 没有协议前缀，当作本地路径
		if (!hasScheme(url)) {
			return new File(url);
		}
		// 其他协议，尝试转换
		URI uri = toURI(url);
		if (uri != null) {
			try {
				return new File(uri);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 将File转为file://格式的URL字符串
	 *
	 * @param file 文件；null返回空字符串
	 * @return file://格式的URL
	 */
	public static String fromFile(File file) {
		return toUrl(file);
	}

	/**
	 * 将本地路径转为file://格式的URL字符串
	 *
	 * @param path 本地路径；空返回空字符串
	 * @return file://格式的URL
	 */
	public static String fromPath(String path) {
		if (Strings.isNullOrEmpty(path)) {
			return Strings.VALUE_EMPTY;
		}
		return new File(path).toURI().toString();
	}
	// ==================== 拼接与构建 ====================

	/**
	 * 拼接URL路径（自动处理斜杠）
	 *
	 * @param base  基础URL；空返回空字符串
	 * @param paths 路径片段
	 * @return 拼接后的URL
	 */
	public static String combine(String base, String... paths) {
		if (Strings.isNullOrEmpty(base)) {
			return Strings.VALUE_EMPTY;
		}
		if (paths == null || paths.length == 0) {
			return base;
		}
		StringBuilder builder = new StringBuilder(base);
		for (String path : paths) {
			if (Strings.isNullOrEmpty(path)) {
				continue;
			}
			boolean baseEndsWithSlash = builder.charAt(builder.length() - 1) == '/';
			boolean pathStartsWithSlash = path.charAt(0) == '/';
			if (baseEndsWithSlash && pathStartsWithSlash) {
				builder.append(path.substring(1));
			} else if (!baseEndsWithSlash && !pathStartsWithSlash) {
				builder.append('/').append(path);
			} else {
				builder.append(path);
			}
		}
		return builder.toString();
	}

	/**
	 * 添加查询参数
	 *
	 * @param url   URL字符串
	 * @param name  参数名；空不处理
	 * @param value 参数值；null视为空字符串
	 * @return 添加参数后的URL
	 */
	public static String addParameter(String url, String name, String value) {
		if (Strings.isNullOrEmpty(url) || Strings.isNullOrEmpty(name)) {
			return url;
		}
		String param = name + "=" + (value != null ? value : Strings.VALUE_EMPTY);
		if (Strings.indexOf(url, "?") < 0) {
			return url + "?" + param;
		} else {
			return url + "&" + param;
		}
	}

	/**
	 * 去除URL的片段标识（#及之后内容）
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 去除fragment后的URL
	 */
	public static String removeFragment(String url) {
		if (Strings.isNullOrEmpty(url)) {
			return Strings.VALUE_EMPTY;
		}
		int hashIndex = url.indexOf('#');
		if (hashIndex >= 0) {
			return url.substring(0, hashIndex);
		}
		return url;
	}

	/**
	 * 去除URL的查询字符串（?及之后内容）
	 *
	 * @param url URL字符串；空返回空字符串
	 * @return 去除query后的URL
	 */
	public static String removeQuery(String url) {
		if (Strings.isNullOrEmpty(url)) {
			return Strings.VALUE_EMPTY;
		}
		int queryIndex = url.indexOf('?');
		if (queryIndex >= 0) {
			return url.substring(0, queryIndex);
		}
		return url;
	}

	// ==================== classpath资源 ====================

	/**
	 * 获取classpath资源的URL
	 *
	 * @param name 资源名称；空返回null
	 * @return URL对象；未找到返回null
	 */
	public static URL getResource(String name) {
		if (Strings.isNullOrEmpty(name)) {
			return null;
		}
		return Thread.currentThread().getContextClassLoader().getResource(name);
	}

	/**
	 * 获取classpath资源的URL字符串
	 *
	 * @param name 资源名称；空返回空字符串
	 * @return URL字符串；未找到返回空字符串
	 */
	public static String getResourceUrl(String name) {
		URL url = getResource(name);
		if (url != null) {
			return url.toString();
		}
		return Strings.VALUE_EMPTY;
	}
}
