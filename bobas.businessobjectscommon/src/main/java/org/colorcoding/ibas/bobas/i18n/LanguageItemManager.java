package org.colorcoding.ibas.bobas.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import org.colorcoding.ibas.bobas.MyConfiguration;
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
				langCode = Locale.getDefault().toString();
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
			workFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_I18N_PATH);
			if (workFolder == null || workFolder.equals("") || !(new File(workFolder)).exists()) {
				// 配置的路径不存在
				workFolder = MyConfiguration.getResource("i18n").getPath();
				if (workFolder == null || workFolder.indexOf("!") > 0) {
					// 无效的路径
					workFolder = MyConfiguration.getWorkFolder();
					if (workFolder.endsWith("WEB-INF")) {
						workFolder = String.format("%s%sresources%si18n", (new File(workFolder)).getParent(),
								File.separator, File.separator);
					}
				}
				RuntimeLog.log(RuntimeLog.MSG_I18N_RESOURCES_FOLDER, workFolder);
			}
		}
		return workFolder;
	}

	@Override
	public void readResources() {
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
		ArrayList<ILanguageItem> languageItems = new ArrayList<ILanguageItem>();
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
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
			RuntimeLog.log(RuntimeLog.MSG_I18N_READ_FILE_DATA, file);
		} catch (Exception e) {
			RuntimeLog.log(e);
		}
		return languageItems.toArray(new ILanguageItem[] {});
	}

}
