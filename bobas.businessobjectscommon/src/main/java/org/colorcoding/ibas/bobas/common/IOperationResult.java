package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.data.ArrayList;

/**
 * 操作结果
 * 
 * @author Niuren.Zhu
 *
 * @param <P> 返回的对象类型
 */
public interface IOperationResult<P> extends IOperationMessage {
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
	 * 复制数据
	 * 
	 * @param content 复制内容
	 * @return 当前实例
	 */
	IOperationResult<P> copy(IOperationResult<?> content);
}
