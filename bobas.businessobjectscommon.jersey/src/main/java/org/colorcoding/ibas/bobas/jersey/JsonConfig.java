package org.colorcoding.ibas.bobas.jersey;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonConfig implements ContextResolver<MoxyJsonConfig> {

	private final MoxyJsonConfig config;

	public JsonConfig() {
		MoxyJsonConfig jsonConfig = new MoxyJsonConfig();
		jsonConfig.property(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
		jsonConfig.property(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		jsonConfig.property(JAXBContextProperties.JSON_TYPE_COMPATIBILITY, true);
		this.config = jsonConfig;
	}

	@Override
	public MoxyJsonConfig getContext(Class<?> objectType) {
		return config;
	}

}
