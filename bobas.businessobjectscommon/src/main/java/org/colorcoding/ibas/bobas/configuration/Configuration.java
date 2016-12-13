package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
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
                    String configFile = null;
                    try {
                        String folder = getStartupFolder();
                        if (folder.endsWith("target" + File.separator + "test-classes")) {
                            // 测试脚本 target\test-classes
                            folder = (new File(folder)).getParentFile().getParentFile().getPath();
                        }
                        configFile = String.format("%s%sapp.xml", folder, File.separator);
                        configuration = ConfigurationManager.create(configFile);
                    } catch (FileNotFoundException | JAXBException e) {
                        // 读取配置文件出错
                        e.printStackTrace();
                    }
                    if (configuration == null) {
                        // 读取配置文件失败
                        System.err.println(String.format(RuntimeLog.MSG_CONFIG_READ_FILE_DATA_FAILD, configFile));
                        configuration = new ConfigurationManager();
                    } else {
                        // 读取配置文件成功
                        RuntimeLog.log(RuntimeLog.MSG_CONFIG_READ_FILE_DATA, configFile);
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
        if (valueString == null || valueString.isEmpty()) {
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
     * 获取配置项的值
     * 
     * @param key
     *            配置项
     * @return
     */
    public static String getConfigValue(String key) {
        return getInstance().getValue(key);
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
        getInstance().addSetting(key, tmpValue);
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
            synchronized (Configuration.class) {
                if (workFolder == null) {
                    String path = getConfigValue(CONFIG_ITEM_WORK_FOLDER);
                    if (path == null || path.isEmpty()) {
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
        return getWorkFolder() + File.separator + "log";
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
     * 从配置文件重新加载配置项目
     */
    public static void update() {
        String folder = getStartupFolder();
        if (folder.endsWith("target" + File.separator + "test-classes")) {
            // 测试脚本 target\test-classes
            folder = (new File(folder)).getParentFile().getParentFile().getPath();
        }
        String configFile = String.format("%s%sapp.xml", folder, File.separator);
        getInstance().update(configFile);
        RuntimeLog.log(RuntimeLog.MSG_CONFIG_READ_FILE_DATA, configFile);
    }
}
