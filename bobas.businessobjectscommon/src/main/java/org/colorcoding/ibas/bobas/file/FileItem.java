package org.colorcoding.ibas.bobas.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;
import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 文件项目，描述文件或文件夹的属性
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

	/** 隐藏的基准目录，用于计算相对路径 */
	public String getMaskFolder() {
		return maskFolder;
	}

	public void setMaskFolder(String maskFolder) {
		this.maskFolder = maskFolder;
	}

	@XmlElement(name = "Name")
	private String name;

	/** 文件或文件夹名称 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "IsFolder")
	private boolean isFolder;

	/** 是否文件夹 */
	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	@XmlElement(name = "IsFile")
	private boolean isFile;

	/** 是否文件 */
	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	@XmlElement(name = "ModifyTime")
	private DateTime modifyTime;

	/** 最后修改时间 */
	public DateTime getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(DateTime modifyTime) {
		this.modifyTime = modifyTime;
	}

	private String path;

	/** 文件完整路径 */
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 获取相对于基准目录的路径，路径分隔符统一为斜杠
	 *
	 * @return 相对路径
	 */
	public String relativePath() {
		String path = this.path;
		if (!Strings.isNullOrEmpty(path)) {
			int index = Strings.indexOf(path, this.maskFolder);
			if (index >= 0) {
				path = path.substring(index + this.maskFolder.length());
				if (path.startsWith(File.separator)) {
					path = path.substring(1);
				}
			}
		}
		// 路径符统一
		return Strings.replace(path, Strings.VALUE_BACKSLASH, Strings.VALUE_SLASH);
	}

	@XmlElement(name = "Path")
	String getProxyPath() {
		return this.relativePath();
	}

	void setProxyPath(String path) {
		this.path = path;
	}

	/**
	 * 将文件内容写入输出流
	 *
	 * @param outputStream 输出流
	 * @throws IOException 文件读取或写入失败
	 */
	public void writeTo(OutputStream outputStream) throws IOException {
		Files.writeTo(Files.valueOf(this.getPath()), outputStream);
	}

	@Override
	public String toString() {
		return String.format("{fileItem: %s}", this.name);
	}
}