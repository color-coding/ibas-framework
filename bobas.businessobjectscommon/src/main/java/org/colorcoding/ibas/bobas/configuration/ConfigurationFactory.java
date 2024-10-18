package org.colorcoding.ibas.bobas.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 审批工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class ConfigurationFactory extends ConfigurableFactory<IConfigurationManager> {
	private ConfigurationFactory() {
	}

	private volatile static ConfigurationFactory instance;

	public synchronized static ConfigurationFactory create() {
		if (instance == null) {
			synchronized (ConfigurationFactory.class) {
				if (instance == null) {
					instance = new ConfigurationFactory();
				}
			}
		}
		return instance;
	}

	protected IConfigurationManager createDefault(String typeName) {
		return new IConfigurationManager() {

			public Collection<KeyText> getElements() {
				return new ArrayList<>();
			}

			public String getConfigValue(String key) {
				return MyConfiguration.getConfigValue(key);
			}

			public <P> P getConfigValue(String key, P defaultValue) {
				return MyConfiguration.getConfigValue(key, defaultValue);
			}

			public void addConfigValue(String key, String value) {
			}

			public String getConfigSign() {
				return null;
			}

			public void setConfigSign(String configSign) {
			}

			public void save() {
			}

			public void update() {
			}

		};
	}

	public synchronized IConfigurationManager createManager() {
		return this.create(MyConfiguration.CONFIG_ITEM_CONFIGURATION_WAY, "ConfigurationManager");
	}

}
