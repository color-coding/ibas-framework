package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务对象相关容器
 */
public class RepositoryFactory extends ConfigurableFactory {

	private RepositoryFactory() {

	}

	private volatile static RepositoryFactory instance = null;

	public synchronized static RepositoryFactory createFatory() {
		if (instance == null) {
			synchronized (RepositoryFactory.class) {
				if (instance == null) {
					instance = new RepositoryFactory();
				}
			}
		}
		return instance;
	}

	public IBOCacheContainer createCacheContainer(IBusinessObjectBase data) {
		return new BOCacheContainer(data);
	}
}
