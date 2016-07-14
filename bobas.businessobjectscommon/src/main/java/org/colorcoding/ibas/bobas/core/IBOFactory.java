package org.colorcoding.ibas.bobas.core;

import java.net.URL;

/**
 * 业务对象的工厂
 */
public interface IBOFactory {
	/**
	 * 加载包
	 * 
	 * @param path
	 *            路径
	 * @throws BOFactoryException
	 */
	void loadPackage(String path) throws BOFactoryException;

	/**
	 * 加载包
	 * 
	 * @param url
	 *            地址
	 * @throws BOFactoryException
	 */
	void loadPackage(URL url) throws BOFactoryException;

	/**
	 * 获取业务对象类型
	 * 
	 * @param boCode
	 *            业务对象编码
	 * 
	 * @return 业务对象的类型
	 * @throws BOFactoryException
	 */
	Class<?> getBOClass(String boCode) throws BOFactoryException;

	/**
	 * 创建业务对象实例
	 * 
	 * @param type
	 *            对象类型
	 * 
	 * @return 业务对象实例
	 * @throws BOFactoryException
	 */
	<P> P createInstance(Class<P> type) throws BOFactoryException;

	/**
	 * 获取类型
	 * 
	 * @param className
	 *            类型名称
	 * @return 类型
	 * @throws BOFactoryException
	 */
	Class<?> getClass(String className) throws BOFactoryException;

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
	 * 获取业务对象编码
	 * 
	 * @param type
	 *            类型
	 * @return 编码
	 * @throws BOFactoryException
	 */
	String getBOCode(Class<?> type) throws BOFactoryException;

	/**
	 * 判断是否派生自类型
	 * 
	 * @param child
	 *            类型
	 * @param parent
	 *            基础类型
	 * @return true；false
	 */
	boolean isDerivedFrom(Class<?> child, Class<?> parent);

}
