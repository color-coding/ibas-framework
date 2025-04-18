package org.colorcoding.ibas.bobas.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 文件项目
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "FileItem", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "FileItem", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class FileItem extends Serializable {

	private static final long serialVersionUID = 4673125305186382021L;

	public FileItem() {
	}

	public FileItem(File file) {
		this();
		this.name = file.getName();
		this.isFile = file.isFile();
		this.isFolder = file.isDirectory();
		this.path = file.getPath();
		this.modifyTime = DateTimes.valueOf(file.lastModified());
	}

	private String maskFolder;

	public String getMaskFolder() {
		return maskFolder;
	}

	public void setMaskFolder(String maskFolder) {
		this.maskFolder = maskFolder;
	}

	@XmlElement(name = "Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "IsFolder")
	private boolean isFolder;

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	@XmlElement(name = "IsFile")
	private boolean isFile;

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	@XmlElement(name = "ModifyTime")
	private DateTime modifyTime;

	public DateTime getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(DateTime modifyTime) {
		this.modifyTime = modifyTime;
	}

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@XmlElement(name = "Path")
	String getProxyPath() {
		if (!Strings.isNullOrEmpty(this.maskFolder)) {
			if (!Strings.isNullOrEmpty(this.path)) {
				return this.path.replace(this.maskFolder, "...");
			}
		}
		return path;
	}

	void setProxyPath(String path) {
		this.path = path;
	}

	/**
	 * 文件数据写入输出流
	 * 
	 * @param out 输出流
	 * @throws IOException
	 */
	public void writeTo(OutputStream outputStream) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(new File(this.getPath()))) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("{file item: %s}", this.name);
	}
}
