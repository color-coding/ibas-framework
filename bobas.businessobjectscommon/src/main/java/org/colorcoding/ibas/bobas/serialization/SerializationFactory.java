package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

/**
 * 序列化者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class SerializationFactory {

	/**
	 * 输出字符串类型，XML
	 */
	public final static String TYPE_XML = "xml";
	/**
	 * 输出化字符串类型，JSON
	 */
	public final static String TYPE_JSON = "json";

	private SerializationFactory() {
	}

	private volatile static SerializerManager instance;

	public synchronized static SerializerManager createManager() {
		if (instance == null) {
			synchronized (SerializationFactory.class) {
				if (instance == null) {
					_SerializerFactory factory = new _SerializerFactory();
					instance = factory.create();
				}
			}
		}
		return instance;
	}

	private static class _SerializerFactory extends ConfigurableFactory<SerializerManager> {

		@Override
		protected SerializerManager createDefault(String typeName) {
			return new SerializerManager();
		}

		public synchronized SerializerManager create() {
			return this.create(MyConfiguration.CONFIG_ITEM_SERIALIZATION_WAY, "SerializerManager");
		}
	}
}
