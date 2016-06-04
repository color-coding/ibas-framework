package org.colorcoding.bobas.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


/**
 * 配置项操作类-XML
 * 
 */

public class ConfigurationManager implements IConfigurationManager {
	/**
	 * 保存配置项
	 */
	@Override
	public void saveSettings(IConfigurationElements elements, String filePath) {
		try {
			if (filePath == null || filePath.equals(""))
				return;
			if (elements == null)
				return;
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			ConfigurationElements settings = (ConfigurationElements) elements;
			JAXBContext context = JAXBContext.newInstance(ConfigurationElements.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
			marshaller.marshal(settings, file);
		} catch (Exception e) {
			throw new RuntimeException(e);

		}
	}

	/**
	 * 在指定路径下读取配置项
	 */
	@Override
	public IConfigurationElements readSettings(String filePath) {
		if (filePath == null || filePath.equals(""))
			return null;
		File file = new File(filePath);
		if (!file.exists())
			return null;
		IConfigurationElements collection = null;
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			collection = readSettings(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}

		}
		return collection;
	}

	/**
	 * 读取配置流 反序列化成实体类
	 */
	@Override
	public IConfigurationElements readSettings(InputStream stream) {
		if (stream == null)
			return null;
		IConfigurationElements elements = null;
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationElements.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			elements = (ConfigurationElements) unmarshaller.unmarshal(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return elements;

	}
}
