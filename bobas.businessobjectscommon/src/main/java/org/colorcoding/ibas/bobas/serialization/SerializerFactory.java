package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

/**
 * 序列化者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class SerializerFactory extends ConfigurableFactory<ISerializerManager> {

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
	protected ISerializerManager createDefault(String typeName) {
		return new SerializerManager();
	}

	private volatile static ISerializerManager manager = null;

	public synchronized ISerializerManager createManager() {
		if (manager == null) {
			manager = this.create(MyConfiguration.CONFIG_ITEM_SERIALIZATION_WAY, "SerializerManager");
		}
		return manager;
	}
}
