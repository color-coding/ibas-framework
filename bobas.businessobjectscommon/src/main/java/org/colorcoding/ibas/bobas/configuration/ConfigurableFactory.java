package org.colorcoding.ibas.bobas.configuration;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.util.StringBuilder;

/**
 * 可配置工厂
 * 
 */
public abstract class ConfigurableFactory {

	public ConfigurableFactory() {

	}

	/**
	 * 获取对象实例 实例路径：工厂命名空间 + 实现的类型 + 类的名称， 例如：
	 * [org.colorcoding.ibas.bobas.db].[mysql].[DbAdapter]
	 * 
	 * @param factory
	 *            工厂类型
	 * @param type
	 *            实现类型标记，此参数存在“.”则不加工厂命名空间前缀
	 * @param name
	 *            实例名称
	 * @return 工厂实现的类型实例
	 * @throws BOFactoryException
	 */
	protected static Class<?> getInstance(Class<?> factory, String type, String name) throws BOFactoryException {
		StringBuilder className = new StringBuilder();
		if (type != null && type.indexOf(".") > 0) {
			// 提供路径
			className.append(type);
		} else {
			// 基础命名空间
			className.append(factory.getPackage().getName());
			// 类型实现命名空间
			if (type != null && !type.isEmpty()) {
				className.append(".");
				className.append(type);
			}
		}
		// 类名称
		if (name != null && !name.isEmpty()) {
			className.append(".");
			className.append(name);
		}

		// 获取类类型
		Class<?> instanceClass = BOFactory.create().getClass(className.toString());
		return instanceClass;
	}

}
