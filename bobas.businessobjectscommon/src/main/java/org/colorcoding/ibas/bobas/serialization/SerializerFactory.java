package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

/**
 * 序列化者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class SerializerFactory extends ConfigurableFactory<SerializerManager> {

	/**
	 * 输出字符串类型，XML
	 */
	public final static String TYPE_XML = "xml";
	/**
	 * 输出化字符串类型，JSON
	 */
	public final static String TYPE_JSON = "json";

	private SerializerFactory() {
	}

	private volatile static SerializerFactory instance;

	public synchronized static SerializerFactory create() {
		if (instance == null) {
			synchronized (SerializerFactory.class) {
				if (instance == null) {
					instance = new SerializerFactory();
				}
			}
		}
		return instance;
	}

	@Override
	protected SerializerManager createDefault(String typeName) {
		return new SerializerManager();
	}

	private volatile static SerializerManager manager = null;

	public synchronized SerializerManager createManager() {
		if (manager == null) {
			manager = this.create(MyConfiguration.CONFIG_ITEM_SERIALIZATION_WAY, "SerializerManager");
		}
		return manager;
	}
}
