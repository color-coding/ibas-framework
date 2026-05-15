package org.colorcoding.ibas.bobas.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 文件数据，用于传输和存储文件内容
 *
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "FileData", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "FileData", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class FileData extends Serializable implements AutoCloseable {

	private static final long serialVersionUID = 8583908279754401069L;

	public FileData() {
	}

	public FileData(FileItem fileItem) {
		this(new File(fileItem.getPath()));
	}

	public FileData(File file) {
		this();
		this.name = file.getName();
		this.location = file.getPath();
		this.originalName = file.getName();
	}

	public FileData(InputStream stream) {
		this();
		this.stream = stream;
	}

	private String name;

	/** 文件名称 */
	@XmlElement(name = "Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String location;

	/** 文件存储位置 */
	@XmlElement(name = "Location")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private String originalName;

	/** 原始文件名称 */
	@XmlElement(name = "OriginalName")
	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	private InputStream stream;

	/**
	 * 获取文件流，优先返回已设置的流；未设置时根据location尝试打开文件输入流
	 *
	 * @return 文件流，location无效或文件不可读时返回null
	 * @throws IOException 文件打开失败时可能抛出
	 */
	public InputStream getStream() throws IOException {
		if (this.stream == null) {
			if (!Strings.isNullOrEmpty(this.location)) {
				File file = new File(this.location);
				if (file.isFile() && file.exists() && file.canRead()) {
					this.setStream(new FileInputStream(file));
				}
			}
		}
		return stream;
	}

	protected void setStream(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public void close() throws Exception {
		if (this.stream != null) {
			this.stream.close();
			this.stream = null;
		}
	}

	@Override
	public String toString() {
		return String.format("{fileData: %s}",
				Strings.isNullOrEmpty(this.getOriginalName()) ? this.getName() : this.getOriginalName());
	}
}