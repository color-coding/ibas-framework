package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

public class Configuration {
	protected Configuration() {

	}

	/**
	 * 配置文件默认名称
	 */
	private static final String CONFIGURATION_FILENAME = "app.xml";
	/**
	 * 配置项集合
	 */
	private HashMap<String, IConfigurationElement> settings;

	private HashMap<String, IConfigurationElement> getSettings() {
		if (settings == null) {
			settings = new HashMap<String, IConfigurationElement>();
		}
		return settings;
	}

	/**
	 * 单例模式
	 */
	private volatile static Configuration configuration;

	public static Configuration getConfiguration() {
		if (configuration == null) {
			synchronized (Configuration.class) {
				if (configuration == null) {
					configuration = new Configuration();
				}
			}
		}
		return configuration;
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
		return getConfiguration().getValue(key);
	}

	/**
	 * 获取配置的值
	 * 
	 * @param key
	 *            配置项
	 * 
	 * @return 配置项的值（字符串）
	 * 
	 */
	private String getValue(String key) {
		String result = "";
		if (key == null || key.equals(""))
			return result;
		if (this.settings == null) {
			// 初始化读取配置项
			this.putSettings(loadConfigFile());
		}
		if (this.getSettings().containsKey(key)) {
			IConfigurationElement element = this.settings.get(key);
			result = element.getValue();
		}
		return result;
	}

	/**
	 * 将配置集合添加到hashmap 中
	 * 
	 * @param elements
	 *            配置项集合
	 */
	public void putSettings(IConfigurationElements elements) {
		if (elements == null)
			return;
		HashMap<String, IConfigurationElement> map = getConfiguration().getSettings();
		for (IConfigurationElement element : elements) {
			String key = element.getKey();
			String value = element.getValue();
			if (key == null || key.equals(""))
				continue;
			if (value == null || value.equals(""))
				continue;
			map.put(key, element);
		}
	}

	/**
	 * 加载默认的配置文件
	 * 
	 * @return 返回配置项集合
	 */
	public IConfigurationElements loadConfigFile() {
		return loadConfigFile(this.getDefaultPath());
	}

	/**
	 * 加载指定路径的配置文件
	 * 
	 * @param filePath
	 *            指定路径
	 * @return 返回配置项集合
	 */
	public IConfigurationElements loadConfigFile(String filePath) {
		if (filePath == null || filePath.equals(""))
			return null;
		IConfigurationManager configurationManager = new ConfigurationManager();
		return configurationManager.readSettings(filePath);

	}

	/**
	 * 添加配置项
	 * 
	 * @param element
	 *            配置项
	 * @return true 表示成功
	 */
	public boolean addSetting(IConfigurationElement element) {
		if (element == null)
			return false;
		String key = element.getKey();
		if (key == null || key.equals(""))
			return false;
		this.getSettings().put(key, element);
		return true;
	}

	/**
	 * 添加配置项
	 * 
	 * @param key
	 *            配置key
	 * @param value
	 *            配置value
	 * @return true 表示成功
	 */
	public boolean addSetting(String key, String value) {
		if (key == null || key.equals(""))
			return false;
		if (value == null || value.equals(""))
			return false;
		ConfigurationElement element = new ConfigurationElement();
		element.setKey(key);
		element.setValue(value);
		return addSetting(element);
	}

	/**
	 * 修改配置项
	 * 
	 * @param key
	 *            配置key
	 * @param value
	 *            配置value
	 * @return true 表示成功
	 */
	public boolean setSetting(String key, String value) {
		return addSetting(key, value);
	}

	/**
	 * 删除配置文件项
	 * 
	 * @param key
	 *            配置文件-key
	 * @return true 表示成功
	 */
	public boolean deleteConfigElement(String key) {
		if (key == null || key.equals(""))
			return false;
		HashMap<String, IConfigurationElement> map = this.getSettings();
		if (!map.containsKey(key)) {
			return false;
		} else {
			map.remove(key);
		}
		return true;
	}

	/**
	 * 按照默认路径保存配置项
	 */
	public void saveSettings() {
		saveSettings(this.getDefaultPath());
	}

	/**
	 * 按照指定路径保存配置项
	 * 
	 * @param filePath
	 */
	public void saveSettings(String filePath) {
		IConfigurationManager configurationManager = new ConfigurationManager();
		IConfigurationElements elements = new ConfigurationElements();
		for (Entry<String, IConfigurationElement> entry : this.getSettings().entrySet()) {
			IConfigurationElement element = elements.create();
			String key = entry.getKey();
			IConfigurationElement value = entry.getValue();
			if (key == null || key.equals(""))
				continue;
			element.setKey(key);
			element.setValue(value.getValue());
		}
		configurationManager.saveSettings(elements, filePath);
	}

	/**
	 * 获取默认配置路径
	 * 
	 * @return 默认配置路径
	 */
	protected String getDefaultPath() {
		String result = "";
		// 获取项目根目录
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		String classPath = path.substring(0, path.lastIndexOf("/"));
		String middlePath = classPath.substring(0, classPath.lastIndexOf("/"));
		String rootPath = middlePath.substring(0, middlePath.lastIndexOf("/"));
		String filePath = String.format("%s/%s", rootPath, CONFIGURATION_FILENAME);
		File file_rootPath = new File(filePath);
		if (file_rootPath.exists()) {
			result = filePath;
			return result;
		}
		// 获取服务器配置文件路径
		String servicePath = System.getProperty("catalina.base");
		if (servicePath != null) {
			filePath = String.format("%s/%s", servicePath, CONFIGURATION_FILENAME);
			File file_ServicePath = new File(filePath);
			if (file_ServicePath.exists()) {
				result = filePath;
				return result;
			}
		}
		// 获取当前工作目录路径
		String currentPath = System.getProperty("user.dir");

		filePath = String.format("%s/%s", currentPath, CONFIGURATION_FILENAME);
		File file_currentPath = new File(filePath);
		if (file_currentPath.exists()) {
			result = filePath;
			return result;
		}
		return result;
	}
}
