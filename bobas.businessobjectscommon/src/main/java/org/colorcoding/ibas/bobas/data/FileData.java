package org.colorcoding.ibas.bobas.data;

import java.io.InputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 文件数据
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "FileData", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "FileData", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class FileData {

	private String fileName;

	/**
	 * 文件名称
	 * 
	 * @return
	 */
	@XmlElement(name = "FileName")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private String location;

	/**
	 * 文件位置
	 * 
	 * @return
	 */
	@XmlElement(name = "Location")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private InputStream stream;

	/**
	 * 文件流
	 * 
	 * @return
	 */
	@XmlTransient
	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	private String originalName;

	/**
	 * 原始文件名称
	 * 
	 * @return
	 */
	@XmlElement(name = "OriginalName")
	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	@Override
	public String toString() {
		return String.format("{file data: %s}", this.getFileName());
	}
}
