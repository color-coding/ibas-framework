package org.colorcoding.ibas.bobas.jersey;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonConfig implements ContextResolver<MoxyJsonConfig> {

	private final MoxyJsonConfig config;

	public JsonConfig() {
		MoxyJsonConfig jsonConfig = new MoxyJsonConfig();
		jsonConfig.property(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		this.config = jsonConfig;
	}

	@Override
	public MoxyJsonConfig getContext(Class<?> objectType) {
		return config;
	}

}