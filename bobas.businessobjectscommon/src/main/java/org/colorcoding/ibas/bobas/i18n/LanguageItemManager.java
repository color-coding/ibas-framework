package org.colorcoding.ibas.bobas.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 默认语言项目管理员
 *
 * @author Niuren.Zhu
 *
 */
public class LanguageItemManager {

	private String languageCode;

	public String getLanguageCode() {
		if (Strings.isNullOrEmpty(this.languageCode)) {
			String langCode = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LANGUAGE_CODE);
			if (Strings.isNullOrEmpty(langCode)) {
				// 获取当前系统语言编码
				langCode = Locale.getDefault().toLanguageTag();
			}
			// 设置语言编码
			this.setLanguageCode(langCode);
		}
		return this.languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	private Map<String, LanguageItem> languageItems;

	protected Map<String, LanguageItem> getLanguageItems() {
		if (languageItems == null) {
			synchronized (this) {
				if (languageItems == null) {
					languageItems = new ConcurrentHashMap<>(256);
				}
			}
		}
		return languageItems;
	}

	/**
	 * 获取国际化文本
	 *
	 * @param key  文本键名；未找到时返回[key]格式
	 * @param args 格式化参数
	 * @return 国际化文本
	 */
	public String getContent(String key, Object... args) {
		LanguageItem languageItem = this.getLanguageItems().get(key);
		if (languageItem != null) {
			if (args.length == 0) {
				return languageItem.getContent(this.getLanguageCode());
			} else {
				return String.format(languageItem.getContent(this.getLanguageCode()), args);
			}
		}
		return String.format("[%s]", key);
	}

	private String workFolder;

