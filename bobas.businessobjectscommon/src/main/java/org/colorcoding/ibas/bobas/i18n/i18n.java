package org.colorcoding.ibas.bobas.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.colorcoding.ibas.bobas.MyConfiguration;

public class i18n {

	private String languageCode;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * 存储已经加载的文件名
	 */
	private List<String> fileNames = new ArrayList<String>();
	/**
	 * 单例模式
	 */
	private volatile static i18n instance;

	public static i18n getInstance() {
		if (instance == null) {
			synchronized (i18n.class) {
				if (instance == null) {
					instance = new i18n();
				}
			}
		}
		return instance;
	}

	/**
	 * 存储多语言资源
	 */
	private HashMap<String, ILanguageItem> languageItems;

	private HashMap<String, ILanguageItem> getLanguageItems() {
		if (languageItems == null) {
			languageItems = new HashMap<String, ILanguageItem>();
		}
		return languageItems;
	}

	/**
	 * 获取默认资源文件路径
	 * 
	 * @return 返回资源文件路径
	 */
	protected String getFilePath() {
		// 先取配置文件路径地址，否则取项目classPath路径
		String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		String workFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_WORK_FOLDER, classPath);
		String i18nPath = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_I18N_PATH, "i18n/");
		String result = String.format("%s%s", workFolder, i18nPath);
		return result;
	}

	/**
	 * 获取key所对应的值
	 * 
	 * @param key
	 *            需要翻译的文本
	 * @param args
	 *            有效值
	 * @return 返回key所对应的值
	 */
	public static String prop(String key, Object... args) {
		return getInstance().getProp(key, args);
	}

	/**
	 * 获取key所对应的值
	 * 
	 * @param key
	 *            需要翻译的文本
	 * @param args
	 *            有效值
	 * @return 返回key所对应的值
	 */
	private String getProp(String key, Object... args) {
		String result = "";
		if (key == null || key.equals(""))
			return result;
		key = key.trim();
		result = String.format("[%s]", key);
		// 加载默认资源文件
		loadDefaultResource(key);
		if (getLanguageItems().containsKey(key)) {
			ILanguageItem item = languageItems.get(key);
			if (this.languageCode == null || this.languageCode.equals("")) {
				result = item.getContent();// 获取默认的文本
			} else {
				result = item.getContent(getLanguageCode());// 获取默认的文本
			}

		}
		if (args.length > 0) {
			result = String.format(result, args);
		}
		return result;

	}

	/**
	 * 根据key加载默认的资源文件
	 * 
	 * @param key
	 *            要翻译的文本
	 */
	private void loadDefaultResource(String key) {
		String sign = "";
		String tag = "";
		String[] temps = key.split("_");
		if (temps.length >= 2)
			tag = temps[1];
		if (key.startsWith("bo_")) {
			if (temps.length >= 3) {
				String bo = temps[3];
				sign = String.format("locale.bo.%s.%s.properties", tag, bo);// 业务对象信息
			}
		} else {
			sign = String.format("locale.%s.properties", tag);// 非业务对象信息
		}
		String fileName = String.format("%s%s", this.getFilePath(), sign);
		loadResource(fileName);
	}

	/**
	 * 根据文件路径加载资源文件
	 * 
	 * @param fileName
	 *            完整的文件绝对路径
	 * @return 返回资源集合
	 */
	protected ILanguageItems loadResource(String fileName) {
		ILanguageItems items = null;
		if (fileName == null || fileName.equals(""))
			return items;
		File file = new File(fileName);
		if (file.exists()) {
			InputStreamReader reader = null;
			try {
				if (!fileNames.contains(file.getName()) && file.getName().endsWith(".properties")) {
					reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
					items = loadResource(reader, fileName);
					fileNames.add(file.getName());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (Exception exception) {
					throw new RuntimeException(exception);
				}

			}

		}
		return items;

	}

	/**
	 * 根据文件流和完整文件名加载资源文件
	 * 
	 * @param reader
	 *            文件读取流
	 * @param fileName
	 *            完整的文件绝对路径
	 * @return 返回资源集合
	 */
	protected ILanguageItems loadResource(Reader reader, String fileName) {
		if (reader == null)
			return null;
		if (fileName == null || fileName.equals(""))
			return null;
		File file = new File(fileName);
		ILanguageItems items = null;
		if (file.exists()) {
			try {
				String langcode = "";
				String[] temps = file.getName().split("_");
				if (temps.length >= 2) {
					langcode = temps[1].substring(0, temps[1].lastIndexOf("."));
				} else {
					Locale locale = Locale.getDefault();
					langcode = String.format("%s-%s", locale.getLanguage(), locale.getCountry());
				}
				Properties props = new Properties();
				props.load(reader);
				Enumeration<?> enumeration = props.propertyNames();
				items = new LanguageItems();
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
					ILanguageItem item = null;
					if (getLanguageItems().containsKey(key)) {
						item = getLanguageItems().get(key);
					} else {
						item = items.create();
						item.setKey(key);
					}
					item.addContent(langcode, property);
					getLanguageItems().put(key, item);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return items;
	}

	/**
	 * 根据指定的文件路径或文件目录读取资源文件
	 * 
	 * @param fileFolder
	 *            文件路径或目录
	 */
	public static void readResources(String fileFolder) {
		if (fileFolder == null || fileFolder.equals(""))
			return;
		File file = new File(fileFolder);
		if (file.exists()) {
			if (file.isFile()) {
				getInstance().loadResource(fileFolder);
			} else {
				String[] list_File = file.list();
				for (int i = 0; i < list_File.length; i++) {
					readResources(fileFolder + "\\" + list_File[i]);
				}
			}
		}
	}
}
