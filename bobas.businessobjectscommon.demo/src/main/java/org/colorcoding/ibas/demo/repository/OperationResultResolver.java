package org.colorcoding.ibas.demo.repository;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.colorcoding.ibas.bobas.bo.UserFieldProxy;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.User;

@Provider
@Produces({ "application/xml", "application/json" })
public class OperationResultResolver implements ContextResolver<JAXBContext> {

	private static JAXBContext jaxbContext = null;

	@Override
	public JAXBContext getContext(Class<?> type) {
		try {
			System.out.println(type.getName());
			if (type == OperationResult.class) {
				if (jaxbContext == null) {
					jaxbContext = JAXBContext.newInstance(Criteria.class, SalesOrder.class, User.class,
							UserFieldProxy.class, OperationResult.class);
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return jaxbContext;
	}

}
