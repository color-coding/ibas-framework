package org.colorcoding.ibas.bobas.cxf;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 不使用spring的CXFServlet
 * 
 * @author Niuren.Zhu
 *
 */
public class CXFServlet extends CXFNonSpringServlet {

	private static final long serialVersionUID = 450755718934409533L;

	public final static String CONFIG_SERVER_PROVIDER = "ibas.config.server.provider.";
	public final static String MSG_SERVER_PROVIDER = "servlet: using provider [%s], by parameters [%s].";
	public final static String MSG_REGISTER_SERVER = "servlet: register server [%s] address [%s].";

	@Override
	public void loadBus(ServletConfig servletConfig) {
		super.loadBus(servletConfig);
		this.registerServices();
	}

	/**
	 * 注册服务
	 */
	protected void registerServices() {
		// 遍历配置参数
		Enumeration<String> enums = this.getInitParameterNames();
		while (enums.hasMoreElements()) {
			String key = enums.nextElement();
			if (!key.startsWith(CONFIG_SERVER_PROVIDER)) {
				continue;
			}
			// 找到服务提供的相关参数
			String name = key.substring(CONFIG_SERVER_PROVIDER.length());
			WebServiceProvider serverProvider = CXFFactory.create().createProvider(name);
			if (serverProvider == null) {
				continue;
			}
			// 调用服务提供者
			String value = this.getInitParameter(key);
			RuntimeLog.log(MessageLevel.DEBUG, String.format(MSG_SERVER_PROVIDER, name, value));
			// 注册服务
			WebService[] servers = serverProvider.getWebServices(value);
			if (servers != null) {
				BusFactory.setDefaultBus(this.getBus());
				for (WebService server : servers) {
					RuntimeLog.log(MessageLevel.DEBUG,
							String.format(MSG_REGISTER_SERVER, server.getName(), server.getAddress()));
					Endpoint.publish(server.getAddress(), server.getImplementor());
				}
			}
		}
	}
}
