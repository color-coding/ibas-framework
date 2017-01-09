package org.colorcoding.ibas.demo.jersey.repository.jersey;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

@Provider
public class MoxyJsonConfigResolver implements ContextResolver<MoxyJsonConfig> {

	private final MoxyJsonConfig config;

	public MoxyJsonConfigResolver() {
		MoxyJsonConfig jsonConfig = new MoxyJsonConfig();
		jsonConfig.property(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		this.config = jsonConfig;
	}

	@Override
	public MoxyJsonConfig getContext(Class<?> objectType) {
		return config;
	}

}