package org.colorcoding.ibas.bobas.diagnosis;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.colorcoding.ibas.bobas.common.OperationInformation;
import org.colorcoding.ibas.bobas.common.OperationMessage;
import org.colorcoding.ibas.bobas.common.OperationResult;

/**
 * 序列化解释器
 */
@Provider
@Produces({ "application/json" })
public class Resolver implements ContextResolver<JAXBContext> {

	private static JAXBContext jaxbContext = null;

	public JAXBContext getContext(Class<?> type) {
		try {
			if (jaxbContext == null) {
				jaxbContext = JAXBContext.newInstance(OperationMessage.class, OperationResult.class,
						OperationInformation.class);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return jaxbContext;
	}

}
