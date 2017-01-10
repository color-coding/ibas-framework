package org.colorcoding.ibas.demo.jersey.repository.cxf;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class ServiceBasic {
	@WebMethod
	public String sayHello() {
		return "Hello CXF";
	}

}
