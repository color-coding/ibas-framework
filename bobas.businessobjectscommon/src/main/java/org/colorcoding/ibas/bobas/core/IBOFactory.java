package org.colorcoding.ibas.bobas.core;

import java.io.IOException;
import java.net.URL;

/**
 * 业务对象的工厂
 */
public interface IBOFactory {

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
	 * 获取类型
	 * 
	 * @param className
	 *            类型名称
	 * @return
	 * @throws ClassNotFoundException
	 */
	Class<?> loadClass(String name) throws ClassNotFoundException;

	/**
	 * 获取类型
	 * 
	 * @param boCode
	 *            业务对象编码
	 * @return
	 * @throws ClassNotFoundException
	 */
	Class<?> getClass(String boCode) throws ClassNotFoundException;

	/**
	 * 加载类型
	 * 
	 * @param packageName
	 *            加载的命名空间
	 * @return
	 */
	Class<?>[] loadClasses(String packageName);

	/**
	 * 获取命名空间下的类型
	 * 
	 * @param packageName
	 *            获取的命名空间
	 * @return
	 */
	Class<?>[] getClasses(String packageName);

	/**
	 * 获取业务对象编码
	 * 
	 * @param type
	 *            类型
	 * @return
	 */
	String getCode(Class<?> type);

	/**
	 * 注册业务对象
	 * 
	 * @param boCode
	 *            编码
	 * @param type
	 *            类型
	 */
	void register(String boCode, Class<?> type);

	/**
	 * 注册业务对象
	 * 
	 * @param boCode
	 *            编码
	 * @param className
	 *            类名称
	 */
	void register(String boCode, String className);

	/**
	 * 注册业务对象
	 * 
	 * @param type
	 * @return 是否成功
	 */
	boolean register(Class<?> type);

}
