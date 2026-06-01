package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

/**
 * 序列化工厂
 *
 * @author Niuren.Zhu
 *
 */
public class SerializationFactory {

	/** 序列化类型：XML */
	public final static String TYPE_XML = "xml";
	/** 序列化类型：JSON */
	public final static String TYPE_JSON = "json";

	private SerializationFactory() {
	}

	private volatile static SerializerManager instance;

	/**
	 * 创建序列化管理器（单例）
	 *
	 * @return 序列化管理器实例
	 */
	public static SerializerManager createManager() {
		if (instance == null) {
			synchronized (SerializationFactory.class) {
				if (instance == null) {
					instance = new Factory().create();
				}
			}
		}
		return instance;
	}

	private static class Factory extends ConfigurableFactory<SerializerManager> {

		public synchronized SerializerManager create() {
			return this.create(MyConfiguration.CONFIG_ITEM_SERIALIZATION_WAY, SerializerManager.class.getSimpleName());
		}

		@Override
		protected SerializerManager createDefault(String typeName) {
			return new SerializerManager();
		}

	}
}