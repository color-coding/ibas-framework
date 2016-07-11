package org.colorcoding.ibas.bobas.messages;

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
	 * @return
	 */
	public synchronized static IMessageRecorder createRecorder() {
		String type = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_MESSAGE_RECORDER_WAY);
		if (type != null && !type.equals("")) {
			// 创建实现的实例
			try {
				Class<?> recorderClass = getInstance(RecorderFactory.class, "", type);
				if (recorderClass != null) {
					return (IMessageRecorder) recorderClass.newInstance();
				}
			} catch (BOFactoryException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		// 未配置则使用默认的
		return new MessageRecorder4File();
	}

}
