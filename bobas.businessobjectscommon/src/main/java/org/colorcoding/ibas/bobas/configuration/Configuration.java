package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
	 * @return
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
						e.printStackTrace();
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
	 * 创建实例
	 * 
	 * @param configFile 配置文件路径
	 * @return
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
	 * @return
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
	 * 
	 * @return
	 */
	public static String getStartupFolder() {
		try {
			File file = null;
			URL url = Thread.currentThread().getContextClassLoader().getResource("");
			String path = null;
			if (url != null) {
				URI uri = url.toURI();
				if (uri != null) {
					path = uri.getPath();
				}
				if (path == null) {
					path = url.getPath();
					if (path != null)
						path = java.net.URLDecoder.decode(path, "UTF-8");
				}
			}
			// file:/E:/WorkTemp/ibas/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/ibcp.systemcenter.service/WEB-INF/classes/
			// 取到的值如上
			if (path != null) {
				if (path.split(":").length > 2) {
					path = path.substring(path.indexOf(":") + 1, path.length());
				}
				if (path.indexOf("!") > 0) {
					path = path.substring(0, path.indexOf("!"));
				}
			}
			if (path == null) {
				path = System.getProperty("user.dir");
			}
			file = new File(path);
			if (file.isFile()) {
				file = file.getParentFile();
			}
			if (file.getParentFile() != null && file.getParentFile().isDirectory()
					&& file.getParentFile().getName().equals("WEB-INF")) {
				// web路径
				file = file.getParentFile();
			}
			return file.getPath();
		} catch (URISyntaxException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取资源地址
	 * 
	 * @param type 资源名称
	 * @return 统一格式（此对象避免路径的中文问题）
	 * @throws URISyntaxException
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
	 * @return
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
	 * @return
	 */
	public static String getTempFolder() {
		File folder = Strings.isNullOrEmpty(System.getProperty("java.io.tmpdir")) ? new File(getWorkFolder(), "temp")
				: new File(System.getProperty("java.io.tmpdir"));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder.getPath();
	}

	/**
	 * 获取数据目录
	 * 
	 * @return
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
	 * @return
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
		String tName;
		ArrayList<String> names = new ArrayList<>(8);
		Matcher matcher = Pattern.compile(VARIABLE_PATTERN).matcher(value);
		while (matcher.find()) {
			tName = matcher.group(0);
			// 带格式名称${}
			names.add(tName);
			// 不带格式名称
			names.add(tName.substring(2, tName.length() - 1));
		}
		IKeyText variable;
		while (variables.hasNext()) {
			variable = variables.next();
			for (String name : names) {
				if (name.equalsIgnoreCase(variable.getKey())) {
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
