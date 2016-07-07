package org.colorcoding.ibas.bobas.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

/**
 * 配置项操作类-XML
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "configuration", namespace = MyConsts.NAMESPACE_BOBAS_CONFIGURATION)
@XmlRootElement(name = "configuration", namespace = MyConsts.NAMESPACE_BOBAS_CONFIGURATION)
public class ConfigurationManager implements IConfigurationManager {

	public static IConfigurationManager create(String filePath) throws FileNotFoundException, JAXBException {
		if (filePath == null || filePath.equals(""))
			return null;
		File file = new File(filePath);
		if (!file.exists())
			return null;
		InputStream stream = new FileInputStream(file);
		JAXBContext context = JAXBContext.newInstance(ConfigurationManager.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (IConfigurationManager) unmarshaller.unmarshal(stream);
	}

	private HashMap<String, IConfigurationElement> elements;

	/**
	 * 配置项
	 */
	protected HashMap<String, IConfigurationElement> getElements() {
		if (elements == null) {
			elements = new HashMap<String, IConfigurationElement>();
		}
		return elements;
	}

	@XmlElementWrapper(name = "appSettings")
	@XmlElement(name = "add", type = ConfigurationElement.class)
	private ConfigurationElement[] getConfigurationElements() {
		return this.getElements().values().toArray(new ConfigurationElement[] {});
	}

	@SuppressWarnings("unused")
	private void setConfigurationElements(ConfigurationElement[] value) {
		if (value == null) {
			return;
		}
		for (ConfigurationElement item : value) {
			this.addSetting(item.getKey(), item.getValue());
		}
	}

	@Override
	public void saveSettings(String filePath) throws JAXBException, IOException {
		if (filePath == null || filePath.equals(""))
			return;
		if (elements == null)
			return;
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		JAXBContext context = JAXBContext.newInstance(ConfigurationManager.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, file);

	}

	@Override
	public String getValue(String key) {
		if (this.getElements().containsKey(key)) {
			return this.getElements().get(key).getValue();
		}
		return null;
	}

	@Override
	public void addSetting(String key, String value) {
		IConfigurationElement element = null;
		if (this.getElements().containsKey(key)) {
			element = this.getElements().get(key);
		}
		if (element == null) {
			element = new ConfigurationElement();
			element.setKey(key);
			this.getElements().put(element.getKey(), element);
		}
		element.setValue(value);
	}

}
