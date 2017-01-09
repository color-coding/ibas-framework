package org.colorcoding.ibas.bobas.cxf;

/**
 * 服务提供者
 * 
 * @author Niuren.Zhu
 *
 */
public interface ServerProvider {
	/**
	 * 获取服务
	 * 
	 * @param pars
	 * @return
	 */
	Server[] getServers(Object... pars);
}
