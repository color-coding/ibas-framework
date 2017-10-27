package org.colorcoding.ibas.bobas.configuration;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 可配置工厂
 * 
 * @author Niuren.Zhu
 *
 * @param <T>
 *            工厂创建的类型
 */
public abstract class ConfigurableFactory<T> {

	protected static final String MSG_CONFIG_COMBINED_CLASS_NAME = "config: combined class name [%s].";
	protected static final String MSG_CONFIG_NOT_CONFIGURATION_USING_DEFALUT = "config: not configured [%s], using defalut [%s].";

	/**
	 * 获取类型
	 * 
	 * @param names
	 *            名称，任意值包含“.”，则不在补子类命名空间
	 * @return 类型，未找到为null
	 * @throws BOFactoryException
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getClass(String... names) throws ClassNotFoundException {
		StringBuilder stringBuilder = new StringBuilder();
		boolean nsDone = false;
		for (String item : names) {
			if (item == null || item.isEmpty()) {
				continue;
			}
			// 补齐命名空间分隔符
			if (stringBuilder.length() > 0 && stringBuilder.lastIndexOf(".") != stringBuilder.length()) {
				stringBuilder.append(".");
			}
			if (item.indexOf(".") >= 0) {
				nsDone = true;
			}
			// 拼接命令空间
			stringBuilder.append(item.trim());
		}
		// 参数中不包含“.”则补齐命名空间
		if (!nsDone) {
			String namespace = this.getClass().getName();
			int index = namespace.lastIndexOf(".");
			if (index > 0) {
				namespace = namespace.substring(0, index + 1);
			}
			stringBuilder.insert(0, namespace);
		}
		// 去除最后分隔符
		if (stringBuilder.length() > 0 && stringBuilder.lastIndexOf(".") == stringBuilder.length()) {
			stringBuilder.setLength(stringBuilder.length() - 1);
		}
		// 获取类类型
		String fullName = stringBuilder.toString();
		Logger.log(MessageLevel.DEBUG, MSG_CONFIG_COMBINED_CLASS_NAME, fullName);
		return (Class<T>) BOFactory.create().loadClass(fullName);
	}

	/**
	 * 获取类型实例
	 * 
	 * @param names
	 *            名称
	 * @return 类型实例
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public T newInstance(String... names)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<T> type = this.getClass(names);
		if (type == null) {
			return null;
		}
		return type.newInstance();
	}

	/**
	 * 创建实例
	 * 
	 * @param configKey
	 *            配置项，提供命名空间
	 * @param typeName
	 *            类名
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BOFactoryException
	 */
	protected T create(String typeName) {
		// 使用配置的实例
		try {
			return this.newInstance(typeName);
		} catch (Exception e) {
			throw new RuntimeException(I18N.prop("msg_bobas_configurable_factory_create_instance_faild", typeName), e);
		}
	}

	/**
	 * 创建实例
	 * 
	 * @param configKey
	 *            配置项，提供命名空间
	 * @param typeName
	 *            类名
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BOFactoryException
	 */
	protected T create(String configKey, String typeName) {
		String configValue = MyConfiguration.getConfigValue(configKey, "").toLowerCase();
		if (configValue == null || configValue.isEmpty()) {
			// 没有配置，则使用默认
			Logger.log(MessageLevel.WARN, MSG_CONFIG_NOT_CONFIGURATION_USING_DEFALUT, configKey, typeName);
			return this.createDefault(typeName);
		}
		// 使用配置的实例
		try {
			T instance = this.newInstance(configValue, typeName);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException(I18N.prop("msg_bobas_configurable_factory_create_instance_faild", typeName), e);
		}
	}

	/**
	 * 默认实例，没有配置时使用
	 * 
	 * @param typeName
	 * @return
	 */
	protected abstract T createDefault(String typeName);
}
