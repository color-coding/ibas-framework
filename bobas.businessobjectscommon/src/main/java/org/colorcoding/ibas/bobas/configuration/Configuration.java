package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 配置
 * 
 * @author Niuren.Zhu
 *
 */
public class Configuration {

	private volatile static IConfigurationManager configuration;

	public static IConfigurationManager getInstance() {
		if (configuration == null) {
			synchronized (Configuration.class) {
				if (configuration == null) {
					try {
						String configFile = String.format("%s%sapp.xml", getStartupFolder(), File.separator);
						configuration = ConfigurationManager.create(configFile);
					} catch (FileNotFoundException | JAXBException e) {
						RuntimeLog.log(e);
					}
					if (configuration == null) {
						configuration = new ConfigurationManager();
					}
				}
			}
		}
		return configuration;
	}

	/**
	 * 程序启动的目录（主要的配置文件目录）
	 * 
	 * @return
	 */
	public static String getStartupFolder() {
		String path;
		File file = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath());
		if (file.getParentFile().isDirectory() && file.getParentFile().getName().equals("WEB-INF")) {
			// web路径
			path = file.getParentFile().getPath();
		} else if (file.isDirectory()) {
			path = file.getPath();
		} else {
			path = System.getProperty("user.dir");
		}
		return path;
	}

	/**
	 * 获取配置的值
	 * 
	 * @param key
	 *            配置项
	 * 
	 * @param defaultValue
	 *            默认值
	 * 
	 * @return 配置的值（P类型）
	 */
	@SuppressWarnings("unchecked")
	public static <P> P getConfigValue(String key, P defaultValue) {
		String valueString = getConfigValue(key);
		if (valueString == null || valueString.equals("")) {
			return defaultValue;
		} else {
			try {
				// 强行转换配置值为P类型
				if (defaultValue != null) {
					return (P) DataConvert.convert(defaultValue.getClass(), valueString);
				}
				return (P) valueString;
			} catch (Exception e) {
				RuntimeLog.log(e);
				return defaultValue;
			}
		}
	}

	/**
	 * 静态方法，供直接调用
	 * 
	 * @param key
	 * @return
	 */
	public static String getConfigValue(String key) {
		return getInstance().getValue(key);
	}

	/**
	 * 配置项目-工作目录
	 */
	public static String CONFIG_ITEM_WORK_FOLDER = "WorkFolder";

	private volatile static String workFolder = null;

	/**
	 * 获取工作目录
	 * 
	 * @param type
	 *            类型（i18n）
	 * @return
	 */
	public static String getWorkFolder() {
		if (workFolder == null) {
			synchronized (Configuration.class) {
				if (workFolder == null) {
					String path = getConfigValue(CONFIG_ITEM_WORK_FOLDER);
					if (path == null || path.equals("")) {
						// 没有配置工作目录
						path = getStartupFolder();
					}
					workFolder = (new File(path)).getPath();
				}
			}
		}
		return workFolder;
	}

	/**
	 * 获取资源地址
	 * 
	 * @param type
	 *            资源名称
	 * @return
	 */
	public static URL getResource(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name);
	}

}
