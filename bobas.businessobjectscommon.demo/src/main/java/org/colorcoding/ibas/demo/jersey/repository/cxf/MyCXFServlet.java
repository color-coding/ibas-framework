package org.colorcoding.ibas.demo.jersey.repository.cxf;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.colorcoding.ibas.bobas.cxf.CXFServlet;

public class MyCXFServlet extends CXFServlet {

	private static final long serialVersionUID = 450755718934409533L;

	@Override
	public void loadBus(ServletConfig servletConfig) {
		super.loadBus(servletConfig);
		Bus bus = this.getBus();
		BusFactory.setDefaultBus(bus);
		Endpoint.publish("/oooo", new ServiceSoap());
	}
}