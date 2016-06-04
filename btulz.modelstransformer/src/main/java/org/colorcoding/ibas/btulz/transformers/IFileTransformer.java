package org.colorcoding.ibas.btulz.transformers;

import org.colorcoding.ibas.btulz.models.IDomain;

/**
 * 文件转换
 * 
 * @author Niuren.Zhu
 *
 */
public interface IFileTransformer {

	/**
	 * 是否分组输出文件
	 * 
	 * @return
	 */
	boolean isGroupingFile();

	/**
	 * 设置是否分组输出文件
	 * 
	 * @param value
	 * @return
	 */
	void setGroupingFile(boolean value);

	/**
	 * 获取-工作目录
	 * 
	 * @return
	 */
	String getWorkFolder();

	/**
	 * 设置-工作目录
	 * 
	 * @param foder
	 */
	void setWorkFolder(String foder);

	/**
	 * 输入文件
	 * 
	 * @param filePath
	 */
	void input(String filePath);

	/**
	 * 输入文件数组
	 * 
	 * @param filePathes
	 */
	void input(String[] filePathes);

	/**
	 * 输入的模型
	 * 
	 * @param domain
	 */
	void input(IDomain domain);

	/**
	 * 转换为领域模型
	 * 
	 * @return
	 */
	IDomain[] getDomainModels();
}
