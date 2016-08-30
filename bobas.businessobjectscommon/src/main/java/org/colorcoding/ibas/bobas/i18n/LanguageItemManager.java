package org.colorcoding.ibas.bobas.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 默认语言项目管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class LanguageItemManager implements ILanguageItemManager {

	private String languageCode;

	public String getLanguageCode() {
		if (languageCode == null || languageCode.equals("")) {
			String langCode = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LANGUAGE_CODE);
			if (langCode == null || langCode.equals("")) {
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
		// 语言编码改变，读取对应的内容
		this.readResources(this.getWorkFolder(), this.languageCode);
	}

	private HashMap<String, ILanguageItem> languageItems;

	/**
	 * 存储多语言资源
	 */
	protected HashMap<String, ILanguageItem> getLanguageItems() {
		if (languageItems == null) {
			languageItems = new HashMap<String, ILanguageItem>();
		}
		return languageItems;
	}

	@Override
	public String getContent(String key, Object... args) {
		if (this.getLanguageItems().containsKey(key)) {
			return String.format(this.getLanguageItems().get(key).getContent(this.getLanguageCode()), args);
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
		if (workFolder == null || workFolder.equals("")) {
			String path = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_I18N_PATH);
			if (path == null || path.equals("") || !(new File(path)).exists()) {
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
			RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_I18N_RESOURCES_FOLDER, workFolder);
		}
		return workFolder;
	}

	public void loadResources(String name) {
		if (name == null) {
			return;
		}
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		if (stream != null) {
			ILanguageItem[] languageItems;
			try {
				languageItems = this.loadFileContent(new InputStreamReader(stream, "UTF-8"));
				for (ILanguageItem item : languageItems) {
					this.getLanguageItems().put(item.getKey(), item);
				}
				if (languageItems.length > 0) {
					RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_I18N_READ_FILE_DATA, "!" + name);
				}
			} catch (UnsupportedEncodingException e) {
				RuntimeLog.log(e);
			}
		}
	}

	@Override
	public void readResources() {
		this.loadResources("i18n/locale.bobas.properties");
		this.loadResources(String.format("i18n/locale.bobas_%s.properties", this.getLanguageCode()));
		this.readResources(this.getWorkFolder());
	}

	/**
	 * 加载默认语言，不带语言编码的
	 * 
	 * @param fileFolder
	 *            工作目录
	 */
	public void readResources(String fileFolder) {
		if (fileFolder == null || fileFolder.equals(""))
			return;
		File file = new File(fileFolder);
		if (file.exists()) {
			if (file.isFile()) {
				// 文件，加载资源
				ILanguageItem[] languageItems = this.loadFileContent(file.getPath());
				for (ILanguageItem item : languageItems) {
					this.getLanguageItems().put(item.getKey(), item);
				}
			} else {
				// 文件夹，遍历文件
				for (File fileItem : file.listFiles()) {
					if (!fileItem.getName().endsWith(".properties")) {
						// 非语言
						continue;
					}
					if (fileItem.getName().indexOf("_") > 0) {
						// 带分隔符的，表示不是默认资源
						continue;
					}
					this.readResources(fileItem.getPath());
				}
			}
		}
	}

	/**
	 * 加载语言，带语言编码
	 * 
	 * @param fileFolder
	 *            工作目录
	 * @param langCode
	 *            语言编码
	 */
	public void readResources(String fileFolder, String langCode) {
		if (fileFolder == null || fileFolder.equals(""))
			return;
		File file = new File(fileFolder);
		if (file.exists()) {
			if (file.isFile()) {
				ILanguageItem[] languageItems = this.loadFileContent(file.getPath());
				// 添加新的语言内容
				for (ILanguageItem item : languageItems) {
					if (this.getLanguageItems().containsKey(item.getKey())) {
						ILanguageItem languageItem = this.getLanguageItems().get(item.getKey());
						languageItem.addContent(langCode, item.getContent(langCode));
					}
				}
			} else {
				for (File fileItem : file.listFiles()) {
					if (!fileItem.getName().endsWith(".properties")) {
						continue;
					}
					if (!fileItem.getName().endsWith(String.format("_%s.properties", langCode))) {
						continue;
					}
					this.readResources(fileItem.getPath());
				}
			}
		}
	}

	protected ILanguageItem[] loadFileContent(String file) {
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			ILanguageItem[] languageItems = this.loadFileContent(reader);
			RuntimeLog.log(RuntimeLog.MSG_I18N_READ_FILE_DATA, file);
			return languageItems;
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			RuntimeLog.log(e);
		}
		return new ILanguageItem[] {};
	}

	protected ILanguageItem[] loadFileContent(Reader reader) {
		ArrayList<ILanguageItem> languageItems = new ArrayList<ILanguageItem>();
		try {
			if (reader != null) {
				Properties props = new Properties();
				props.load(reader);
				Enumeration<?> enumeration = props.propertyNames();
				while (enumeration.hasMoreElements()) {
					String key = (String) enumeration.nextElement();
					// 去掉注释
					if (key == null || key.equals(""))
						continue;
					key = key.trim();// 去掉两端的空格
					String property = props.getProperty(key);
					if (property == null || property.equals(""))
						continue;
					// 判断是否存在含有key 的 item
					ILanguageItem item = new LanguageItem();
					item.setKey(key);
					item.addContent(this.getLanguageCode(), property);
					languageItems.add(item);
				}
			}
		} catch (Exception e) {
			RuntimeLog.log(e);
		}
		return languageItems.toArray(new ILanguageItem[] {});
	}

}
