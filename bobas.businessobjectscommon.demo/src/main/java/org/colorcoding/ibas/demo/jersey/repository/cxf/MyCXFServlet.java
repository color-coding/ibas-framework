package org.colorcoding.ibas.demo.jersey.repository.cxf;

import javax.servlet.ServletConfig;

import org.colorcoding.ibas.bobas.cxf.CXFServlet;

public class MyCXFServlet extends CXFServlet {

	private static final long serialVersionUID = 450755718934409533L;

	@Override
	public void loadBus(ServletConfig servletConfig) {
		super.loadBus(servletConfig);
	}
}