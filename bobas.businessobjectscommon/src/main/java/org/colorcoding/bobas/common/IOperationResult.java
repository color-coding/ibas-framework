package org.colorcoding.bobas.common;

import org.colorcoding.bobas.util.ArrayList;

/**
 * 操作结果
 * 
 * @author Niuren.Zhu
 *
 * @param
 * 			<P>
 *            返回的对象类型
 */
public interface IOperationResult<P> extends IOperationMessages {
	/**
	 * 返回对象
	 * 
	 */
	ArrayList<P> getResultObjects();

	/**
	 * 操作执行信息
	 * 
	 */
	ArrayList<IOperationInformation> getInformations();

	/**
	 * 复制消息
	 */
	void copy(IOperationResult<?> opRslt);
}
