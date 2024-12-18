package org.colorcoding.ibas.bobas.bo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;

/**
 * 业务对象工厂
 */
public class BOFactory {

	private BOFactory() {
	}

	/**
	 * 业务对象的编码字典
	 */
	private volatile static Map<Class<?>, String> MAP_BO2CODE = new HashMap<Class<?>, String>(256);
	/**
	 * 业务编码的对象字典
	 */
	private volatile static Map<String, Class<?>> MAP_CODE2BO = new HashMap<String, Class<?>>(256);

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

	private static Map<Class<?>, List<IPropertyInfo<?>>> boProperties = new HashMap<>();

	/**
	 * 获取对象的所有属性
	 * 
	 * @param type 对象
	 * @return
	 */
	public static List<IPropertyInfo<?>> propertyInfos(Class<?> type) {
		if (boProperties.containsKey(type)) {
			return boProperties.get(type);
		}
		Object data = newInstance(type);
		if (data instanceof FieldedObject) {
			boProperties.put(type, ((FieldedObject) data).properties());
			return propertyInfos(type);
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", type.toString()));
	}

	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static Class<?>[] loadClasses(String packageName) {
		// 第一个class类的集合
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			ClassLoader classLoader = getClassLoader();
			dirs = classLoader.getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findClassesInPackageByFile(packageName, filePath, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if (idx != -1) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											// 添加到classes
											classes.add(classLoader.loadClass(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											// Logger.log(MessageLevel.DEBUG, e);
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
		// 获取此包的目录 建立一个File
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
			// 循环所有文件
			for (File file : dirfiles) {
				// 如果是目录 则继续扫描
				if (file.isDirectory()) {
					findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
				} else {
					// 如果是java类文件 去掉后面的.class 只留下类名
					String className = file.getName().substring(0, file.getName().length() - 6);
					try {
						// 添加到集合中去
						// 这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
						classes.add(classLoader.loadClass(packageName + '.' + className));
					} catch (ClassNotFoundException e) {
						// Logger.log(MessageLevel.DEBUG, e);
					}
				}
			}
		}
	}

}
