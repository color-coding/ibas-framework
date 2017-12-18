package org.colorcoding.ibas.bobas.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.serialization.Serializable;

/**
 * 文件数据
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "FileData", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "FileData", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class FileData extends Serializable {

	private static final long serialVersionUID = 8583908279754401069L;

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

	/**
	 * 获取文件字节数组
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] getFileBytes() throws IOException {
		FileInputStream inputStream = null;
		try {
			File file = new File(this.getLocation());
			long fileSize = file.length();
			if (fileSize > Integer.MAX_VALUE) {
				throw new IOException(I18N.prop("msg_bobas_invalid_data"));
			}
			inputStream = new FileInputStream(file);
			byte[] buffer = new byte[(int) fileSize];
			int offset = 0;
			int numRead = 0;
			while (offset < buffer.length
					&& (numRead = inputStream.read(buffer, offset, buffer.length - offset)) >= 0) {
				offset += numRead;
			}
			return buffer;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	@Override
	public String toString() {
		return String.format("{file data: %s}", this.getFileName());
	}
}
