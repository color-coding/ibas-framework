package org.colorcoding.ibas.bobas.configuration;

import java.util.ArrayList;
import java.util.Collection;

import org.colorcoding.ibas.bobas.MyConfiguration;

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

	@Override
	protected IConfigurationManager createDefault(String typeName) {
		return new IConfigurationManager() {

			@Override
			public Collection<IConfigurationElement> getElements() {
				return new ArrayList<>();
			}

			@Override
			public String getConfigValue(String key) {
				return MyConfiguration.getConfigValue(key);
			}

			@Override
			public <P> P getConfigValue(String key, P defaultValue) {
				return MyConfiguration.getConfigValue(key, defaultValue);
			}

			@Override
			public void addConfigValue(String key, String value) {
			}

			@Override
			public String getConfigSign() {
				return null;
			}

			@Override
			public void setConfigSign(String configSign) {
			}

			@Override
			public void save() {
			}

			@Override
			public void update() {
			}

		};
	}

	public synchronized IConfigurationManager createManager() {
		return this.create(MyConfiguration.CONFIG_ITEM_CONFIGURATION_WAY, "ConfigurationManager");
	}

}
