package org.colorcoding.ibas.bobas.bo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfoManager;
import org.colorcoding.ibas.bobas.core.PropertyInfoRegisterListener;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 业务对象工厂
 */
public final class BOFactory {

	private BOFactory() {
	}

	static {
		// 初始化变量
		BO_PROPERTIES = new ConcurrentHashMap<>(256);
		MAP_BO2CODE = new ConcurrentHashMap<Class<?>, String>(256);
		MAP_CODE2BO = new ConcurrentHashMap<String, Class<?>>(256);
		// 注册属性注册监听
		PropertyInfoManager.registerListener(new PropertyInfoRegisterListener() {

			@Override
			public void onRegistered(Class<?> clazz) {
				if (!MAP_BO2CODE.containsKey(clazz)) {
					register(clazz);
				}
			}
		});
	}

	/**
	 * 业务对象的编码字典
	 */
	private volatile static Map<Class<?>, String> MAP_BO2CODE;
	/**
	 * 业务编码的对象字典
	 */
	private volatile static Map<String, Class<?>> MAP_CODE2BO;

	/**
	 * 注册对象
	 * 
	 * @param type 对象类型
	 * @return
	 */
	public static boolean register(Class<?> type) {
		String boCode = codeOf(type);
		if (!Strings.isNullOrEmpty(boCode)) {
			MAP_BO2CODE.put(type, boCode);
			MAP_CODE2BO.put(boCode, type);
			return true;
		}
		return false;
	}

	/**
	 * 注册对象
	 * 
	 * @param boCode 对象编码
	 * @param type   对象类型
	 * @return
	 */
	public static boolean register(String boCode, Class<?> type) {
		if (type == null) {
			return false;
		}
		MAP_BO2CODE.put(type, boCode);
		MAP_CODE2BO.put(boCode, type);
		return true;
	}

	/**
	 * 获取对象编码
	 * 
	 * @param type 对象类型
	 * @return
	 */
	public static String codeOf(Class<?> type) {
		if (type == null) {
			return Strings.VALUE_EMPTY;
		}
		BusinessObjectUnit businessObjectUnit = type.getAnnotation(BusinessObjectUnit.class);
		if (businessObjectUnit != null) {
			return MyConfiguration.applyVariables(businessObjectUnit.code());
		}
		return Strings.VALUE_EMPTY;
	}

	public static Class<?> classOf(String boCode) throws ClassNotFoundException {
		Class<?> clazz = MAP_CODE2BO.get(boCode);
		if (clazz != null) {
			return clazz;
		}
		throw new ClassNotFoundException(I18N.prop("msg_bobas_not_found_bo_class", boCode));
	}

	/**
	 * 创建对象实例
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> type) {
		try {
			return (T) type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<Class<?>, List<IPropertyInfo<?>>> BO_PROPERTIES;

	/**
	 * 获取对象的所有属性
	 * 
	 * @param type 对象
	 * @return
	 */
	public static List<IPropertyInfo<?>> propertyInfos(Class<?> type) {
		if (BO_PROPERTIES.containsKey(type)) {
			return BO_PROPERTIES.get(type);
		}
		if (type.isInterface()) {
			for (Class<?> item : BO_PROPERTIES.keySet()) {
				if (type.isAssignableFrom(item)) {
					return BO_PROPERTIES.get(item);
				}
			}
		} else {
			// 从管理员处获取
			Collection<IPropertyInfo<?>> collection = PropertyInfoManager.createFetcher().apply(type);
			if (collection != null && !collection.isEmpty()) {
				BO_PROPERTIES.put(type, new ArrayList<IPropertyInfo<?>>(collection));
				return propertyInfos(type);
			}
			// 获取父项的
			List<IPropertyInfo<?>> propertyInfos = null;
			Class<?> superClass = type.getSuperclass();
			while (superClass != null && superClass != BusinessObject.class
					&& !Modifier.isAbstract(type.getModifiers())) {
				propertyInfos = propertyInfos(superClass);
				if (propertyInfos != null && !propertyInfos.isEmpty()) {
					BO_PROPERTIES.put(type, new ArrayList<IPropertyInfo<?>>(propertyInfos));
					return propertyInfos(type);
				}
				superClass = superClass.getSuperclass();
			}
			// 获取实例的
			if (Modifier.isPublic(type.getModifiers()) && !Modifier.isAbstract(type.getModifiers())) {
				Object data = newInstance(type);
				if (data instanceof FieldedObject) {
					BO_PROPERTIES.put(type, new ArrayList<IPropertyInfo<?>>(((FieldedObject) data).properties()));
					return propertyInfos(type);
				}
			}
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", type.toString()));
	}

	private static ClassLoader classLoader = null;

	/**
	 * 当前类加载器
	 * 
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		if (classLoader == null) {
			return Thread.currentThread().getContextClassLoader();
		}
		return classLoader;
	}

	public static void setClassLoader(ClassLoader loader) {
		classLoader = loader;
	}

	/**
	 * 获取类
	 * 
	 * @param packageName 类名
	 * @return
	 */
	public static Class<?> loadClass(String classsName) {
		try {
			return getClassLoader().loadClass(classsName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取类（不触发类加载）
	 * 
	 * @param packageName 类的命名空间
	 * @return
	 */
	public static Class<?>[] loadClasses(String packageName) {
		int idx;
		URL url;
		String name;
		JarEntry entry;
		Enumeration<JarEntry> entries;
		ClassLoader classLoader = getClassLoader();
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		String packageDirName = packageName.replace('.', '/');
		try {
			Enumeration<URL> dirs = classLoader.getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				url = dirs.nextElement();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(url.getProtocol())) {
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findClassesInPackageByFile(packageName, URLDecoder.decode(url.getFile(), "UTF-8"), classes);
				} else if ("jar".equals(url.getProtocol())) {
					// 如果是jar包文件
					try (JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile()) {
						entries = jar.entries();
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							entry = entries.nextElement();
							name = entry.getName();
							// 如果是以/开头的，获取后面的字符串
							if (name.charAt(0) == '/') {
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if (idx != -1) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										try {
											// 添加到classes
											classes.add(classLoader.loadClass(packageName + '.'
											// 去掉后面的".class" 获取真正的类名
													+ name.substring(packageName.length() + 1, name.length() - 6)));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						Logger.log(e);
					}
				}
			}
		} catch (IOException e) {
			Logger.log(e);
		}
		return classes.toArray(new Class<?>[] {});
	}

	private static void findClassesInPackageByFile(String packageName, String packagePath, Set<Class<?>> classes) {
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		if (dirfiles != null) {
			ClassLoader classLoader = getClassLoader();
			for (File file : dirfiles) {
				if (file.isDirectory()) {
					findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
				} else {
					try {
						// 这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
						classes.add(classLoader.loadClass(packageName + '.'
						// 如果是java类文件 去掉后面的.class 只留下类名
								+ file.getName().substring(0, file.getName().length() - 6)));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
