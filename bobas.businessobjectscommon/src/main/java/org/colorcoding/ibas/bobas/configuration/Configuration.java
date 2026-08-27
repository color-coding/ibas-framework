package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Function;

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
					String configFile = Files.valueOf(folder, "app.xml").getPath();
					try {
						instance = create(configFile);
					} catch (Exception e) {
						System.err.println(e);
					}
					if (instance == null) {
						System.err.println(String.format("config: read file's data [%s] faild.", configFile));
						instance = new ConfigurationManagerFile(configFile);
					} else {
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
		File file = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader == null ? null : classLoader.getResource("");
		if (url != null && "file".equalsIgnoreCase(url.getProtocol())) {
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				file = new File(url.getPath());
			}
		}
		if (file == null && Configuration.class.getProtectionDomain() != null
				&& Configuration.class.getProtectionDomain().getCodeSource() != null) {
			URL location = Configuration.class.getProtectionDomain().getCodeSource().getLocation();
			if (location != null && "file".equalsIgnoreCase(location.getProtocol())) {
				try {
					file = new File(location.toURI());
				} catch (URISyntaxException e) {
					file = new File(location.getPath());
				}
			}
		}
		if (file == null) {
			file = new File(System.getProperty("user.dir"));
		}
		if (file.isFile()) {
			file = file.getParentFile();
		}
		if (file.getName().equalsIgnoreCase("classes") && file.getParentFile() != null
				&& file.getParentFile().getName().equalsIgnoreCase("WEB-INF")) {
			file = file.getParentFile();
		}
		// 测试/开发环境：target/classes 或 target/test-classes 对应模块目录。
		if (file.getName().equalsIgnoreCase("classes") || file.getName().equalsIgnoreCase("test-classes")) {
			File target = file.getParentFile();
			if (target != null && target.getName().equalsIgnoreCase("target") && target.getParentFile() != null) {
				file = target.getParentFile();
			}
		}
		return file.getPath();
	}

	/**
	 * 获取资源地址
	 *
	 * @param name 资源名称
	 * @return URI；未找到返回null
	 * @throws URISyntaxException URL转URI失败
	 */
	public static URI getResource(String name) throws URISyntaxException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader == null ? Configuration.class.getClassLoader().getResource(name)
				: classLoader.getResource(name);
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
		if ((!folder.exists() && !folder.mkdirs()) || !folder.isDirectory()) {
			throw new IllegalStateException(Strings.format("configuration: invalid work folder [%s].", folder.getPath()));
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
		if ((!folder.exists() && !folder.mkdirs()) || !folder.isDirectory()) {
			throw new IllegalStateException(Strings.format("configuration: invalid temporary folder [%s].", folder.getPath()));
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
		if ((!folder.exists() && !folder.mkdirs()) || !folder.isDirectory()) {
			throw new IllegalStateException(Strings.format("configuration: invalid data folder [%s].", folder.getPath()));
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
		if ((!folder.exists() && !folder.mkdirs()) || !folder.isDirectory()) {
			throw new IllegalStateException(Strings.format("configuration: invalid log folder [%s].", folder.getPath()));
		}
		return folder.getPath();
	}

	/**
	 * 变量命名模板，${%s}
	 */
	public static final String VARIABLE_NAMING_TEMPLATE = "${%s}";
	/**
	 * 变量表达式
	 * 例如：${Name}。
	 */
	public static final String VARIABLE_PATTERN = "\\$\\{([\\!a-zA-Z].*?)\\}";
	private static final Pattern VARIABLE_PATTERN_COMPILED = Pattern.compile(VARIABLE_PATTERN);
	/**
	 * 变量表达式：变量名后可跟一个受限字符串操作。
	 * 例如：${Name.toLowerCase()}、${Name.replace("a", "b")}。
	 */
	public static final String VARIABLE_EXPRESSION_PATTERN = "^\\s*([\\!a-zA-Z][a-zA-Z0-9_-]*)(?:\\.([a-zA-Z][a-zA-Z0-9_]*)(?:\\((.*)\\)|,(.*))?)?\\s*$";
	private static final Pattern VARIABLE_EXPRESSION_PATTERN_COMPILED = Pattern.compile(VARIABLE_EXPRESSION_PATTERN);

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
		return applyVariables(value, create().getElements());
	}

	/**
	 * 查询并替换字符中的变量
	 * 
	 * @param value     待处理字符
	 * @param variables 变量
	 * @return 替换过字符
	 */
	public static String applyVariables(String value, Iterator<IKeyText> variables) {
		return applyVariables(value, variables, null);
	}

	/**
	 * 应用变量，并允许调用方对最终替换值进行格式化，例如 Shell 参数转义。
	 */
	public static String applyVariables(String value, Iterator<IKeyText> variables,
			Function<String, String> replacementFormatter) {
		if (value == null || variables == null) {
			return value;
		}
		ArrayList<IKeyText> items = new ArrayList<>();
		while (variables.hasNext()) {
			items.add(variables.next());
		}
		Matcher matcher = VARIABLE_PATTERN_COMPILED.matcher(value);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String replacement = resolveVariableExpression(matcher.group(1), items);
			if (replacement == null) {
				// 保持原有行为：无法解析的变量保留原文。
				replacement = matcher.group(0);
			} else if (replacementFormatter != null) {
				replacement = replacementFormatter.apply(replacement);
			}
			matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(result);
		return result.toString();
	}

	/**
	 * 解析变量表达式。只支持白名单字符串操作，禁止任意方法调用。
	 */
	private static String resolveVariableExpression(String expression, Iterable<IKeyText> variables) {
		Matcher expressionMatcher = VARIABLE_EXPRESSION_PATTERN_COMPILED.matcher(expression);
		if (!expressionMatcher.matches()) {
			return null;
		}
		String variableName = expressionMatcher.group(1);
		String operation = expressionMatcher.group(2);
		String operationArguments = expressionMatcher.group(3);
		if (operationArguments == null) {
			operationArguments = expressionMatcher.group(4);
		}
		String source = null;
		boolean found = false;
		for (IKeyText variable : variables) {
			if (variable == null) {
				continue;
			}
			String key = variable.getKey();
			if (key != null && (key.equalsIgnoreCase(variableName)
					|| key.equalsIgnoreCase(String.format(VARIABLE_NAMING_TEMPLATE, variableName)))) {
				source = variable.getText();
				found = true;
				break;
			}
		}
		if (!found) {
			return null;
		}
		if (source == null) {
			source = Strings.VALUE_EMPTY;
		}
		if (operation == null || operation.isEmpty()) {
			return source;
		}
		if ("toLowerCase".equalsIgnoreCase(operation)) {
			return source.toLowerCase(Locale.ROOT);
		}
		if ("toUpperCase".equalsIgnoreCase(operation)) {
			return source.toUpperCase(Locale.ROOT);
		}
		if ("trim".equalsIgnoreCase(operation)) {
			return source.trim();
		}
		if ("replace".equalsIgnoreCase(operation)) {
			String[] arguments = parseVariableArguments(operationArguments);
			if (arguments == null || arguments.length != 2) {
				return null;
			}
			return source.replace(arguments[0], arguments[1]);
		}
		if ("replaceAll".equalsIgnoreCase(operation) || "replaceFirst".equalsIgnoreCase(operation)) {
			String[] arguments = parseVariableArguments(operationArguments);
			if (arguments == null || arguments.length != 2) {
				return null;
			}
			try {
				if ("replaceAll".equalsIgnoreCase(operation)) {
					return source.replaceAll(arguments[0], arguments[1]);
				}
				return source.replaceFirst(arguments[0], arguments[1]);
			} catch (IllegalArgumentException e) {
				// 非法正则或非法替换表达式不应中断整个模板替换，保留原变量。
				return null;
			}
		}
		return null;
	}

	/** 解析 replace 的两个字符串参数，参数可以使用双引号，也可以省略。 */
	private static String[] parseVariableArguments(String text) {
		if (text == null) {
			return null;
		}
		text = text.trim();
		// 兼容历史写法：replace,["a","b"]。
		if (text.length() >= 2 && text.charAt(0) == '[' && text.charAt(text.length() - 1) == ']') {
			text = text.substring(1, text.length() - 1);
		}
		ArrayList<String> arguments = new ArrayList<>(2);
		StringBuilder current = new StringBuilder();
		boolean quoted = false;
		char quote = 0;
		for (int i = 0; i < text.length(); i++) {
			char character = text.charAt(i);
			if ((character == '\"' || character == '\'') && (!quoted || character == quote)) {
				if (!quoted) {
					quoted = true;
					quote = character;
				} else {
					quoted = false;
				}
				continue;
			}
			if (character == ',' && !quoted) {
				arguments.add(current.toString().trim());
				current.setLength(0);
			} else {
				current.append(character);
			}
		}
		if (quoted) {
			return null;
		}
		arguments.add(current.toString().trim());
		if (arguments.size() != 2) {
			return null;
		}
		for (int i = 0; i < arguments.size(); i++) {
			String argument = arguments.get(i);
			if (argument.length() >= 2
					&& ((argument.charAt(0) == '\"' && argument.charAt(argument.length() - 1) == '\"')
							|| (argument.charAt(0) == '\'' && argument.charAt(argument.length() - 1) == '\''))) {
				argument = argument.substring(1, argument.length() - 1);
			}
			arguments.set(i, argument);
		}
		return arguments.toArray(new String[0]);
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

	public static String applyVariables(String value, Iterable<IKeyText> variables,
			Function<String, String> replacementFormatter) {
		if (value == null || variables == null) {
			return value;
		}
		return applyVariables(value, variables.iterator(), replacementFormatter);
	}

}
