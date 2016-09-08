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
	 * @param sign
	 *            类型标记
	 * @return
	 */
	public synchronized static IMessageRecorder createRecorder(String sign) {
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
			recorder = new MessageRecorder4File(sign);			
		}
		// 未配置则使用默认的
		return recorder;
	}

}
