package org.colorcoding.bobas.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;

import org.colorcoding.bobas.MyConfiguration;
import org.colorcoding.bobas.i18n.i18n;
import org.colorcoding.bobas.mapping.BOCode;

public class BOFactory implements IBOFactory {
	volatile private static IBOFactory instance = null;

	public static IBOFactory create() {
		if (instance == null) {
			synchronized (BOFactory.class) {
				if (instance == null) {
					instance = new BOFactory();
				}
			}
		}
		return instance;
	}

	private BOFactory() {

	}

	public String getWorkFolder() {
		String workFoder = System.getProperty("user.dir");
		workFoder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_WORK_FOLDER, workFoder);
		return workFoder;
	}

	/**
	 * 判断是否派生自
	 * 
	 * @param child
	 *            派生类
	 * @param parent
	 *            基础类
	 */
	@Override
	public boolean isDerivedFrom(Class<?> child, Class<?> parent) {
		// 非法数据
		if (child == null || parent == null) {
			return false;
		}
		boolean inherited = false;
		// 本层是否继承
		inherited = child.isAssignableFrom(parent);
		if (inherited) {
			return inherited;
		}
		// 判断上层是否继承
		for (Class<?> item : child.getInterfaces()) {
			inherited = this.isDerivedFrom(item, parent);
			if (inherited) {
				return inherited;
			}
		}
		return false;
	}

	@Override
	public void loadPackage(String path) throws BOFactoryException {
		URL url = null;
		try {
			String fullPath = path;
			String fileSeparator = System.getProperty("file.separator");
			if (fullPath != null && (fullPath.indexOf(fileSeparator) < 0)) {
				// 不是完整路径(目录 分隔符 文件名称)
				fullPath = String.format("%s%s%s", getWorkFolder(), fileSeparator, fullPath);
			}
			url = new File(path).toURI().toURL();
		} catch (Exception e) {
			throw new BOFactoryException(e.getMessage(), e);
		}
		this.loadPackage(url);
	}

	@Override
	public void loadPackage(URL url) throws BOFactoryException {
		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[] { url });
			classLoader.close();
		} catch (Exception e) {
			throw new BOFactoryException(e.getMessage(), e);
		}
	}

	@Override
	public Object createBO(String boCode) throws BOFactoryException {
		return this.createInstance(this.getBOClass(boCode));
	}

	@Override
	public Object createInstance(String className) throws BOFactoryException {
		return this.createInstance(this.getClass(className));
	}

	@Override
	public Class<?> getBOClass(String boCode) throws BOFactoryException {
		// TODO Auto-generated method stub
		// 业务对象标记了BOCode注释
		return null;
	}

	@Override
	public String getBOCode(Class<?> type) throws BOFactoryException {
		try {
			Annotation annotation = type.getAnnotation(BOCode.class);
			if (annotation != null) {
				return ((BOCode) annotation).value();
			}
		} catch (Exception e) {
			throw e;
		}
		throw new ClassNotDefinedBOCode(i18n.prop("msg_bobas_not_found_bo_code", type.getName()));
	}

	@Override
	public <P> P createInstance(Class<P> type) throws BOFactoryException {
		try {
			if (type == null) {
				return null;
			}
			return type.newInstance();
		} catch (Exception e) {
			throw new BOFactoryException(e.getMessage(), e);
		}
	}

	@Override
	public Class<?> getClass(String className) throws BOFactoryException {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			throw new BOFactoryException(e.getMessage(), e);
		}
	}

	@Override
	public Class<?>[] getClasses(String packageName) throws BOFactoryException {
		// TODO Auto-generated method stub
		return null;
	}
}