	/**
	 * 获取工作目录
	 *
	 * @return 国际化文件目录路径
	 */
	protected String getWorkFolder() {
		if (Strings.isNullOrEmpty(this.workFolder)) {
			String path = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_I18N_FOLDER);
			if (Strings.isNullOrEmpty(path) || !new File(path).exists()) {
				// 配置路径无效，尝试从 classpath 定位
				path = null;
				try {
					URI uri = MyConfiguration.getResource("i18n");
					if (uri != null && "file".equalsIgnoreCase(uri.getScheme())) {
						// classpath 上的 i18n 目录（仅 file 协议可用）
						File dir = new File(uri);
						if (dir.isDirectory()) {
							path = dir.getPath();
						}
					}
				} catch (URISyntaxException e) {
					path = null;
					Logger.log(MessageLevel.DEBUG, e);
				}
				if (path == null) {
					// 回退到工作目录下 i18n 子目录
					path = Files.valueOf(MyConfiguration.getWorkFolder(), "i18n").getPath();
				}
			}
			this.workFolder = new File(path).getPath();
			Logger.log(MessageLevel.DEBUG, "i18n: use folder [%s].", workFolder);
		}
		return workFolder;
	}

	/**
	 * 从文件名提取语言编码
	 * <p>
	 * 如 locale.bobas_en-US.properties → "en-US"<br>
	 * 如 locale.bobas.properties → ""（默认语言）
	 *
	 * @param fileName 文件名（仅名称，不含路径）
	 * @return 语言编码；默认语言返回空字符串
	 */
	protected String extractLanguageCode(String fileName) {
		if (Strings.isNullOrEmpty(fileName)) {
			return "";
		}
		// 去掉 .properties 后缀
		String name = fileName;
		if (name.endsWith(".properties")) {
			name = name.substring(0, name.length() - ".properties".length());
		}
		// 查找最后一个 _ 分隔的语言编码
		int lastUnderscore = name.lastIndexOf('_');
		if (lastUnderscore < 0) {
			// 无语言编码后缀，为默认语言文件
			return "";
		}
		return name.substring(lastUnderscore + 1);
	}

	/**
	 * 加载 classpath 上的语言资源（支持 jar 和 file 协议）
	 *
	 * @param url classpath 资源 URL
	 */
	public void loadResources(URL url) {
		if (url == null) {
			return;
		}
		if ("jar".equals(url.getProtocol())) {
			// JAR 内资源
			this.loadResourcesFromJar(url);
		} else if ("file".equals(url.getProtocol())) {
			// 文件系统目录资源
			this.loadResourcesFromFile(url);
		}
	}

	/**
	 * 从 JAR 文件中加载语言资源
	 */
	private void loadResourcesFromJar(URL url) {
		try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if (jarEntry.isDirectory()) {
					continue;
				}
				String entryName = jarEntry.getName();
				if (!entryName.startsWith("i18n")) {
					continue;
				}
				if (!entryName.endsWith(".properties")) {
					continue;
				}
				// 从文件名提取语言编码
				String langCode = this.extractLanguageCode(new File(entryName).getName());
				try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
					try (Reader reader = new InputStreamReader(inputStream, "UTF-8")) {
						List<LanguageItem> items = this.loadFileContent(reader, langCode);
						for (LanguageItem item : items) {
							this.getLanguageItems().put(item.getKey(), item);
						}
						if (!items.isEmpty()) {
							Logger.log(MessageLevel.DEBUG, "i18n: read jar entry [%s] with lang [%s].", entryName,
									Strings.isNullOrEmpty(langCode) ? "default" : langCode);
						}
					}
				} catch (UnsupportedEncodingException e) {
					Logger.log(MessageLevel.DEBUG, e);
				} catch (IOException e) {
					// 单条目异常不影响其他条目
					Logger.log(MessageLevel.DEBUG, "i18n: failed to read jar entry [%s].", entryName);
					Logger.log(e);
				}
			}
		} catch (IOException e) {
			Logger.log(e);
		}
	}

	/**
	 * 从文件系统目录加载语言资源
	 */
	private void loadResourcesFromFile(URL url) {
		try {
			File dir = new File(url.toURI());
			if (dir.isDirectory()) {
				this.readResources(dir.getPath());
			}
		} catch (URISyntaxException e) {
			Logger.log(e);
		}
	}

	/**
	 * 加载目录或文件中的语言资源（加载所有 .properties 文件，由文件名决定语言编码）
	 *
	 * @param fileFolder 工作目录或文件路径
	 */
	public void readResources(String fileFolder) {
		if (Strings.isNullOrEmpty(fileFolder)) {
			return;
		}
		File file = new File(fileFolder);
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			String langCode = this.extractLanguageCode(file.getName());
			List<LanguageItem> items = this.loadFileContent(file.getPath(), langCode);
			for (LanguageItem item : items) {
				this.getLanguageItems().put(item.getKey(), item);
			}
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File fileItem : files) {
					if (fileItem.isFile() && fileItem.getName().endsWith(".properties")) {
						this.readResources(fileItem.getPath());
					}
				}
			}
		}
	}

	/**
	 * 加载所有语言资源
	 */
	public void readResources() {
		// 已加载的目录路径，防止重复加载
		Set<String> loadedPaths = new HashSet<>();
		// 加载 classpath 上的语言资源（jar 和 file 协议均处理）
		try {
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources("i18n");
			while (dirs.hasMoreElements()) {
				URL dir = dirs.nextElement();
				this.loadResources(dir);
				// 记录 file 协议的路径，避免与 getWorkFolder 重复
				if ("file".equals(dir.getProtocol())) {
					try {
						loadedPaths.add(new File(dir.toURI()).getCanonicalPath());
					} catch (Exception e) {
						// 忽略
					}
				}
			}
		} catch (IOException e) {
			Logger.log(e);
		}
		// 加载工作目录中的语言资源
		String workFolder = this.getWorkFolder();
		try {
			File workDir = new File(workFolder);
			if (workDir.exists()) {
				String canonicalPath = workDir.getCanonicalPath();
				if (!loadedPaths.contains(canonicalPath)) {
					this.readResources(workFolder);
				}
			}
		} catch (IOException e) {
			Logger.log(e);
		}
	}

	protected List<LanguageItem> loadFileContent(String file, String langCode) {
		try (InputStream stream = new FileInputStream(file)) {
			try (Reader reader = new InputStreamReader(stream, "UTF-8")) {
				List<LanguageItem> items = this.loadFileContent(reader, langCode);
				Logger.log("i18n: read file's data [%s] with lang [%s].", file,
						Strings.isNullOrEmpty(langCode) ? "default" : langCode);
				return items;
			}
		} catch (IOException e) {
			Logger.log(e);
		}
		return new ArrayList<>(0);
	}

	protected List<LanguageItem> loadFileContent(Reader reader, String langCode) throws IOException {
		Properties properties = new Properties();
		properties.load(reader);

		LanguageItem langItem;
		ArrayList<LanguageItem> languageItems = new ArrayList<>(properties.size());

		for (Entry<Object, Object> property : properties.entrySet()) {
			if (property.getKey() == null || property.getValue() == null) {
				continue;
			}
			String key = Strings.valueOf(property.getKey());
			// 查找已存在的 LanguageItem，合并同一 key 的不同语言内容
			langItem = this.getLanguageItems().get(key);
			if (langItem == null) {
				langItem = new LanguageItem();
				langItem.setKey(key);
				languageItems.add(langItem);
			}
			langItem.addContent(langCode, Strings.valueOf(property.getValue()));
		}

		return languageItems;
	}

}
