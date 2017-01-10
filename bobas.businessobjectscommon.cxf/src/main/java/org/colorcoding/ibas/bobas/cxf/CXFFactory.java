package org.colorcoding.ibas.bobas.cxf;

/**
 * 服务提供者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class CXFFactory {

	private static volatile CXFFactory instance;

	public static CXFFactory create() {
		if (instance == null) {
			synchronized (CXFFactory.class) {
				if (instance == null) {
					instance = new CXFFactory();
				}
			}
		}
		return instance;
	}

	public WebServiceProvider createProvider(String provider) {
		if ("packages".equals(provider)) {
			return new WebServiceProviderPackages();
		}
		return null;
	}
}
