package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 配置
 * 
 * @author Niuren.Zhu
 *
 */
public class Configuration {

	private volatile static ConfigurationManager instance;

	/**
	 * 创建实例，使用默认位置配置
	 *
	 * @return 配置管理器实例（单例）
	 */
	public static ConfigurationManager create() {
		if (instance == null) {
			synchronized (Configuration.class) {
				if (instance == null) {
					String folder = getStartupFolder();
					if (folder.endsWith("target" + File.separator + "test-classes")
							|| folder.endsWith("target" + File.separator + "classes")) {
						// 测试脚本 target\test-classes
						folder = (new File(folder)).getParentFile().getParentFile().getPath();
					}
					String configFile = Files.valueOf(folder, "app.xml").getPath();
					try {
						instance = create(configFile);
					} catch (Exception e) {
						System.err.println(e);
					}
					if (instance == null) {
						// 读取配置文件失败
						System.err.println(String.format("config: read file's data [%s] faild.", configFile));
						instance = new ConfigurationManagerFile(configFile);
					} else {
						// 读取配置文件成功
						Logger.log("config: read file's data [%s].", configFile);
					}
				}
			}
		}
		return instance;
	}

	/**
	 * 创建实例（指定配置文件路径）
	 *
	 * @param configFile 配置文件路径
	 * @return 配置管理器实例（单例）
	 * @throws Exception 读取配置文件失败
	 */
	public static ConfigurationManager create(String configFile) throws Exception {
		synchronized (Configuration.class) {
			ConfigurationManagerFile manager = new ConfigurationManagerFile();
			manager.setConfigFile(configFile);
			manager.update();
			instance = manager;
		}
		return instance;
	}

	/**
	 * 重新加载配置文件
	 * 
	 * @throws Exception
	 */
	public static void update() throws Exception {
		create().update();
	}

	/**
	 * 获取配置的值
	 * 
	 * @param key          配置项
	 * 
	 * @param defaultValue 默认值
	 * 
	 * @return 配置的值（P类型）
	 */
	public static <P> P getConfigValue(String key, P defaultValue) {
		return create().getConfigValue(key, defaultValue);
	}

	/**
	 * 获取配置项的值
	 *
	 * @param key 配置项
	 * @return 配置值；未找到返回null
	 */
	public static String getConfigValue(String key) {
		return create().getConfigValue(key);
	}

	/**
	 * 添加配置项，存在则替换
	 * 
	 * @param key   项
	 * @param value 值
	 */
	public static void addConfigValue(String key, Object value) {
		create().addConfigValue(key, value == null ? null : String.valueOf(value));
	}

