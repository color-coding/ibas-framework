package org.colorcoding.ibas.bobas.jersey;

import java.io.ByteArrayOutputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

import junit.framework.TestCase;

public class TestMoxyJsonProviders extends TestCase {

	public void testContextIsMoxyAndCachedByEntityType() {
		MoxyJsonContextResolver resolver = new MoxyJsonContextResolver();

		JAXBContext criteriaContext = resolver.getContext(Criteria.class);
		assertSame(criteriaContext, resolver.getContext(Criteria.class));
		assertNotSame(criteriaContext, resolver.getContext(OperationResult.class));
		assertTrue(criteriaContext.getClass().getName().startsWith("org.eclipse.persistence."));
	}

	public void testRestJsonConfigurationMatchesSerializerJson() {
		MoxyJsonConfig config = new JsonConfig().getContext(Criteria.class);

		assertEquals(Boolean.FALSE,
				config.getMarshallerProperties().get(JAXBContextProperties.JSON_INCLUDE_ROOT));
		assertEquals(Boolean.TRUE,
				config.getMarshallerProperties().get(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME));
		assertEquals(Boolean.TRUE,
				config.getMarshallerProperties().get(JAXBContextProperties.JSON_TYPE_COMPATIBILITY));
		assertEquals(Boolean.FALSE,
				config.getUnmarshallerProperties().get(JAXBContextProperties.JSON_INCLUDE_ROOT));
		assertEquals(Boolean.TRUE,
				config.getUnmarshallerProperties().get(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME));
		assertEquals(Boolean.TRUE,
				config.getUnmarshallerProperties().get(JAXBContextProperties.JSON_TYPE_COMPATIBILITY));
	}

	public void testOperationResultCanMarshalRuntimeResultType() throws Exception {
		MoxyJsonContextResolver resolver = new MoxyJsonContextResolver();
		OperationResult<DataTable> result = new OperationResult<>();
		result.addResultObjects(new DataTable());
		Marshaller marshaller = resolver.getContext(OperationResult.class).createMarshaller();
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
		marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		marshaller.setProperty(MarshallerProperties.JSON_TYPE_COMPATIBILITY, true);

		marshaller.marshal(result, new ByteArrayOutputStream());
	}
}
