package org.colorcoding.ibas.bobas.test.configuration;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.configuration.ConfigurationManagerFile;

import junit.framework.TestCase;

public class TestConfiguration extends TestCase {

	public void testCreate() throws Exception {
		ConfigurationManagerFile manager = new ConfigurationManagerFile();
		manager.addConfigValue("DbType", "MSSQL");
		manager.addConfigValue("DbServer", "localhost");
		manager.addConfigValue("DbName", "ibas_demo");
		manager.addConfigValue("DbUserID", "sa");
		manager.addConfigValue("DbUserPassword", "1q2w3e");

		manager.setConfigFile(String.format("%s%s~app.xml", Configuration.getStartupFolder(), File.separator));
		System.out.println(String.format("configuration save to [%s].", manager.getConfigFile()));
		manager.save();

		ConfigurationManagerFile newManager = new ConfigurationManagerFile();
		newManager.setConfigFile(manager.getConfigFile());
		newManager.update();
		assertEquals("config value not equal.", manager.getConfigValue("DbType"), newManager.getConfigValue("DbType"));
		assertEquals("config value not equal.", manager.getConfigValue("DbServer"),
				newManager.getConfigValue("DbServer"));
		assertEquals("config value not equal.", manager.getConfigValue("DbName"), newManager.getConfigValue("DbName"));
		assertEquals("config value not equal.", manager.getConfigValue("DbUserID"),
				newManager.getConfigValue("DbUserID"));
		assertEquals("config value not equal.", manager.getConfigValue("DbUserPassword"),
				newManager.getConfigValue("DbUserPassword"));

	}

	public void testGetWorkFolder() throws Exception {
		File folder = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath());
		System.out.println(folder.isFile());
		System.out.println(folder.isDirectory());
		System.out.println(MyConfiguration.getStartupFolder());
		System.out.println(MyConfiguration.getWorkFolder());
		System.out.println(MyConfiguration.getResource("i18n"));
		// System.out.println(MyConfiguration.getResource("app.xml"));//null
		MyConfiguration.update();
	}

	public void testConfigurableFactory() throws ClassNotFoundException {
		// DbAdapter
		TestFactory.create().classOf("DbAdapter.");// 不需要额外处理时，最后位为“.”
		// org.colorcoding.ibas.bobas.test.configuration.DbAdapter
		TestFactory.create().classOf("DbAdapter");
		// db.mssql.DbAdapter
		TestFactory.create().classOf("db.mssql", "DbAdapter");
		// org.colorcoding.ibas.bobas.test.configuration.mssql.DbAdapter
		TestFactory.create().classOf("mssql", "DbAdapter");

	}

}

class TestFactory extends ConfigurableFactory<Object> {

	private TestFactory() {
	}

	public Class<Object> classOf(String... names) throws ClassNotFoundException {
		return super.classOf(names);
	}

	private volatile static TestFactory factory;

	public synchronized static TestFactory create() {
		if (factory == null) {
			synchronized (TestFactory.class) {
				if (factory == null) {
					factory = new TestFactory();
				}
			}
		}
		return factory;
	}

	@Override
	protected Object createDefault(String typeName) {
		return null;
	}

}