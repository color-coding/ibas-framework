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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;

/**
 * 默认语言项目管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class LanguageItemManager {

	private String languageCode;

	public String getLanguageCode() {
		if (languageCode == null || languageCode.isEmpty()) {
			String langCode = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LANGUAGE_CODE);
			if (langCode == null || langCode.isEmpty()) {
				// 获取当前系统语言编码
				langCode = Locale.getDefault().toLanguageTag();
			}
			// 设置语言编码
			this.setLanguageCode(langCode);
		}
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	private Map<String, LanguageItem> languageItems;

	/**
	 * 存储多语言资源
	 */
	protected Map<String, LanguageItem> getLanguageItems() {
		if (languageItems == null) {
			languageItems = new HashMap<String, LanguageItem>();
		}
		return languageItems;
	}

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
	 * @return
	 */
	protected String getWorkFolder() {
		if (workFolder == null || workFolder.isEmpty()) {
			String path = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_I18N_PATH);
			if (path == null || path.isEmpty() || !(new File(path)).exists()) {
				// 配置的路径不存在
				try {
					URI uri = MyConfiguration.getResource("i18n");
					if (uri != null) {
						// 存在资源文件
						path = uri.getPath();
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				if (path == null || path.indexOf("!") > 0) {
					// 无效的路径
					path = MyConfiguration.getWorkFolder();
					if (path.endsWith("WEB-INF")) {
						path = String.format("%s%sresources%si18n", (new File(path)).getParentFile().getPath(),
								File.separator, File.separator);
					}
				}
			}
			workFolder = new File(path).getPath();
			Logger.log(LoggingLevel.DEBUG, "i18n: use folder [%s].", workFolder);
		}
		return workFolder;
	}

	/**
	 * 加载jar包语言资源
	 * 
	 * @param file     jar包
	 * @param langCode 语言编码
	 * @throws IOException
	 */
	public void loadResources(URL file, String langCode) throws IOException {
		if (file == null || !file.getProtocol().equals("jar")) {
			return;
		}
		try (JarFile jarFile = ((JarURLConnection) file.openConnection()).getJarFile()) {
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
				if (jarEntry.isDirectory()) {
					continue;
				}
				if (!jarEntry.getName().startsWith("i18n")) {
					continue;
				}
				if (!jarEntry.getName().endsWith(".properties")) {
					continue;
				}
				if (langCode == null || langCode.isEmpty()) {
					if (jarEntry.getName().indexOf("_") > 0) {
						continue;
					}
				} else {
					if (jarEntry.getName().indexOf(langCode) < 0) {
						continue;
					}
				}
				try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
					try (Reader reader = new InputStreamReader(inputStream, "UTF-8")) {
						List<LanguageItem> languageItems = this.loadFileContent(reader);
						for (LanguageItem item : languageItems) {
							this.getLanguageItems().put(item.getKey(), item);
						}
						if (languageItems.size() > 0) {
							Logger.log(LoggingLevel.DEBUG, "i18n: read file's data [%s].", file.toString());
						}
					} catch (UnsupportedEncodingException e) {
						Logger.log(LoggingLevel.DEBUG, e);
					}
				}
			}
		}
	}

	/**
	 * 加载语言，带语言编码
	 * 
	 * @param fileFolder 工作目录
	 * @param langCode   语言编码
	 */
	public void readResources(String fileFolder, String langCode) {
		if (fileFolder == null || fileFolder.isEmpty())
			return;
		File file = new File(fileFolder);
		if (file.exists()) {
			if (file.isFile()) {
				// 添加新的语言内容
				for (LanguageItem item : this.loadFileContent(file.getPath())) {
					this.getLanguageItems().put(item.getKey(), item);
				}
			} else {
				File[] files = file.listFiles();
				if (files != null) {
					for (File fileItem : files) {
						if (!fileItem.getName().endsWith(".properties")) {
							continue;
						}
						if (langCode == null || langCode.isEmpty()) {
							if (fileItem.getName().indexOf("_") > 0) {
								continue;
							}
						} else {
							if (fileItem.getName().indexOf(langCode) < 0) {
								continue;
							}
						}
						this.readResources(fileItem.getPath(), langCode);
					}
				}
			}
		}
	}

	public void readResources() {
		// 加载jar包中语言
		try {
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources("i18n");
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				this.loadResources(url, null);// 默认语言
				this.loadResources(url, this.getLanguageCode());// 使用语言
			}
		} catch (IOException e) {
			Logger.log(e);
		}
		this.readResources(this.getWorkFolder(), null);// 默认语言
		this.readResources(this.getWorkFolder(), this.getLanguageCode());// 使用语言
	}

	protected List<LanguageItem> loadFileContent(String file) {
		try (InputStream stream = new FileInputStream(file)) {
			try (Reader reader = new InputStreamReader(stream, "UTF-8")) {
				List<LanguageItem> languageItems = this.loadFileContent(reader);
				Logger.log("i18n: read file's data [%s].", file);
				return languageItems;
			} catch (IOException e) {
				Logger.log(e);
			}
		} catch (IOException e) {
			Logger.log(e);
		}
		return new ArrayList<>();
	}

	protected List<LanguageItem> loadFileContent(Reader reader) {
		ArrayList<LanguageItem> languageItems = new ArrayList<>();
		try {
			Properties props = new Properties();
			props.load(reader);
			Enumeration<?> enumeration = props.propertyNames();
			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				// 去掉注释
				if (key == null || key.isEmpty())
					continue;
				key = key.trim();// 去掉两端的空格
				String property = props.getProperty(key);
				if (property == null || property.isEmpty())
					continue;
				// 判断是否存在含有key 的 item
				LanguageItem item = new LanguageItem();
				item.setKey(key);
				item.addContent(this.getLanguageCode(), property);
				languageItems.add(item);
			}
		} catch (Exception e) {
			Logger.log(e);
		}
		return languageItems;
	}

}
