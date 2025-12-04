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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
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
						path = Files.valueOf((new File(path)).getParentFile().getPath(), "resources", "i18n").getPath();
					}
				}
			}
			workFolder = new File(path).getPath();
			Logger.log(MessageLevel.DEBUG, "i18n: use folder [%s].", workFolder);
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
			JarEntry jarEntry;
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while (jarEntries.hasMoreElements()) {
				jarEntry = (JarEntry) jarEntries.nextElement();
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
							Logger.log(MessageLevel.DEBUG, "i18n: read file's data [%s].", file.toString());
						}
					} catch (UnsupportedEncodingException e) {
						Logger.log(MessageLevel.DEBUG, e);
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
		if (!file.exists()) {
			return;
		}
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

	public void readResources() {
		// 加载jar包中语言
		try {
			URL dir;
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources("i18n");
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				dir = dirs.nextElement();
				this.loadResources(dir, null);// 默认语言
				this.loadResources(dir, this.getLanguageCode());// 使用语言
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
			}
		} catch (IOException e) {
			Logger.log(e);
		}
		return new ArrayList<>(0);
	}

	protected List<LanguageItem> loadFileContent(Reader reader) throws IOException {
		Properties properties = new Properties();
		properties.load(reader);

		LanguageItem langItem;
		ArrayList<LanguageItem> languageItems = new ArrayList<>(properties.size());

		for (Entry<Object, Object> property : properties.entrySet()) {
			if (property.getKey() == null) {
				continue;
			}
			if (property.getValue() == null) {
				continue;
			}
			// 判断是否存在含有key 的 item
			langItem = new LanguageItem();
			langItem.setKey(Strings.valueOf(property.getKey()));
			langItem.addContent(this.getLanguageCode(), Strings.valueOf(property.getValue()));
			languageItems.add(langItem);
		}

		return languageItems;
	}

}