	/**
	 * 程序启动的目录（主要的配置文件目录）
	 * <p>
	 * 解析优先级：
	 * <ol>
	 * <li>线程上下文类加载器的类路径根目录（常规classpath、WEB-INF/classes）</li>
	 * <li>ProtectionDomain 获取类加载位置（java -jar 场景下 getResource 返回null）</li>
	 * <li>系统属性 user.dir（最终回退）</li>
	 * </ol>
	 * 特殊处理：
	 * <ul>
	 * <li>若路径指向文件（如JAR），取其所在目录</li>
	 * <li>Web容器场景，WEB-INF/classes 定位到 WEB-INF 目录</li>
	 * </ul>
	 *
	 * @return 启动目录路径
	 */
	public static String getStartupFolder() {
		try {
			File file = null;
			// 优先：线程上下文类加载器的类路径根目录
			if (file == null) {
				URL url = Thread.currentThread().getContextClassLoader().getResource("");
				if (url != null) {
					file = new File(url.toURI());
				}
			}
			// 回退：ProtectionDomain 获取类加载位置
			// java -jar 场景下 getResource("") 可能返回 null，此时可获取到 JAR 文件路径
			if (file == null) {
				URL codeSourceLocation = Configuration.class.getProtectionDomain().getCodeSource().getLocation();
				if (codeSourceLocation != null) {
					file = new File(codeSourceLocation.toURI());
				}
			}
			// 最终回退：当前工作目录
			if (file == null) {
				file = new File(System.getProperty("user.dir"));
			}
			// 如果是文件（如JAR），取其所在目录
			if (file.isFile()) {
				file = file.getParentFile();
			}
			// Web容器场景：类路径为 .../WEB-INF/classes，定位到 WEB-INF 目录
			if (file.getName().equalsIgnoreCase("classes") && file.getParentFile() != null
					&& file.getParentFile().getName().equalsIgnoreCase("WEB-INF")) {
				file = file.getParentFile();
			}
			return file.getPath();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取资源地址
	 *
	 * @param name 资源名称
	 * @return URI；未找到返回null
	 * @throws URISyntaxException URL转URI失败
	 */
	public static URI getResource(String name) throws URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(name);
		if (url == null) {
			return null;
		}
		return url.toURI();
	}

	/**
	 * 配置项目-工作目录
	 */
	public final static String CONFIG_ITEM_WORK_FOLDER = "WorkFolder";

	/**
	 * 获取工作目录
	 *
	 * @return 工作目录路径（目录不存在则自动创建）
	 */
	public static String getWorkFolder() {
		String path = getConfigValue(CONFIG_ITEM_WORK_FOLDER);
		// 没有配置工作目录
		if (Strings.isNullOrEmpty(path)) {
			path = getStartupFolder();
			create().addConfigValue(CONFIG_ITEM_WORK_FOLDER, path);
		}
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder.getPath();
	}

	/**
	 * 获取临时目录
	 *
	 * @return 临时目录路径（目录不存在则自动创建）
	 */
	public static String getTempFolder() {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		File folder = Strings.isNullOrEmpty(tmpFolder) ? new File(getWorkFolder(), "temp") : new File(tmpFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder.getPath();
	}

	/**
	 * 获取数据目录
	 *
	 * @return 数据目录路径（目录不存在则自动创建）
	 */
	public static String getDataFolder() {
		File folder = new File(getWorkFolder(), "data");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder.getPath();
	}

	/**
	 * 获取日志目录
	 *
	 * @return 日志目录路径（目录不存在则自动创建）
	 */
	public static String getLogFolder() {
		File folder = new File(getWorkFolder(), "logs");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder.getPath();
	}

	/**
	 * 变量命名模板，${%s}
	 */
	public static final String VARIABLE_NAMING_TEMPLATE = "${%s}";
	/**
	 * 变量样式，${XXXXXX}
	 */
	public static final String VARIABLE_PATTERN = "\\$\\{([\\!a-zA-Z].*?)\\}";

	/**
	 * 用配置项替换字符中的变量
	 * 
	 * @param variable 待处理字符
	 * @return 替换过字符
	 */
	public static String applyVariables(String value) {
		if (Strings.isNullOrEmpty(value)) {
			return value;
		}
		ArrayList<String> names = new ArrayList<>(4);
		Matcher matcher = Pattern.compile(VARIABLE_PATTERN).matcher(value);
		while (matcher.find()) {
			// 带格式名称${}
			names.add(matcher.group(0));
		}
		Object variable;
		for (String name : names) {
			variable = create().getConfigValue(name);
			if (variable == null) {
				// 不带格式名称
				variable = create().getConfigValue(name.substring(2, name.length() - 1));
			}
			if (variable == null) {
				continue;
			}
			value = value.replace(name, variable == null ? Strings.VALUE_EMPTY : variable.toString());
		}
		return value;
	}

	/**
	 * 查询并替换字符中的变量
	 * 
	 * @param value     待处理字符
	 * @param variables 变量
	 * @return 替换过字符
	 */
	public static String applyVariables(String value, Iterator<IKeyText> variables) {
		if (value == null || variables == null) {
			return value;
		}
		ArrayList<String> names = new ArrayList<>(8);
		Matcher matcher = Pattern.compile(VARIABLE_PATTERN).matcher(value);
		while (matcher.find()) {
			names.add(matcher.group(0));
		}
		String tName;
		IKeyText variable;
		while (variables.hasNext()) {
			variable = variables.next();
			for (String name : names) {
				// 不带格式名称
				tName = name.substring(2, name.length() - 1);
				if (name.equalsIgnoreCase(variable.getKey()) || tName.equalsIgnoreCase(variable.getKey())) {
					value = value.replace(name, variable.getText() == null ? Strings.VALUE_EMPTY : variable.getText());
					break;
				}
			}
		}
		return value;
	}

	/**
	 * 查询并替换字符中的变量
	 * 
	 * @param value     待处理字符
	 * @param variables 变量
	 * @return 替换过字符
	 */
	public static String applyVariables(String value, Iterable<IKeyText> variables) {
		if (value == null || variables == null) {
			return value;
		}
		return applyVariables(value, variables.iterator());
	}

}
