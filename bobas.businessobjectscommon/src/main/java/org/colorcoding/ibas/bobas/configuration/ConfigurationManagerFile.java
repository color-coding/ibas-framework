package org.colorcoding.ibas.bobas.configuration;

import org.colorcoding.ibas.bobas.exception.BasRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.IKeyText;

/**
 * 配置项操作类-XML
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "configuration", namespace = MyConfiguration.NAMESPACE_BOBAS_CONFIGURATION)
@XmlRootElement(name = "configuration", namespace = MyConfiguration.NAMESPACE_BOBAS_CONFIGURATION)
public class ConfigurationManagerFile extends ConfigurationManager {
	public ConfigurationManagerFile() {
	}

	public ConfigurationManagerFile(String configFile) {
		this();
		this.setConfigFile(configFile);
	}

	public final String getConfigFile() {
		if (this.getConfigSign() == null || this.getConfigSign().isEmpty()) {
			this.setConfigSign("app.xml");
		}
		return this.getConfigSign();
	}

	public final void setConfigFile(String configFile) {
		this.setConfigSign(configFile);
	}

	@XmlElementWrapper(name = "appSettings")
	@XmlElement(name = "add", type = ConfigurationElement.class)
	private ConfigurationElement[] getConfigurationElements() {
		Collection<IKeyText> keyValues = this.getElements();
		ConfigurationElement[] elements = new ConfigurationElement[keyValues.size()];
		int index = 0;
		for (IKeyText item : keyValues) {
			elements[index] = new ConfigurationElement(item.getKey(), item.getText());
			index++;
		}
		return elements;
	}

	@SuppressWarnings("unused")
	private void setConfigurationElements(ConfigurationElement[] value) {
		if (value == null) {
			return;
		}
		for (ConfigurationElement item : value) {
			this.addConfigValue(item.getKey(), item.getValue());
		}
	}

	public synchronized void save() throws Exception {
		if (this.getConfigFile() == null || this.getConfigFile().isEmpty())
			return;
		File file = new File(this.getConfigFile());
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		JAXBContext context = JAXBContext.newInstance(ConfigurationManagerFile.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, file);
	}

	public synchronized void update() {
		if (this.getConfigFile() == null || this.getConfigFile().isEmpty()) {
			return;
		}
		File file = new File(this.getConfigFile());
		if (!file.exists()) {
			return;
		}
		try {
			try (InputStream stream = new FileInputStream(file)) {
				JAXBContext context = JAXBContext.newInstance(ConfigurationManagerFile.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				XMLInputFactory inputFactory = XMLInputFactory.newFactory();
				inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
				inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
				XMLStreamReader reader = inputFactory.createXMLStreamReader(stream);
				try {
					ConfigurationManagerFile tmpManager = (ConfigurationManagerFile) unmarshaller.unmarshal(reader);
					this.replaceConfigValues(tmpManager.getElements());
				} finally {
					reader.close();
				}
			}
		} catch (Exception e) {
			throw new BasRuntimeException(e.getMessage(), e);
		}
	}

}
