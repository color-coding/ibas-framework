package org.colorcoding.ibas.bobas.messages;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;

/**
 * 记录者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class RecorderFactory extends ConfigurableFactory {

    /**
     * 创建流程管理员实例
     * 
     * @param sign
     *            类型标记
     * @return
     */
    public synchronized static IMessageRecorder createRecorder() {
        IMessageRecorder recorder = null;
        String type = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_MESSAGE_RECORDER_WAY);
        if (type != null && !type.isEmpty()) {
            // 创建实现的实例
            try {
                Class<?> recorderClass = getInstance(RecorderFactory.class, "", type);
                if (recorderClass != null) {
                    recorder = (IMessageRecorder) recorderClass.newInstance();
                }
            } catch (BOFactoryException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (recorder == null) {
            // 没有实例，则使用默认
            String moduleName = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_MODULE_NAME);
            String logFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_FILE_FOLDER);
            if (logFolder == null || logFolder.isEmpty()) {
                logFolder = MyConfiguration.getWorkFolder() + File.separator + "log";
            }
            StringBuilder fileName = new StringBuilder();
            fileName.append("ibas");
            fileName.append("_");
            fileName.append("runtime");
            if (moduleName != null && !moduleName.isEmpty()) {
                // 配置了模块名称
                fileName.append("_");
                fileName.append(moduleName);
            } else {
                // 未配置模块名称
                String confName = String.format(".%s", MyConfiguration.CONFIG_ITEM_MODULE_NAME);
                for (File file : new File(MyConfiguration.getWorkFolder()).listFiles()) {
                    if (file.isFile()) {
                        if (file.getName().endsWith(confName)) {
                            // 通过文件获取模块名称
                            fileName.append("_");
                            int len = file.getName().lastIndexOf(confName);
                            if (len > 0 && len < file.getName().length()) {
                                fileName.append(file.getName().substring(0, len));
                            }
                        }
                    }
                }
            }
            fileName.append("_");
            fileName.append("%s");
            fileName.append(".");
            fileName.append("log");
            MessageRecorder4File messageRecorder4File = new MessageRecorder4File();
            messageRecorder4File.setFileSign(fileName.toString());
            messageRecorder4File.setWorkFolder(logFolder);
            messageRecorder4File
                    .setLimitSize(MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_FILE_SIZE_LIMIT, 50));
            recorder = messageRecorder4File;
        }
        // 未配置则使用默认的
        return recorder;
    }

}
