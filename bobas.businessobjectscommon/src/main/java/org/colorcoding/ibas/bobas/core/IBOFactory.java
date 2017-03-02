package org.colorcoding.ibas.bobas.core;

import java.io.IOException;
import java.net.URL;

/**
 * 业务对象的工厂
 */
public interface IBOFactory {

	/**
	 * 获取-扫描的命名空间
	 * 
	 * @return 分号分隔
	 */
	String getScanNamespaces();

	/**
	 * 设置-扫描的命名空间
	 */
	void setScanNamespaces(String value);

	/**
	 * 加载包
	 * 
	 * @param path
	 *            路径
	 * @throws IOException
	 */
	void loadPackage(String path) throws IOException;

	/**
	 * 加载包
	 * 
	 * @param url
	 *            地址
	 * @throws IOException
	 */
	void loadPackage(URL url) throws IOException;

	/**
	 * 获取业务对象类型
	 * 
	 * @param boCode
	 *            业务对象编码
	 * 
	 * @return 业务对象的类型
	 * @throws BOFactoryException
	 */
	Class<?> getBOClass(String boCode);

	/**
	 * 创建业务对象实例
	 * 
	 * @param type
	 *            对象类型
	 * 
	 * @return 业务对象实例
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	<P> P createInstance(Class<P> type) throws InstantiationException, IllegalAccessException;

	/**
	 * 创建业务对象实例
	 * 
	 * @param className
	 *            对象类型名称
	 * 
	 * @return 业务对象实例
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	Object createInstance(String className)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	/**
	 * 获取类型
	 * 
	 * @param className
	 *            类型名称
	 * @return 类型
	 * @throws ClassNotFoundException
	 */
	Class<?> getClass(String className) throws ClassNotFoundException;

	/**
	 * 获取命名空间下所有类型
	 * 
	 * @param packageName
	 *            命名空间
	 * @return 类型数组
	 * @throws BOFactoryException
	 */
	Class<?>[] getClasses(String packageName);

	/**
	 * 获取已加载的所有类型
	 * 
	 * @param packageName
	 *            命名空间，null为全部
	 * @return 类型数组
	 */
	Class<?>[] getKnownClasses(String packageName);

	/**
	 * 获取业务对象编码
	 * 
	 * @param type
	 *            类型
	 * @return 编码
	 * @throws BOFactoryException
	 */
	String getBOCode(Class<?> type);

	/**
	 * 注册业务对象编码
	 * 
	 * @param types
	 *            对象类型
	 * @return 成功注册业务对象编码个数
	 * @throws BOFactoryException
	 */
	int registerBOCode(Class<?>[] types);

}
