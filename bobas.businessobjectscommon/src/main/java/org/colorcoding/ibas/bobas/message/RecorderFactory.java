package org.colorcoding.ibas.bobas.message;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.message.IMessageRecorder;
import org.colorcoding.ibas.bobas.message.MessageRecorder4File;
import org.colorcoding.ibas.bobas.message.RecorderFactory;

/**
 * 记录者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class RecorderFactory extends ConfigurableFactory<IMessageRecorder> {
	private RecorderFactory() {
	}

	private volatile static RecorderFactory instance;

	public synchronized static RecorderFactory create() {
		if (instance == null) {
			synchronized (RecorderFactory.class) {
				if (instance == null) {
					instance = new RecorderFactory();
				}
			}
		}
		return instance;
	}

	public synchronized IMessageRecorder createRecorder() {
		IMessageRecorder recorder = null;
		String type = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_MESSAGE_RECORDER_WAY);
		if (type != null && !type.isEmpty()) {
			// 创建实现的实例
			try {
				recorder = this.newInstance(type);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if (recorder == null) {
			// 没有实例，则使用默认
			String moduleName = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_MODULE_NAME);
			String logFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_FILE_FOLDER);
			if (logFolder == null || logFolder.isEmpty()) {
				logFolder = MyConfiguration.getLogFolder();
			}
			StringBuilder fileName = new StringBuilder();
			fileName.append("ibas");
			fileName.append("_");
			fileName.append("runtime");
			if (moduleName != null && !moduleName.isEmpty()) {
				// 配置了模块名称
				fileName.append("_");
				fileName.append(moduleName);
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

	@Override
	protected IMessageRecorder createDefault(String typeName) {
		return null;
	}

}
