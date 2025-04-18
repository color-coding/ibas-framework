package org.colorcoding.ibas.bobas.configuration;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 配置工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class ConfigurationFactory {
	private ConfigurationFactory() {
	}

	public synchronized static ConfigurationManager createManager() {
		return new Factory().create();
	}

	private static class Factory extends ConfigurableFactory<ConfigurationManager> {

		public synchronized ConfigurationManager create() {
			return this.create(MyConfiguration.CONFIG_ITEM_CONFIGURATION_WAY, "ConfigurationManager");
		}

		@Override
		protected ConfigurationManager createDefault(String typeName) {
			return Configuration.create();
		}
	}
}