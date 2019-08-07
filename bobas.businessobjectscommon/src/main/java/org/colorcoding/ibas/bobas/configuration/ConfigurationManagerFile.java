package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
		return this.getElements().toArray(new ConfigurationElement[] {});
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

	@Override
	public synchronized void save() {
		try {
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void update() {
		if (this.getConfigFile() == null || this.getConfigFile().isEmpty()) {
			return;
		}
		File file = new File(this.getConfigFile());
		if (!file.exists()) {
			return;
		}
		try (InputStream stream = new FileInputStream(file)) {
			JAXBContext context = JAXBContext.newInstance(ConfigurationManagerFile.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			ConfigurationManagerFile tmpManager = (ConfigurationManagerFile) unmarshaller.unmarshal(stream);
			for (IConfigurationElement item : tmpManager.getElements()) {
				this.addConfigValue(item.getKey(), item.getValue());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
