package org.colorcoding.ibas.bobas.cxf;

/**
 * 服务提供者
 * 
 * @author Niuren.Zhu
 *
 */
public interface WebServiceProvider {
	/**
	 * 获取服务
	 * 
	 * @param pars
	 * @return
	 */
	WebService[] getWebServices(Object... pars);
}
