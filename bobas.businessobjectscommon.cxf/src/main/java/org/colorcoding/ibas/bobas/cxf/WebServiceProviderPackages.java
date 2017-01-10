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
public class WebServiceProviderPackages implements WebServiceProvider {

	@Override
	public WebService[] getWebServices(Object... pars) {
		if (pars == null) {
			return null;
		}
		String packages = String.valueOf(pars[0]);
		ArrayList<WebService> servers = new ArrayList<>();
		for (Class<?> item : BOFactory.create().getClasses(packages)) {
			javax.jws.WebService annotation = item.getDeclaredAnnotation(javax.jws.WebService.class);
			if (annotation == null) {
				continue;
			}
			try {
				WebService service = new WebService(item.newInstance());
				if (annotation.name() != null && !annotation.name().isEmpty()) {
					service.setName(annotation.name());
				} else {
					service.setName(item.getName());
				}
				WebServicePath servicePath = item.getDeclaredAnnotation(WebServicePath.class);
				if (servicePath != null && servicePath.value() != null) {
					service.setAddress(servicePath.value());
				}
				servers.add(service);
			} catch (InstantiationException | IllegalAccessException e) {
				RuntimeLog.log(e);
			}
		}
		return servers.toArray(new WebService[] {});
	}

}
