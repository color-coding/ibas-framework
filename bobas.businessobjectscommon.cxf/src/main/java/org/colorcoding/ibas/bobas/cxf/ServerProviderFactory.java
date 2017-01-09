package org.colorcoding.ibas.bobas.cxf;

/**
 * 服务提供者工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class ServerProviderFactory {

	private static volatile ServerProviderFactory instance;

	public static ServerProviderFactory create() {
		if (instance == null) {
			synchronized (ServerProviderFactory.class) {
				if (instance == null) {
					instance = new ServerProviderFactory();
				}
			}
		}
		return instance;
	}

	public ServerProvider createProvider(String provider) {
		if ("packages".equals(provider)) {
			return new ServerProviderPackages();
		}
		return null;
	}
}
