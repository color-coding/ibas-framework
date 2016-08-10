package org.colorcoding.ibas.bobas.test.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.configuration.ConfigurationManager;
import org.colorcoding.ibas.bobas.configuration.IConfigurationManager;

import junit.framework.TestCase;

public class testConfiguration extends TestCase {

	public void testCreate() throws JAXBException, IOException {
		IConfigurationManager manager = new ConfigurationManager();
		manager.addSetting("DbType", "MSSQL");
		manager.addSetting("DbServer", "localhost");
		manager.addSetting("DbName", "ibas_demo");
		manager.addSetting("DbUserID", "sa");
		manager.addSetting("DbUserPassword", "1q2w3e");

		String filePath = String.format("%s%s~app.xml", Configuration.getStartupFolder(), File.separator);
		System.out.println(String.format("configuration save to [%s].", filePath));
		manager.saveSettings(filePath);

		IConfigurationManager manager1 = ConfigurationManager.create(filePath);
		assertEquals("config value not equal.", manager.getValue("DbType"), manager1.getValue("DbType"));
		assertEquals("config value not equal.", manager.getValue("DbServer"), manager1.getValue("DbServer"));
		assertEquals("config value not equal.", manager.getValue("DbName"), manager1.getValue("DbName"));
		assertEquals("config value not equal.", manager.getValue("DbUserID"), manager1.getValue("DbUserID"));
		assertEquals("config value not equal.", manager.getValue("DbUserPassword"),
				manager1.getValue("DbUserPassword"));

	}

	public void testGetWorkFolder() throws URISyntaxException {
		File folder = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath());
		System.out.println(folder.isFile());
		System.out.println(folder.isDirectory());
		System.out.println(MyConfiguration.getStartupFolder());
		System.out.println(MyConfiguration.getWorkFolder());
		System.out.println(MyConfiguration.getResource("i18n"));
		// System.out.println(MyConfiguration.getResource("app.xml"));//null
		MyConfiguration.update();
	}
}
