package org.colorcoding.ibas.bobas.jersey;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

/**
 * Provides MOXy JAXB contexts for JSON entities.
 *
 * <p>Each entity type gets its own context so newly added REST types do not
 * need to be maintained in a separate, hard-coded type list.</p>
 */
public class MoxyJsonContextResolver implements ContextResolver<JAXBContext> {

	private final Map<Class<?>, JAXBContext> contexts = new ConcurrentHashMap<>();

	@Override
	public JAXBContext getContext(Class<?> type) {
		Objects.requireNonNull(type, "type");
		return this.contexts.computeIfAbsent(type, this::createContext);
	}

	private JAXBContext createContext(Class<?> type) {
		try {
			return JAXBContextFactory.createContext(new Class<?>[] { type }, null);
		} catch (JAXBException e) {
			throw new IllegalStateException(
					String.format("Unable to create MOXy JAXB context for [%s].", type.getName()), e);
		}
	}
}
