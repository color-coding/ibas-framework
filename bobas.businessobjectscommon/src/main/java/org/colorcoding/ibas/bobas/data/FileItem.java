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

	@XmlElement(name = "Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "Folder")
	private boolean isFolder;

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	@XmlElement(name = "File")
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

	@XmlElement(name = "Path")
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 文件数据写入输出流
	 * 
	 * @param out 输出流
	 * @throws IOException
	 */
	public void writeTo(OutputStream out) throws IOException {
		byte[] buffer = new byte[512];
		try (FileInputStream inputStream = new FileInputStream(new File(this.getPath()))) {
			int offset = 0;
			int numRead = 0;
			while (offset < buffer.length
					&& (numRead = inputStream.read(buffer, offset, buffer.length - offset)) >= 0) {
				offset += numRead;
				out.write(buffer);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("{file item: %s}", this.name);
	}
}
