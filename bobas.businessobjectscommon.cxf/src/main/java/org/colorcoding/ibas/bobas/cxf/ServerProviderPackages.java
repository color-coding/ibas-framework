package org.colorcoding.ibas.bobas.cxf;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 服务提供者，命令空间方式
 * 
 * @author Niuren.Zhu
 *
 */
public class ServerProviderPackages implements ServerProvider {

	@Override
	public Server[] getServers(Object... pars) {
		if (pars == null) {
			return null;
		}
		String packages = String.valueOf(pars[0]);
		ArrayList<Server> servers = new ArrayList<>();
		for (Class<?> item : BOFactory.create().getClasses(packages)) {
			try {
				Object tmp = item.newInstance();

				servers.add(new Server("", tmp));
			} catch (InstantiationException | IllegalAccessException e) {
				RuntimeLog.log(e);
			}
		}
		return servers.toArray(new Server[] {});
	}

}
