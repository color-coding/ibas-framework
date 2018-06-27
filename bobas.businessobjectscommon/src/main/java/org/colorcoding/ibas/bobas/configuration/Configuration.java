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

import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 配置
 * 
 * @author Niuren.Zhu
 *
 */
public class Configuration {

	protected static final String MSG_CONFIG_READ_FILE_DATA = "config: read file's data [%s].";
	protected static final String MSG_CONFIG_READ_FILE_DATA_FAILD = "config: read file's data [%s] faild.";

	private volatile static IConfigurationManager instance;

	/**
	 * 创建实例，使用默认位置配置
	 * 
	 * @return
	 */
	public static IConfigurationManager create() {
		if (instance == null) {
			synchronized (Configuration.class) {
				if (instance == null) {
					String folder = getStartupFolder();
					if (folder.endsWith("target" + File.separator + "test-classes")) {
						// 测试脚本 target\test-classes
						folder = (new File(folder)).getParentFile().getParentFile().getPath();
					}
					String configFile = String.format("%s%sapp.xml", folder, File.separator);
					try {
						instance = create(configFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (instance == null) {
						// 读取配置文件失败
						System.err.println(String.format(MSG_CONFIG_READ_FILE_DATA_FAILD, configFile));
						instance = new ConfigurationManagerFile(configFile);
					} else {
						// 读取配置文件成功
						Logger.log(MSG_CONFIG_READ_FILE_DATA, configFile);
					}
				}
			}
		}
		return instance;
	}

	/**
	 * 创建实例
	 * 
	 * @param configFile
	 *            配置文件路径
	 * @return
	 */
	public static IConfigurationManager create(String configFile) throws Exception {
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
	 * @param key
	 *            配置项
	 * 
	 * @param defaultValue
	 *            默认值
	 * 
	 * @return 配置的值（P类型）
	 */
	public static <P> P getConfigValue(String key, P defaultValue) {
		return create().getConfigValue(key, defaultValue);
	}

	/**
	 * 获取配置项的值
	 * 
	 * @param key
	 *            配置项
	 * @return
	 */
	public static String getConfigValue(String key) {
		return create().getConfigValue(key);
	}

	/**
	 * 添加配置项，存在则替换
	 * 
	 * @param key
	 *            项
	 * @param value
	 *            值
	 */
	public static void addConfigValue(String key, Object value) {
		String tmpValue = String.valueOf(value);
		create().addConfigValue(key, tmpValue);
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
	 * @param type
	 *            资源名称
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
	private volatile static String workFolder = null;

	/**
	 * 获取工作目录
	 * 
	 * @return
	 */
	public static String getWorkFolder() {
		if (workFolder == null) {
			String path = getConfigValue(CONFIG_ITEM_WORK_FOLDER);
			if (path == null || path.isEmpty()) {
				// 没有配置工作目录
				path = getStartupFolder();
			}
			workFolder = (new File(path)).getPath();
		}
		return workFolder;
	}

	/**
	 * 获取临时目录
	 * 
	 * @return
	 */
	public static String getTempFolder() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * 获取数据目录
	 * 
	 * @return
	 */
	public static String getDataFolder() {
		return getWorkFolder() + File.separator + "data";
	}

	/**
	 * 获取日志目录
	 * 
	 * @return
	 */
	public static String getLogFolder() {
		return getWorkFolder() + File.separator + "logs";
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
	 * @param variable
	 *            待处理字符
	 * @return 替换过字符
	 */
	public static String applyVariables(String variable) {
		String value = applyVariables(variable, new Iterator<IKeyText>() {

			private Iterator<IConfigurationElement> iterator = create().getElements().iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public IKeyText next() {
				IConfigurationElement next = iterator.next();
				return new IKeyText() {

					@Override
					public void setText(String value) {
						next.setValue(value);
					}

					@Override
					public void setKey(String value) {
						next.setKey(value);
					}

					@Override
					public String getText() {
						return next.getValue();
					}

					@Override
					public String getKey() {
						return next.getKey();
					}

					@Override
					public String toString() {
						return String.format("{key text: %s %s}", this.getKey(), this.getText());
					}

				};
			}

		});
		return value;
	}

	/**
	 * 查询并替换字符中的变量
	 * 
	 * @param value
	 *            待处理字符
	 * @param variables
	 *            变量
	 * @return 替换过字符
	 */
	public static String applyVariables(String value, Iterator<IKeyText> variables) {
		if (value != null && variables != null) {
			ArrayList<String> names = new ArrayList<>();
			Matcher matcher = Pattern.compile(VARIABLE_PATTERN).matcher(value);
			while (matcher.find()) {
				// 带格式名称${}
				names.add(matcher.group(0));
			}
			while (variables.hasNext()) {
				IKeyText item = variables.next();
				for (String name : names) {
					// 不带格式名称
					String tName = name.substring(2, name.length() - 1);
					if (name.equalsIgnoreCase(item.getKey()) || tName.equalsIgnoreCase(item.getKey())) {
						value = value.replace(name, item.getText() == null ? new String() : item.getText());
						break;
					}
				}
			}
		}
		return value;
	}

	/**
	 * 查询并替换字符中的变量
	 * 
	 * @param value
	 *            待处理字符
	 * @param variables
	 *            变量
	 * @return 替换过字符
	 */
	public static String applyVariables(String value, Iterable<IKeyText> variables) {
		if (value == null || variables == null) {
			return value;
		}
		return applyVariables(value, variables.iterator());
	}

}
